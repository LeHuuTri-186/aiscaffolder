package com.aiscaffolder.projecttemplateengine.domain.entities;

import com.aiscaffolder.projecttemplateengine.domain.enums.RelationshipType;
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
    private Boolean isBidirectional;
}
