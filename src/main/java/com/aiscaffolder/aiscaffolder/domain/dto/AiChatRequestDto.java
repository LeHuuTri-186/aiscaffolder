package com.aiscaffolder.aiscaffolder.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AiChatRequestDto {
    private AiAssistantDto assistant;
    private String content;
}
