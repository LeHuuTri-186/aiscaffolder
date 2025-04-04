package com.aiscaffolder.aiscaffolder.domain.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public enum DatabaseType {
    SQL("sql"),
    CASSANDRA("cassandra"),
    COUCHBASE("couchbase"),
    MONGODB("mongodb"),
    NO("no");

    @JsonValue
    private String value;
}
