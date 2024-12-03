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
    private String projectName;
    private String projectDescription;
    private String version;
    private String groupId;
    private String artifactId;
    private String springBootVersion;
    private String javaVersion;
    private String buildTool;
    private List<ProjectDependency> dependencies;
    private String activeProfiles;
    private Map<String, String> environmentVariables;
    private String createdBy;
    private String contactEmail;
    private String organization;
    private LocalDateTime createdOn;
    private LocalDateTime lastModified;
    private Map<String, String> customProperties;
    private String basePackage;
    private String mainClassName;
}
