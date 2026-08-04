package com.demos.model.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


// DTO para representar un punto (lat, lng)
@Data
@Builder
@NoArgsConstructor 
@AllArgsConstructor
public class CoordinateDTO implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private Double lat;
    private Double lng;
}
