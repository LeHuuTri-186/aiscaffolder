package com.aiscaffolder.projecttemplateengine.domain.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
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
