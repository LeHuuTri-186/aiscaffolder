package com.aiscaffolder.projecttemplateengine.domain.dto;

import com.aiscaffolder.projecttemplateengine.domain.enums.ApplicationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConfigurationDto {
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
