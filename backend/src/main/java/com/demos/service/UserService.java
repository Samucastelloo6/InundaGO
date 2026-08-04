package com.demos.service;

import com.demos.model.dto.UserDTO;
import com.demos.model.dto.UserRequestDTO;
import com.demos.model.dto.UserResponseDTO;

public interface UserService {

	public UserResponseDTO updateUser(UserRequestDTO userRequestDTO);
	public UserDTO getUser(Long id);
	public UserDTO getUserByEmail(String email);
	
}
