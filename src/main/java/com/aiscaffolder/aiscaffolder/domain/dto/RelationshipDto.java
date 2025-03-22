package com.aiscaffolder.aiscaffolder.domain.dto;

import com.aiscaffolder.aiscaffolder.domain.enums.RelationshipType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RelationshipDto {
    private RelationshipType type;
    private String fromEntity;
    private String toEntity;
    private Boolean isBidirectional;
}
