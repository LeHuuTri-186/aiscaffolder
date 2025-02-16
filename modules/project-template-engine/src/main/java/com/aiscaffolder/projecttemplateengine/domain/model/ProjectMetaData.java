package com.aiscaffolder.projecttemplateengine.domain.model;

import lombok.*;

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
