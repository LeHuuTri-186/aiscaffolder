package com.aiscaffolder.aiscaffolder.domain.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public enum RelationshipType {
    ONE_TO_MANY("oneToMany"),
    MANY_TO_ONE("manyToOne"),
    ONE_TO_ONE("oneToOne"),
    MANY_TO_MANY("manyToMany");

    @JsonValue
    private String value;
}
