package com.aiscaffolder.aiscaffolder.controllers;


import com.aiscaffolder.aiscaffolder.application.services.ApiGenerationService;
import com.aiscaffolder.aiscaffolder.domain.dto.AiChatRequestDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/ai")
@Slf4j
public class ProjectJsonSchemaGenerationController {
    @Autowired
    private ApiGenerationService apiGenerationService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping("/generate")
    @CrossOrigin(origins = "http://localhost:5173")
    public ResponseEntity<Map<String, Object>> generateJsonFromAI(@RequestBody AiChatRequestDto aiChatRequestDto) throws JsonProcessingException {
        Map<String, Object> aiGeneratedData = apiGenerationService.generateAIJson(aiChatRequestDto.getContent());

        log.info(aiGeneratedData.toString());

        String result = "";

        List<Map<String, Object>> candidates = (List<Map<String, Object>>) aiGeneratedData.get("candidates");
        if (candidates != null && !candidates.isEmpty()) {
            Object contentObj = candidates.getFirst().get("content");
            if (contentObj instanceof Map<?, ?> content) {
                Object partsObj = content.get("parts");
                if (partsObj instanceof List<?> parts && !parts.isEmpty()) {
                    Object textObj = ((Map<?, ?>) parts.getFirst()).get("text");
                    if (textObj instanceof List<?> textList && !textList.isEmpty()) {
                        result = textList.getFirst().toString();
                    } else if (textObj instanceof String text) {
                        result = text;
                    }
                }
            }
        }

        return ResponseEntity.ok().body(objectMapper.readValue(result, Map.class));
    }
}
