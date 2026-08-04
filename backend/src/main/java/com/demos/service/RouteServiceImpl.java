package com.demos.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.demos.model.dto.AlertDTO;
import com.demos.model.dto.CoordinateDTO;
import com.demos.model.dto.FloodZoneDTO;
import com.demos.model.dto.RouteDTO;
import com.demos.model.dto.RouteMapboxDTO;
import com.demos.model.dto.RouteRequestDTO;
import com.demos.model.dto.RouteResponseDTO;
import com.demos.repository.dao.RouteRepository;
import com.demos.repository.dao.UserRepository;
import com.demos.repository.entity.Route;
import com.demos.repository.entity.User;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;

@Service
public class RouteServiceImpl implements RouteService {

	@Autowired
	private RouteRepository routeRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private MapboxService mapboxService;

	@Autowired
	private AlertService alertService;

	@PersistenceContext
	private EntityManager entityManager;

	private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

	// Clase interna para resultado de la verificación de riesgo
	private static class RiskResult {
		boolean tieneRiesgo;
		String mensaje;

		RiskResult(boolean riesgo, String msj) {
			this.tieneRiesgo = riesgo;
			this.mensaje = msj;
		}
	}	
	
	// Clase interna para coordenadas geocodificadas
	private static class GeocodedAddresses {
		CoordinateDTO coordenadasOrigen;
		CoordinateDTO coordenadasDestino;
		String direccionOrigenFinal;
		String direccionDestinoFinal;

		GeocodedAddresses(CoordinateDTO origen, CoordinateDTO destino, String direccionOrigen, String direccionDestino) {
			this.coordenadasOrigen = origen;
			this.coordenadasDestino = destino;
			this.direccionOrigenFinal = direccionOrigen;
			this.direccionDestinoFinal = direccionDestino;
		}
	}

	@Override
	@Transactional
	public RouteResponseDTO calculateRoute(RouteRequestDTO request, Long idUsuario) {

		// Geocodificar direcciones
		GeocodedAddresses direcciones = geocodificarDirecciones(request);

		// Calcular la ruta
		//RouteMapboxDTO rutaMapboxDTO = mapboxService.calcularRuta(direcciones.coordenadasOrigen, direcciones.coordenadasDestino);
		List<RouteMapboxDTO> rutas = mapboxService.calcularRutasAlternativas(direcciones.coordenadasOrigen, direcciones.coordenadasDestino);

		// Ruta principal
		RouteMapboxDTO rutaPrincipal = rutas.get(0);

		// Convertir a geometrías PostGIS
		Point puntoOrigen = createPoint(direcciones.coordenadasOrigen.getLat(), direcciones.coordenadasOrigen.getLng());
		Point puntoDestino = createPoint(direcciones.coordenadasDestino.getLat(), direcciones.coordenadasDestino.getLng());

		// Convertir a LineString
		LineString lineaRuta = createAlineStringFromCoordinates(rutaPrincipal.getListaCoordenadas());

		// SIMULACRO
		// Verificar riesgo de inundación solo si llueve en ruta principal
		RiskResult resultado = checkFloodRiskWithWeather(lineaRuta);
		String mensaje = resultado.mensaje;

		// Buscar ruta alternativa solo si hay riesgo en ruta principal
		RouteDTO rutaAlternativaDTO = null;

		if (resultado.tieneRiesgo && rutas.size() > 1) {
			boolean rutaAlternativaEncontrada = false;

			for (int i = 1; i < rutas.size(); i++) {

				RouteMapboxDTO opcionIndice = rutas.get(i);
				LineString opcionLinea = createAlineStringFromCoordinates(opcionIndice.getListaCoordenadas());

				// SIMULACRO
				RiskResult opcionRiesgo = checkFloodRiskWithWeather(opcionLinea);
			
				if (!opcionRiesgo.tieneRiesgo) {
					Route rutaAlternativaSave = saveRoute(direcciones, puntoOrigen, puntoDestino, opcionLinea, opcionIndice, false, idUsuario);
					rutaAlternativaDTO = RouteDTO.convertToDTO(rutaAlternativaSave);
					rutaAlternativaEncontrada = true;
				}
			}
		}

		// Guardar ruta principal en BBDD
		Route rutaGuardada = saveRoute(direcciones, puntoOrigen, puntoDestino, lineaRuta, rutaPrincipal, resultado.tieneRiesgo, idUsuario);

		// Obtener alerta clima destino
		AlertDTO alertaClima = alertService.obtenerAlertaMeteo(direcciones.coordenadasDestino.getLat(),
				direcciones.coordenadasDestino.getLng());

		return RouteResponseDTO.convertToDTO(rutaGuardada, mensaje, alertaClima, rutaAlternativaDTO);

	}

