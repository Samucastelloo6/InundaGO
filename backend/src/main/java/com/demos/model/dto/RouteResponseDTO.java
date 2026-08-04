package com.demos.model.dto;

import java.io.Serializable;
import java.util.List;

import com.demos.repository.entity.Route;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


// DTO para devolver resultado del cálculo al frontend
@Data
@Builder
@NoArgsConstructor 
@AllArgsConstructor
public class RouteResponseDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long idRuta;
    private Double distancia;
    private Integer duracion;
    private Boolean riesgo;
    private String mensaje;
    private List<CoordinateDTO> coordenadasRuta;
    private AlertDTO alertaClima;
    private RouteDTO rutaAlternativa;


    public static RouteResponseDTO convertToDTO(Route ruta, String mensaje, AlertDTO alertaClima) {

        if (ruta == null) {
            return null;
        }

        List<CoordinateDTO> coordenadas = null;
        if (ruta.getLineaRuta() != null) {
            coordenadas = java.util.Arrays.stream(ruta.getLineaRuta().getCoordinates())
                .map(coord -> CoordinateDTO.builder()
                    .lat(coord.getY())
                    .lng(coord.getX())
                    .build())
                .toList();
        }
        
        return RouteResponseDTO.builder()
            .idRuta(ruta.getIdRuta())
            .distancia(ruta.getDistancia())
            .duracion(ruta.getDuracion())
            .riesgo(ruta.getRiesgo())
            .mensaje(mensaje)
            .coordenadasRuta(coordenadas)
            .alertaClima(alertaClima)
            .rutaAlternativa(null)
            .build();
    } 

    public static RouteResponseDTO convertToDTO(Route ruta, String mensaje, AlertDTO alertaClima, RouteDTO rutaAlernativa) {

        RouteResponseDTO dto = convertToDTO(ruta, mensaje, alertaClima);
        dto.setRutaAlternativa(rutaAlernativa);

        return dto;
    }
    
}
