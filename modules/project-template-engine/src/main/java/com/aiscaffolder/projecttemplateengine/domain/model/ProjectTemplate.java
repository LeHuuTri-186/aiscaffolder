package com.aiscaffolder.projecttemplateengine.domain.model;

import lombok.*;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ProjectTemplate {
    private String activeProfile;
    private String buildTool;
    private String createdBy;
    private String createdOn;
    private Map<String, String> customProperties;
    private List<DataSource> dataSources;
    private List<ProjectDependency> dependencies;
    private Map<String, String> environmentVariables;
    private ProjectMetaData projectMetaData;
    private String springBootVersion;
}