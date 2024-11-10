package com.g5.cs203proj.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.json.JSONObject;

@Service
public class ChatbotService {
    
    @Value("${openai.api.key}")
    private String openAiApiKey;

    public String getChessAdvice(String userInput, String personality) {
        String apiUrl = "https://api.openai.com/v1/chat/completions";

        // Set up headers
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + openAiApiKey);
        headers.set("Content-Type", "application/json");

        // Create request body with personality context
        JSONObject requestBody = new JSONObject();
        requestBody.put("model", "gpt-3.5-turbo");
        requestBody.put("messages", new JSONObject[]{
            new JSONObject().put("role", "system").put("content", 
                "You are " + personality + ", give interesting replies that fans will appreciate."),
            new JSONObject().put("role", "user").put("content", userInput)
        });
        requestBody.put("temperature", 0.2);

        // Create HTTP entity with headers and body
        HttpEntity<String> entity = new HttpEntity<>(requestBody.toString(), headers);

        // Send request
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.POST, entity, String.class);

        // Extract and return the response
        return new JSONObject(response.getBody())
            .getJSONArray("choices")
            .getJSONObject(0)
            .getJSONObject("message")
            .getString("content");
    }
}
