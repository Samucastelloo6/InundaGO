package com.demos.model.dto;

import java.io.Serializable;
import java.time.LocalDate;

import com.demos.repository.entity.User;

import lombok.Data;
import lombok.ToString;

@Data
public class UserDTO implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Long idUsuario;
	private String nombre;
	private String apellidos;
	private LocalDate fechaLogin;
	private String email;
	
	@ToString.Exclude
	private RolDTO rolDTO;
	
	
	public static UserDTO convertToDTO(User user) {
		if (user == null) {
			return null;
		}

		UserDTO userDTO = new UserDTO();
    	userDTO.setIdUsuario(user.getIdUsuario());
    	userDTO.setNombre(user.getNombre());
    	userDTO.setApellidos(user.getApellidos());
    	userDTO.setEmail(user.getEmail());
    	userDTO.setFechaLogin(user.getFechaLogin());
    	userDTO.setRolDTO(RolDTO.convertToDTO(user.getRol()));

    	return userDTO;
	}

	public static User convertToEntity(UserDTO userDTO) {
		if (userDTO == null) {
			return null;
		}

		User user = new User();
    	user.setIdUsuario(userDTO.getIdUsuario());
    	user.setNombre(userDTO.getNombre());
    	user.setApellidos(userDTO.getApellidos());
    	user.setEmail(userDTO.getEmail());
    	user.setFechaLogin(userDTO.getFechaLogin());
    	user.setRol(RolDTO.convertToEntity(userDTO.getRolDTO()));

    	return user;
	}
	
}
