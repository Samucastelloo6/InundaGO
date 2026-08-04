package com.demos.service;

import java.util.List;

import com.demos.model.dto.FloodZoneDTO;
import com.demos.model.dto.RouteDTO;
import com.demos.model.dto.RouteRequestDTO;
import com.demos.model.dto.RouteResponseDTO;

import org.locationtech.jts.geom.LineString;

public interface RouteService {

	// Calcular ruta y verificar riesgo
	public RouteResponseDTO calculateRoute(RouteRequestDTO request, Long idUsuario);
	
	// Obtener todas las rutas de un usuario concreto
	public List<RouteDTO> getRutasByUsuario(Long idUsuario);

	// Obtener todas las rutas CON riesgo
	// public List<RouteDTO> getRutasConRiesgo(Long idUsuario);

	// Obtener todas las rutas SIN riesgo
	// public List<RouteDTO> getRutasSinRiesgo(Long idUsuario);

	// Borrar todas las rutas de un usuario en concreto
	public void deleteRutasByUsuario(Long idUsuario);

	// Obtener ruta por idRuta
	public RouteDTO getRutaById(Long idRuta);

	// Verificar el riesgo de la ruta
	//public boolean checkFloodRisk(LineString lineaRuta);

	// Devuelve la lista de zonas inundables que cruza la ruta
	List<FloodZoneDTO> getFloodZonesInRoute(LineString ruta);
}
