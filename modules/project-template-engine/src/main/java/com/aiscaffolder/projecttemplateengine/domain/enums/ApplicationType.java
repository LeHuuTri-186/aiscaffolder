package com.aiscaffolder.projecttemplateengine.domain.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public enum ApplicationType {
    MONOLITH("monolith"),
    MICROSERVICE("microservice"),
    GATEWAY("gateway");

    @JsonValue
    private String value;
}
