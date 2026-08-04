package com.demos.repository.dao;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.demos.repository.entity.Alert;

@Repository
public interface AlertRepository extends JpaRepository<Alert, Integer> {

    // Buscar el nivel de alerta según lluvia (en mm)
    @Query("SELECT a FROM Alert a WHERE :mmLluvia BETWEEN a.mmLluviaMin AND a.mmLluviaMax")
    Optional<Alert> findByMmLluvia(@Param("mmLluvia") BigDecimal mmLluvia);
    
}
