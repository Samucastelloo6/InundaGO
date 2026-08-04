package com.demos.repository.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "alerta")
public class Alert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_alerta")
    private Integer idAlerta;

    @Column(name = "nivel", nullable = false)
    private String nivel;

    @Column(name = "color", nullable = false)
    private String color;

    @Column(name = "mm_lluvia_min", nullable = false, precision = 5, scale = 2)
    private BigDecimal mmLluviaMin;

    @Column(name = "mm_lluvia_max", nullable = false, precision = 5, scale = 2)
    private BigDecimal mmLluviaMax;

    @Column(name = "descripcion")
    private String descripcion;
}
