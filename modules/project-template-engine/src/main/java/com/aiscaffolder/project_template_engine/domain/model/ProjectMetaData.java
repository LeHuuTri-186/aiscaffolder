package com.aiscaffolder.project_template_engine.domain.model;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ProjectMetaData {
    private String artifactId;
    private String basePackage;
    private String description;
    private String groupId;
    private String javaVersion;
    private String packaging;
    private String projectName;
}
