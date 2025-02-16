package com.aiscaffolder.projecttemplateengine.domain.entities;

import com.aiscaffolder.projecttemplateengine.domain.enums.ApplicationType;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Configuration {
    private String authenticationType;
    private int serverPort;
    private String databaseType;
    private String devDatabaseType;
    private String prodDatabaseType;
    private String buildTool;
    private ApplicationType applicationType;
    private String description;
    private String springBootVersion;
    private String javaVersion;
    private String group;
    private String artifact;
    private String name;
    private String packageName;
}
