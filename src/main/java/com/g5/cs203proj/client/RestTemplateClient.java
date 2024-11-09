package com.g5.cs203proj.client;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.g5.cs203proj.entity.Player;

@Component
public class RestTemplateClient {

    private final RestTemplate template;
 
    public RestTemplateClient(RestTemplateBuilder restTemplateBuilder) {
        this.template = restTemplateBuilder
                .basicAuthentication("Player1", "Password1")
                .build();
    }
}
