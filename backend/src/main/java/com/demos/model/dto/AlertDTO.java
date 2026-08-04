package com.demos.model.dto;

import java.io.Serializable;
import java.math.BigDecimal;

import com.demos.repository.entity.Alert;

import lombok.Data;

@Data
public class AlertDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String nivel;
    private String color;
    private BigDecimal mmLluvia;
    private String descripcion;


    public static AlertDTO convertToDTO(Alert alerta, BigDecimal mmLluviaActual) {

        if (alerta == null) {
            return null;
        }

        AlertDTO alertDTO = new AlertDTO();
        alertDTO.setNivel(alerta.getNivel());
        alertDTO.setColor(alerta.getColor());
        alertDTO.setMmLluvia(mmLluviaActual);
        alertDTO.setDescripcion(alerta.getDescripcion());
        
        return alertDTO;
    }
    
}