	@Override
	public List<FloodZoneDTO> getFloodZonesInRoute(LineString lineaRuta) {
		
		String sql = """
			SELECT 
				ST_Y(ST_Centroid(z.geom)) as lat,
				ST_X(ST_Centroid(z.geom)) as lng
			FROM zonas_inundacion z
			WHERE ST_Intersects(z.geom, ST_GeomFromText(:ruta, 4326))
		""";

		try {

			Query query = entityManager.createNativeQuery(sql);
			query.setParameter("ruta", lineaRuta.toText());

			@SuppressWarnings("unchecked") // Desactiva el warning
			List<Object[]> resultados = query.getResultList();

			List<FloodZoneDTO> zonas = new ArrayList<>();

			for (Object[] fila : resultados) {
				Double lat = ((Number) fila[0]).doubleValue();
				Double lng = ((Number) fila[1]).doubleValue();

				zonas.add(FloodZoneDTO.builder()
					.lat(lat)
					.lng(lng)
					.build());
			}

			return zonas;

		} catch (Exception ex) {
			System.err.println("Error al obtener zonas inundables: " + ex.getMessage());
			return new ArrayList<>();
		}

	}


	@Override
	public List<RouteDTO> getRutasByUsuario(Long idUsuario) {

		List<Route> listaRutas = routeRepository.findByUsuarioIdUsuarioOrderByFechaBusquedaDesc(idUsuario);
		List<RouteDTO> listaRutasDTO = listaRutas.stream()
				.map(r -> RouteDTO.convertToDTO(r))
				.collect(Collectors.toList());

		return listaRutasDTO;

	}


	@Override
	public void deleteRutasByUsuario(Long idUsuario) {
		routeRepository.deleteByUsuarioIdUsuario(idUsuario);
	}


	@Override
	public RouteDTO getRutaById(Long idRuta) {
		Route ruta = routeRepository.findById(idRuta).orElse(null);

		return RouteDTO.convertToDTO(ruta);
	}



	//--- MÉTODOS PRIVADOS--

	// Geocodificar direcciones
	private GeocodedAddresses geocodificarDirecciones(RouteRequestDTO request) {

		CoordinateDTO coordenadasOrigen, coordenadasDestino;
		String direccionOrigenFinal, direccionDestinoFinal;

		// Si usuario pasa coordenadas (hace click en mapa) o autocompleta -> ORIGEN
		if (request.getLatOrigen() != null && request.getLngOrigen() != null) {
			coordenadasOrigen = CoordinateDTO.builder()
					.lat(request.getLatOrigen())
					.lng(request.getLngOrigen())
					.build();

			// Para guardar en bbdd, agregamos las coordenadas como texto a direccionOrigen
			// si no hay nombre
			direccionOrigenFinal = request.getDireccionOrigen() != null ? request.getDireccionOrigen()
					: "Lat: " + request.getLatOrigen() + ", Lng: " + request.getLngOrigen();

		} else {
			coordenadasOrigen = mapboxService.geocodificar(request.getDireccionOrigen());
			direccionOrigenFinal = request.getDireccionOrigen();
		}


		// Si usuario pasa coordenadas (hace click en mapa) -> DESTINO
		if (request.getLatDestino() != null && request.getLngDestino() != null) {
			coordenadasDestino = CoordinateDTO.builder()
					.lat(request.getLatDestino())
					.lng(request.getLngDestino())
					.build();

			// Agregamos las coordenadas como texto a direccionDestino si no hay dirección
			// de autocompletado
			direccionDestinoFinal = request.getDireccionDestino() != null ? request.getDireccionDestino()
					: "Lat: " + request.getLatDestino() + ", Lng: " + request.getLngDestino();

		} else {
			coordenadasDestino = mapboxService.geocodificar(request.getDireccionDestino());
			direccionDestinoFinal = request.getDireccionDestino();
		}

		return new GeocodedAddresses(coordenadasOrigen, coordenadasDestino, direccionOrigenFinal, direccionDestinoFinal);
	}

	// Convierte la lista de coordenadas a LineString de PostGIS
	private LineString createAlineStringFromCoordinates(List<CoordinateDTO> coordenadas) {

		double[][] coords = coordenadas.stream()
				.map(c -> new double[] { c.getLng(), c.getLat() })
				.toArray(size -> new double[size][]);

		return createLineString(coords);
	}

	// Métodos para geometría
	private Point createPoint(Double lat, Double lng) {
		return geometryFactory.createPoint(new Coordinate(lng, lat));
	}

	private LineString createLineString(double[][] coordenadas) {
		Coordinate[] coordinates = new Coordinate[coordenadas.length];

		for (int i = 0; i < coordenadas.length; i++) {
			coordinates[i] = new Coordinate(coordenadas[i][0], coordenadas[i][1]);
		}

		return geometryFactory.createLineString(coordinates);
	}

