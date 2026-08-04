package com.demos.model.dto;

import java.time.LocalDate;


import com.demos.repository.entity.Role;
import com.demos.repository.entity.User;

import lombok.Data;

@Data
public class RegisterRequestDTO {

    private String nombre;
    private String apellidos;
    private String email;
    private String password;
    //LocalDate fechaLogin;

    // Método convert para registro, con contraseña y rol
    public static User convertToEntityRegister(RegisterRequestDTO request, String encodedPassword, Role role) {

        if (request == null) {
            return null;
        }

        User user = new User();
        user.setNombre(request.getNombre());
        user.setApellidos(request.getApellidos());
        user.setEmail(request.getEmail());
        user.setPassword(encodedPassword);
        user.setFechaLogin(LocalDate.now());
        user.setRol(role);

        return user;
    }
}
