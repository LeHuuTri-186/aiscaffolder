package com.aiscaffolder.projecttemplateengine.domain.entities;

import lombok.*;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
public class Entity {
    private List<EntityField> entityFields;
    private String entityName;
}
