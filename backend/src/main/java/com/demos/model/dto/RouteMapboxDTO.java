package com.demos.model.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor 
@AllArgsConstructor
public class RouteMapboxDTO {

    private List<CoordinateDTO> listaCoordenadas; // array de puntos
    private Double distancia; // km
    private Integer duracion; // segundos
    
}
