package com.g5.cs203proj.security;

import java.io.IOException;
import java.util.*;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;  // Update this import
import jakarta.servlet.http.HttpServletResponse;  // Update this import
import org.springframework.security.core.AuthenticationException;
import jakarta.servlet.ServletException;
import org.springframework.http.MediaType;



import com.fasterxml.jackson.databind.ObjectMapper;


@Component
//use a custom AuthenticationEntryPoint to regain control over my the UsernameNotFoundException (in PlayerController)
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        Map<String, Object> body = new HashMap<>();
        body.put("status", HttpStatus.UNAUTHORIZED.value());
        body.put("error", "Unauthorized");
        
        if (authException instanceof UsernameNotFoundException) {
            body.put("message", "Player not found: " + authException.getMessage());
        } else {
            body.put("message", authException.getMessage() + " or User not created yet.");
            
        }

        new ObjectMapper().writeValue(response.getOutputStream(), body);
    }
    
}
