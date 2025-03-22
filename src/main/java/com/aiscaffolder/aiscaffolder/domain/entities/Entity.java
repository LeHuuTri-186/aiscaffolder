package com.aiscaffolder.aiscaffolder.domain.entities;

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
    private String idFieldType;
    private String idFieldName;
}
