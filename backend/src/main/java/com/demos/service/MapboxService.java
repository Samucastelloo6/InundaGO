package com.demos.service;

import java.util.List;

import com.demos.model.dto.CoordinateDTO;
import com.demos.model.dto.RouteMapboxDTO;

public interface MapboxService {

    public CoordinateDTO geocodificar(String direccion);

    public RouteMapboxDTO calcularRuta(CoordinateDTO origen, CoordinateDTO destino);

    public List<RouteMapboxDTO> calcularRutasAlternativas(CoordinateDTO origen, CoordinateDTO destino);
    
}
