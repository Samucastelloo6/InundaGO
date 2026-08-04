package com.demos.model.dto;

import java.io.Serializable;

import com.demos.repository.entity.Role;

import lombok.Data;


@Data
public class RolDTO implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Long idRol;
	private String nombre;
	
	
	public static RolDTO convertToDTO (Role rol) {

		if (rol == null) {
			return null;
		}
		
		RolDTO rolDTO = new RolDTO();
    	rolDTO.setIdRol(rol.getIdRol());
    	rolDTO.setNombre(rol.getNombreRol());
    
    	return rolDTO;
	}
	
	public static Role convertToEntity(RolDTO rolDTO) {
		
		if (rolDTO == null) {
			return null;
		}

		Role rol = new Role();
		rol.setIdRol(rolDTO.getIdRol());
		rol.setNombreRol(rolDTO.getNombre());
		
		return rol;
	}
}