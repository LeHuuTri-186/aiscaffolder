package com.aiscaffolder.project_template_engine.domain.model.dto;

import com.aiscaffolder.project_template_engine.domain.model.DataSource;
import com.aiscaffolder.project_template_engine.domain.model.ProjectDependency;
import com.aiscaffolder.project_template_engine.domain.model.ProjectMetaData;
import lombok.*;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ProjectTemplateDto {
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
