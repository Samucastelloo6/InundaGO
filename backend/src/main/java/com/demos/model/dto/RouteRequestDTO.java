package com.demos.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


// DTO que recibe los datos del frontend
@Data
@Builder
@NoArgsConstructor 
@AllArgsConstructor
public class RouteRequestDTO {

    private String direccionOrigen;
    private String direccionDestino;
    private Double latOrigen;
    private Double lngOrigen;
    private Double latDestino;
    private Double lngDestino;
}
