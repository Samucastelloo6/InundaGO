package com.demos.model.dto;


import com.demos.repository.entity.User;

import lombok.Data;

// RECIBE DATOS DEL FRONTEND

@Data
public class UserRequestDTO {

	private Long idUsuario;
	private String nombre;
	private String apellidos;
	private String email;


	public static User convertToEntity (UserRequestDTO userRequestDTO) {

		if (userRequestDTO == null) {
			return null;
		}

		User user = new User();
    	user.setIdUsuario(userRequestDTO.getIdUsuario());
    	user.setNombre(userRequestDTO.getNombre());
    	user.setApellidos(userRequestDTO.getApellidos());
    	user.setEmail(userRequestDTO.getEmail());

    	return user;
	}

}