	/* // Búsqueda de ruta alternativa segura
	private RouteDTO findSafeAlternativeRoute(GeocodedAddresses direcciones, Point puntoOrigen, Point puntoDestino, Long idUsuario) {

		List<RouteMapboxDTO> rutasAlternativas = mapboxService.calcularRutasAlternativas(direcciones.coordenadasOrigen, direcciones.coordenadasDestino);

		// Revisar alternativas (se omite la principal)
		for (int i = 1; i < rutasAlternativas.size(); i++) {

			RouteMapboxDTO opcionRuta = rutasAlternativas.get(i);
			LineString opcionLinea = createAlineStringFromCoordinates(opcionRuta.getListaCoordenadas());
			RiskResult opcionRiesgo = checkFloodRiskWithWeather(opcionLinea);

			// Si encontramos alguna sin riesgo, guarda
			if (!opcionRiesgo.tieneRiesgo) {

				Route rutaAlternativaGuardada = saveRoute(direcciones, puntoOrigen, puntoDestino, opcionLinea, opcionRuta, false, idUsuario);
			
				return RouteDTO.convertToDTO(rutaAlternativaGuardada);
			}

		}
		return null;
	} */

	// Crear ruta para guardar
	private Route createRoute (GeocodedAddresses direcciones, Point puntoOrigen, Point puntoDestino, LineString lineaRuta, 
		RouteMapboxDTO rutaMapboxDTO, boolean tieneRiesgo, User usuario) {

			Route route = new Route();
    		route.setDireccionOrigen(direcciones.direccionOrigenFinal);
    		route.setDireccionDestino(direcciones.direccionDestinoFinal);
    		route.setPuntoOrigen(puntoOrigen);
    		route.setPuntoDestino(puntoDestino);
    		route.setLineaRuta(lineaRuta);
    		route.setDistancia(rutaMapboxDTO.getDistancia());
    		route.setDuracion(rutaMapboxDTO.getDuracion());
    		route.setRiesgo(tieneRiesgo);
    		route.setFechaBusqueda(LocalDateTime.now());
   			 route.setUsuario(usuario);
    
    		return route;
	}

	// Guardar ruta
	private Route saveRoute(GeocodedAddresses direcciones, Point puntoOrigen, Point puntoDestino, LineString lineaRuta, 
		RouteMapboxDTO rutaMapboxDTO, boolean tieneRiesgo, Long idUsuario) {
		
		// Buscamos usuario
		User usuario = userRepository.findById(idUsuario)
				.orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

		Route ruta = createRoute(direcciones, puntoOrigen, puntoDestino, lineaRuta, rutaMapboxDTO, tieneRiesgo, usuario);
		
		return routeRepository.save(ruta);

		}

	 
	private RiskResult checkFloodRiskWithWeather (LineString lineaRuta) {

		// Obtener zonas inundables
		List<FloodZoneDTO> zonasInundables = getFloodZonesInRoute(lineaRuta);

		System.out.println("Zonas inundables encontradas: " + zonasInundables.size()); //

		if (zonasInundables.isEmpty()) {
			return new RiskResult(false, "Ruta segura");
		}

		// SIMULACRO LLUVIA
		/* if (simular) {
        return new RiskResult(true, 
            "DEMO - Ruta con riesgo: zona inundable con lluvia intensa simulada (nivel ALTA)");
    	} */

		// Creo lista sincronizada para los resultados de ls hilos
		List<AlertDTO> alertas = Collections.synchronizedList(new ArrayList<>());

		// Creo un hilo por cada zona
		List<Thread> listaHilosZonas = new ArrayList<>(); 

		for (FloodZoneDTO zona : zonasInundables) {

			Thread hilo = new Thread(() -> {
				AlertDTO alerta = alertService.obtenerAlertaMeteo(zona.getLat(), zona.getLng());
				if (alerta != null) {
					alertas.add(alerta);
				}
			});
			listaHilosZonas.add(hilo);
			hilo.start();
		}	

		// Bucle para esperar a todos los hilos
		for (Thread hilo : listaHilosZonas) {
			
			try {
				hilo.join();
			} catch (InterruptedException ex) {
				Thread.currentThread().interrupt();
			}
		}
		
		// Comprobar riesgo en las alertas
		for (AlertDTO alerta : alertas) {
			if (!"BAJA".equalsIgnoreCase(alerta.getNivel())) {

				String mensajeAlerta = String.format("Ruta con riesgo: zona inundable con %.1fmm de lluvia detectada (nivel %s)", 
					alerta.getMmLluvia(), alerta.getNivel());
				
				return new RiskResult(true, mensajeAlerta);	
			}				
		}
		return new RiskResult(false, "Advertencia: La ruta atraviesa una zona inundable (sin lluvia abundante)");
	}
}
