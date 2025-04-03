package com.aiscaffolder.aiscaffolder.controllers;


import com.aiscaffolder.aiscaffolder.application.services.ApiGenerationService;
import com.aiscaffolder.aiscaffolder.domain.dto.AiChatRequestDto;
import com.aiscaffolder.aiscaffolder.domain.dto.ConfigurationDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/ai")
@Slf4j
@CrossOrigin(origins = "http://localhost:5173")
public class ProjectJsonSchemaGenerationController {

    @Autowired
    private ApiGenerationService apiGenerationService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping("/generate/project")
    public ResponseEntity<Map<String, Object>> generateJsonFromAI(@RequestBody AiChatRequestDto aiChatRequestDto)
            throws JsonProcessingException {

        Map<String, Object> aiGeneratedData = apiGenerationService.generateAIJson(aiChatRequestDto.getContent());

        String result = extractGeneratedText(aiGeneratedData);
        if (result.isEmpty()) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", "No valid content generated"));
        }

        return ResponseEntity.ok(objectMapper.readValue(result, Map.class));
    }

    @PostMapping("/generate/config")
    public ResponseEntity<ConfigurationDto> generateConfigJsonFromAI(@RequestBody AiChatRequestDto aiChatRequestDto) {

        Map<String, Object> aiGeneratedData = apiGenerationService.generateAIJson(aiChatRequestDto.getContent());

        String result = extractGeneratedText(aiGeneratedData);
        if (result.isEmpty()) {
            return ResponseEntity.badRequest().body(null);
        }

        return ResponseEntity.badRequest().body(null);
    }

    private String extractGeneratedText(Map<String, Object> aiGeneratedData) {
        List<Map<String, Object>> candidates = (List<Map<String, Object>>) aiGeneratedData.get("candidates");
        if (candidates == null || candidates.isEmpty()) return "";

        Object contentObj = candidates.getFirst().get("content");
        if (!(contentObj instanceof Map<?, ?> content)) return "";

        Object partsObj = content.get("parts");
        if (!(partsObj instanceof List<?> parts) || parts.isEmpty()) return "";

        Object textObj = ((Map<?, ?>) parts.getFirst()).get("text");
        if (textObj instanceof List<?> textList && !textList.isEmpty()) {
            return textList.getFirst().toString();
        } else if (textObj instanceof String text) {
            return text;
        }
        return "";
    }
}
