package com.aiscaffolder.aiscaffolder.controllers;


import com.aiscaffolder.aiscaffolder.application.services.ApiGenerationService;
import com.aiscaffolder.aiscaffolder.domain.dto.AiChatRequestDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/ai")
public class ProjectJsonSchemaGenerationController {
    @Autowired
    private ApiGenerationService apiGenerationService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping("/generate")
    @CrossOrigin(origins = "http://localhost:5173")
    public ResponseEntity<Map<String, Object>> generateJsonFromAI(@RequestBody AiChatRequestDto aiChatRequestDto) throws JsonProcessingException {
        Map<String, Object> aiGeneratedData = apiGenerationService.generateAIJson(aiChatRequestDto.getContent());

        String result = "";

        List<Map<String, Object>> candidates = (List<Map<String, Object>>) aiGeneratedData.get("candidates");
        if (candidates != null && !candidates.isEmpty()) {
            Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
            List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
            if (parts != null && !parts.isEmpty()) {
                result = (String) parts.getFirst().get("text");
            }
        }

        return ResponseEntity.ok().body(objectMapper.readValue(result, Map.class));
    }
}
