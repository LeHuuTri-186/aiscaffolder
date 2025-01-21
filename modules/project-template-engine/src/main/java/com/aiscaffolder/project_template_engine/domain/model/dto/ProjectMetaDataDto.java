package com.aiscaffolder.project_template_engine.domain.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProjectMetaDataDto {
    private String artifactId;
    private String basePackage;
    private String description;
    private String groupId;
    private String javaVersion;
    private String packaging;
    private String projectName;
}
