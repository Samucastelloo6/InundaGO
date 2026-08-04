package com.demos.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.demos.Config.EmailNotFoundException;
import com.demos.model.dto.AuthResponseDTO;
import com.demos.model.dto.LoginRequestDTO;
import com.demos.model.dto.RegisterRequestDTO;
import com.demos.repository.dao.RoleRepository;
import com.demos.repository.dao.UserRepository;
import com.demos.repository.entity.Role;
import com.demos.repository.entity.User;
import com.demos.security.JwtService;
import com.demos.util.RegisterRequestValidator;


@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private RegisterRequestValidator registerValidator;

    public AuthResponseDTO login(LoginRequestDTO request) {
        userRepository.findByEmail(request.getEmail()).orElseThrow(()-> new EmailNotFoundException("No existe ninguna cuenta con el email introducido"));

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        UserDetails user = userRepository.findByEmail(request.getEmail()).orElseThrow();
        String token = jwtService.getToken(user);

        return AuthResponseDTO.builder()
            .accesoToken(token)
            .build();
    }

    public AuthResponseDTO register(RegisterRequestDTO request) {

        registerValidator.validate(request);

        Role roleUser = roleRepository.findByNombreRol("ROLE_USER").orElseThrow(() -> new RuntimeException("El rol no existe")); 
        
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        User user = RegisterRequestDTO.convertToEntityRegister(request, encodedPassword, roleUser);

        userRepository.save(user);
        
        return AuthResponseDTO.builder()
        .accesoToken(jwtService.getToken(user))
        .build();
    }

}
