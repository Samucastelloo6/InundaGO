package com.demos.model.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor 
@AllArgsConstructor
public class FloodZoneDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    // Coordenadas centroide
    private Double lat;
    private Double lng;
    
}
