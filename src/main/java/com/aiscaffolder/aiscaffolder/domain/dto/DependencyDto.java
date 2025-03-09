package com.aiscaffolder.aiscaffolder.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DependencyDto {
    private String groupId;
    private String artifactId;
    private String version;
    private String scope;
}
