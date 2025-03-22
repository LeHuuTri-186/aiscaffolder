package com.aiscaffolder.aiscaffolder.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EntityDto {
    private List<EntityFieldDto> entityFields;
    private String entityName;
    private String idFieldType;
    private String idFieldName;
}
