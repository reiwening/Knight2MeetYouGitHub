package com.g5.cs203proj.security;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest; 
import jakarta.servlet.http.HttpServletResponse; 

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;

// To regain control over my AccessDeniedException 
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, 
                       AccessDeniedException accessDeniedException) throws IOException {

        // Set the response status
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);

        // Create a custom response body
        Map<String, Object> body = new HashMap<>();
        body.put("error", "You are not authorised for this.");
        body.put("details", accessDeniedException.getMessage());

        // Convert the response body to JSON and write it to the response
        ObjectMapper mapper = new ObjectMapper();
        response.getOutputStream().println(mapper.writeValueAsString(body));
    }
}
