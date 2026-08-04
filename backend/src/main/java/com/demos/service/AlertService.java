package com.demos.service;

import com.demos.model.dto.AlertDTO;

public interface AlertService {

    // Obtener alerta por coordenadas
    public AlertDTO obtenerAlertaMeteo(Double lat, Double lng);
    
}
