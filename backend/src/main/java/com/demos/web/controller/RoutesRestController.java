package com.demos.web.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.demos.model.dto.RouteDTO;
import com.demos.model.dto.RouteRequestDTO;
import com.demos.model.dto.RouteResponseDTO;
import com.demos.repository.dao.UserRepository;
import com.demos.repository.entity.User;
import com.demos.service.RouteService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;




@RestController
@RequestMapping("/api/v1/routes")
public class RoutesRestController {

    @Autowired
    private RouteService routeService;
    @Autowired
    private UserRepository userRepository;

    @PostMapping("/calculate")
    public ResponseEntity<RouteResponseDTO> calculateRoute(@RequestBody RouteRequestDTO request, Authentication auth) {

        String email = auth.getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        RouteResponseDTO response = routeService.calculateRoute(request, user.getIdUsuario());

        return ResponseEntity.ok(response);
    }


    @GetMapping("/me")
    public ResponseEntity<List<RouteDTO>> getMisRutas(Authentication auth) {

        String email = auth.getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        List<RouteDTO> listaRutasDTO = routeService.getRutasByUsuario(user.getIdUsuario());

        return ResponseEntity.ok(listaRutasDTO);
    }


    @GetMapping("/{id}")
    public ResponseEntity<RouteDTO> getRutaById(@PathVariable Long id, Authentication auth) {

        String email = auth.getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        RouteDTO rutaDTO = routeService.getRutaById(id);

        if (rutaDTO == null) {
            return ResponseEntity.notFound().build();
        }

        if (!rutaDTO.getIdUsuario().equals(user.getIdUsuario())) {
            return ResponseEntity.status(403).build();
        }

        return ResponseEntity.ok(rutaDTO);
    }

    
    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteMisRutas(Authentication auth) {
        String email = auth.getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        routeService.deleteRutasByUsuario(user.getIdUsuario());

        return ResponseEntity.noContent().build();
    }
}
    
    
    


