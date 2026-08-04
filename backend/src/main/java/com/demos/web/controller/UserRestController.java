package com.demos.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.demos.model.dto.UserDTO;
import com.demos.model.dto.UserRequestDTO;
import com.demos.model.dto.UserResponseDTO;
import com.demos.service.UserService;


@RestController
@RequestMapping("/api/v1/user")

public class UserRestController {
	
	@Autowired
	private UserService userService;
	
	// Acceder a usuario por id, solo administrador
	@GetMapping(value = "{id}")
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	public ResponseEntity<UserDTO> getUser(@PathVariable Long id) {
		UserDTO userDTO = userService.getUser(id);
		if (userDTO == null) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(userDTO);
	}

	@GetMapping("me")
	public ResponseEntity<UserDTO> me(Authentication auth) {
		String email = auth.getName();
		UserDTO userDTO = userService.getUserByEmail(email);

		if (userDTO == null) {
			return ResponseEntity.notFound().build();
		}

		return ResponseEntity.ok(userDTO);
	}
	
	@PutMapping()
	public ResponseEntity<UserResponseDTO> updateUser(@RequestBody UserRequestDTO userRequestDTO) {
		
		return ResponseEntity.ok(userService.updateUser(userRequestDTO));
	}

}
