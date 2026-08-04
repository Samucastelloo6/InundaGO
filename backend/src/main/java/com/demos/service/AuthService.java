package com.demos.service;

import com.demos.model.dto.AuthResponseDTO;
import com.demos.model.dto.LoginRequestDTO;
import com.demos.model.dto.RegisterRequestDTO;

public interface AuthService {
    public AuthResponseDTO login(LoginRequestDTO request);
    public AuthResponseDTO register(RegisterRequestDTO request);

}
