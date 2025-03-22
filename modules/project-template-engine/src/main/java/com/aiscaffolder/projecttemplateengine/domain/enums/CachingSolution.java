package com.aiscaffolder.projecttemplateengine.domain.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Getter
@NoArgsConstructor
public enum CachingSolution {
    EHCACHE("ehcache"),
    REDIS("redis"),
    HAZELCAST("hazelcast"),;

    @JsonValue
    private String value;
}
