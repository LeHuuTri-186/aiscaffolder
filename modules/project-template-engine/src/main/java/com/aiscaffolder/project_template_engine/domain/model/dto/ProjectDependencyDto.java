package com.aiscaffolder.project_template_engine.domain.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProjectDependencyDto {
    private String groupId;
    private String artifactId;
    private String version;
    private String scope;
}
