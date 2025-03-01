package com.aiscaffolder.projecttemplateengine.domain.dto;

import com.aiscaffolder.projecttemplateengine.domain.enums.ApplicationType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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

    @Min(value = 0, message = "Port cannot be a negative value")
    @Max(value = 65535, message = "Port must not be greater than 65535")
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
    private boolean ormEnabled;
    private boolean lombokEnabled;
}
