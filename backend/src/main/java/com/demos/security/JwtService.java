package com.demos.security;

import java.util.function.Function;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;



@Service
public interface JwtService {

    public String getToken(UserDetails user);

    public String getUsernameFromToken(String token);

    public boolean isTokenValid(String token, UserDetails userDetails);

    public <T> T getClaim(String token, Function<Claims, T> claimsResolver);

}
