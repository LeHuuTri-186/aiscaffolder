package com.aiscaffolder.projecttemplateengine.domain.dto;

import com.aiscaffolder.projecttemplateengine.domain.enums.ApplicationType;
import com.aiscaffolder.projecttemplateengine.domain.enums.BuildTool;
import com.aiscaffolder.projecttemplateengine.domain.enums.DatabaseType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
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

    private DatabaseType databaseType;
    private String devDatabaseType;
    private String prodDatabaseType;
    private BuildTool buildTool;
    private ApplicationType applicationType;
    private String description;
    private String springBootVersion;

    private int javaVersion;

    @NotBlank(message = "Group should not be blank")
    private String group;

    @NotBlank(message = "Artifact should not be blank")
    private String artifact;

    @NotBlank(message = "Name should not be blank")
    private String name;

    @NotBlank(message = "Package name should not be blank")
    private String packageName;
    private boolean ormEnabled;
    private boolean lombokEnabled;
}
