package com.aiscaffolder.aiscaffolder.domain.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public enum BuildTool {
    MAVEN("maven"),
    GRADLE("gradle"),;

    @JsonValue
    private String value;
}
