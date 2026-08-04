package com.demos.model.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import com.demos.repository.entity.Route;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


// DTO para devolver las rutas guardadas
@Data
@Builder
@NoArgsConstructor 
@AllArgsConstructor
public class RouteDTO implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Long idRuta;
	private String direccionOrigen;
	private String direccionDestino;
	private Double distancia;
	private Integer duracion;
	private LocalDateTime fechaBusqueda;
	private Boolean riesgo;
	private Long idUsuario;
	private List<CoordinateDTO> coordenadasRuta;
	
	
	public static RouteDTO convertToDTO (Route ruta) {

		if (ruta == null) {
			return null;
		}
		
		// Convertimos LineString a List
		List<CoordinateDTO> coordenadas = null;

		if (ruta.getLineaRuta() != null) {
			coordenadas = java.util.Arrays.stream(ruta.getLineaRuta().getCoordinates())
				.map(c -> CoordinateDTO.builder()
					.lat(c.getY())
					.lng(c.getX())
					.build())
				.toList();
		}
		
		return RouteDTO.builder()
			.idRuta(ruta.getIdRuta())
			.direccionOrigen(ruta.getDireccionOrigen())
			.direccionDestino(ruta.getDireccionDestino())
			.distancia(ruta.getDistancia())
			.duracion(ruta.getDuracion())
			.riesgo(ruta.getRiesgo())
			.fechaBusqueda(ruta.getFechaBusqueda())
			.idUsuario(ruta.getUsuario().getIdUsuario())
			.coordenadasRuta(coordenadas)
			.build();
	}
	
	/*public static Route convertToEntity(RutaDTO rutaDTO) {
		
		Route ruta = new Route();
		ruta.setIdRuta(rutaDTO.getIdRuta());
		ruta.setOrigen(rutaDTO.getOrigen());
		ruta.setDestino(rutaDTO.getDestino());
		ruta.setLatOrigen(rutaDTO.getLatOrigen());
		ruta.setLonOrigen(rutaDTO.getLonOrigen());
		ruta.setLatDestino(rutaDTO.getLatDestino());
		ruta.setLonDestino(rutaDTO.getLonDestino());
		ruta.setDistancia(rutaDTO.getDistancia());
		ruta.setDuracion(rutaDTO.getDuracion());
		ruta.setFecha(rutaDTO.getFecha());
		ruta.setRiesgo(rutaDTO.getRiesgo());
		
		return ruta;
	}*/

}
