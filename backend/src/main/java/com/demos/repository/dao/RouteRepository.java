package com.demos.repository.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.demos.repository.entity.Route;

import jakarta.transaction.Transactional;


@Repository
public interface RouteRepository extends JpaRepository<Route, Long> {

    // SELECT * FROM ruta WHERE id_usuario = ?
    List<Route> findByUsuarioIdUsuarioOrderByFechaBusquedaDesc(Long idUsuario);

    // SELECT * FROM ruta WHERE id_usuario = ? AND riesgo = true
    List<Route> findByUsuarioIdUsuarioAndRiesgoTrueOrderByFechaBusquedaDesc(Long idUsuario);

    // SELECT * FROM ruta WHERE id_usuario = ? AND riesgo = false
    List<Route> findByUsuarioIdUsuarioAndRiesgoFalseOrderByFechaBusquedaDesc(Long idUsuario);

    // DELETE FROM ruta WHERE id_usuario = ?
    @Transactional
    void deleteByUsuarioIdUsuario(Long idUsuario);
    
}
