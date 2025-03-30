package com.aiscaffolder.aiscaffolder.application.services;

import com.aiscaffolder.aiscaffolder.domain.entities.PromptInstructionConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class ApiGenerationService {
    @Value("${api-keys.gemini.key}")
    private String geminiKey;
    private final String GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=";
    private final RestTemplate restTemplate = new RestTemplate();
    @Autowired
    private PromptInstructionService promptInstructionService;

    public Map<String, Object> generateAIJson(String prompt) {
        Map<String, Object> requestBody = new HashMap<>();
        Map<String, Object> systemInstructionParts = new HashMap<>();
        Map<String, String> textInstruction = new HashMap<>();
        Map<String, Object> contents = new HashMap<>();
        Map<String, Object> promptDetail = new HashMap<>();
        Map<String, Object> responseMimeType = new HashMap<>();
        List<Map<String,Object>> chatHistory = new ArrayList<>();
        PromptInstructionConfig instruction = promptInstructionService.getBackEndPromptConfig().orElseThrow();
        responseMimeType.put("response_mime_type", "application/json");
        contents.put("text", prompt);
        promptDetail.put("role", "user");
        promptDetail.put("parts", contents);
        chatHistory.add(promptDetail);
        textInstruction.put("text", instruction.toString());
        systemInstructionParts.put("parts", textInstruction);
        requestBody.put("contents", chatHistory);
        requestBody.put("system_instruction", systemInstructionParts);
        requestBody.put("generationConfig", responseMimeType);

        Map<String, Object> response = restTemplate.postForObject(GEMINI_API_URL + geminiKey, requestBody, Map.class);

        log.info(response.toString());

        return response != null ? response : new HashMap<>();
    }
}
