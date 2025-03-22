package com.aiscaffolder.aiscaffolder.domain.enums;

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
    HAZELCAST("hazelcast"),
    NO("no");

    @JsonValue
    private String value;
}
