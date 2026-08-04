package com.demos.service;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import com.demos.model.dto.UserDTO;
import com.demos.model.dto.UserRequestDTO;
import com.demos.model.dto.UserResponseDTO;
import com.demos.repository.dao.UserRepository;
import com.demos.repository.entity.User;

import jakarta.transaction.Transactional;

@Service
public class UserServiceImpl implements UserService {
	
	@Autowired 
	private UserRepository userRepository;

	
	@Transactional
	public UserResponseDTO updateUser(UserRequestDTO userRequestDTO) {

		User user = userRepository.findById(userRequestDTO.getIdUsuario()).orElseThrow(()-> new RuntimeException("Usuario no encontrado"));

		
		if (userRequestDTO.getNombre() != null) {
			user.setNombre(userRequestDTO.getNombre());
		}
		if (userRequestDTO.getApellidos() != null) {
			user.setApellidos(userRequestDTO.getApellidos());
		}
		if (userRequestDTO.getEmail() != null) {
			user.setEmail(userRequestDTO.getEmail());
		}

		User updateUser = userRepository.save(user);

		return UserResponseDTO.convertToDTO(updateUser);
		
	}

	@Override
	public UserDTO getUser(Long id) {

		User user = userRepository.findById(id).orElse(null);

		return UserDTO.convertToDTO(user);
	}

	@Override
	public UserDTO getUserByEmail(String email) {
		
		User user = userRepository.findByEmail(email).orElse(null);
		
		return UserDTO.convertToDTO(user);	
		
	}
	
	

}
