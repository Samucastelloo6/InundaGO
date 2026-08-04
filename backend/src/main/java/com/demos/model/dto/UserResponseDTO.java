package com.demos.model.dto;

import java.io.Serializable;

import com.demos.repository.entity.User;

import lombok.Data;

// ENVIA DATOS AL FRONTEND
@Data
public class UserResponseDTO implements Serializable {

	private static final long serialVersionUID = 1L;

    private Long idUsuario;
    private String nombre;
    private String apellidos;
    private String email;
    private String rolNombre;
    

    public static UserResponseDTO convertToDTO (User user) {

        if (user == null) {
			return null;
		}

		UserResponseDTO userResponseDTO = new UserResponseDTO();
    	userResponseDTO.setIdUsuario(user.getIdUsuario());
    	userResponseDTO.setNombre(user.getNombre());
    	userResponseDTO.setApellidos(user.getApellidos());
    	userResponseDTO.setEmail(user.getEmail());
    	userResponseDTO.setRolNombre(user.getRol().getNombreRol());

    	return userResponseDTO;

    }
}
