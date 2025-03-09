package com.aiscaffolder.aiscaffolder.domain.entities;

import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
public class EntityField {
    private String fieldName;
    private String fieldType;
}
