package com.demos.util;

import org.springframework.stereotype.Component;

import com.demos.model.dto.RegisterRequestDTO;
import com.demos.repository.dao.UserRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RegisterRequestValidator {

    // Se podrian añadir condiciones en los campos, de momento no hago nada, solo compruebo

    private final UserRepository userRepository;

    public void validate(RegisterRequestDTO request) {
        
        if (request.getNombre() == null || request.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }

        if (request.getApellidos() == null || request.getApellidos().trim().isEmpty()) {
            throw new IllegalArgumentException("Los apellidos son obligatorios");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("El email ya está registrado");
        } else {
           boolean emailValidate = isValidEmail(request.getEmail());
           if (emailValidate == false) {
                throw new IllegalArgumentException("Introduce un email válido");
           }
        }

        if (request.getPassword() == null || request.getPassword().isEmpty()) {
            throw new RuntimeException("La contraseña es obligatoria");
        }
    }

    private boolean isValidEmail(String email) {

        String regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";

        return email.matches(regex);
    }
    
}
