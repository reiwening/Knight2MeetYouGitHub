package com.g5.cs203proj.controller;

import com.g5.cs203proj.service.ChatbotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/chess")
public class ChatbotController {

    @Autowired
    private ChatbotService chatbotService;

    @PostMapping("/advice")
    public String getChessAdvice(@RequestBody Map<String, String> requestBody) {
        String userInput = requestBody.get("userInput");
        String personality = requestBody.get("personality");
        return chatbotService.getChessAdvice(userInput, personality);
    }
}
