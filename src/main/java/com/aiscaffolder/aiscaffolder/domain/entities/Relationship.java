package com.aiscaffolder.aiscaffolder.domain.entities;

import com.aiscaffolder.aiscaffolder.domain.enums.RelationshipType;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
public class Relationship {
    private RelationshipType type;
    private String fromEntity;
    private String toEntity;
    private String fromEntityField;
    private String toEntityField;
}
