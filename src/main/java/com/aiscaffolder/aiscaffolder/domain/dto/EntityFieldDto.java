package com.aiscaffolder.aiscaffolder.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EntityFieldDto {
    private String fieldName;
    private String fieldType;
}
