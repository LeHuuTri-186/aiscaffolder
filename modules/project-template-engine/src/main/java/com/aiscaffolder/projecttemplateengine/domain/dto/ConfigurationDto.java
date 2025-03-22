package com.aiscaffolder.projecttemplateengine.domain.dto;

import com.aiscaffolder.projecttemplateengine.domain.enums.ApplicationType;
import com.aiscaffolder.projecttemplateengine.domain.enums.BuildTool;
import com.aiscaffolder.projecttemplateengine.domain.enums.CachingSolution;
import com.aiscaffolder.projecttemplateengine.domain.enums.DatabaseType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConfigurationDto {
    private String authenticationType;

    @NotNull
    @Range(min = 0, max = 65535, message = "Port must be in range of 0 - 65535")
    private Integer serverPort;
    private DatabaseType databaseType;
    private String devDatabaseType;
    private String prodDatabaseType;
    private BuildTool buildTool;
    private ApplicationType applicationType;
    private String description;
    private String springBootVersion;
    private Integer javaVersion;

    @NotBlank(message = "Group should not be blank")
    private String group;

    @NotBlank(message = "Artifact should not be blank")
    private String artifact;

    @NotBlank(message = "Name should not be blank")
    private String name;

    @NotBlank(message = "Package name should not be blank")
    private String packageName;

    private CachingSolution caching;

    private Boolean hibernateEnabled;
    private Boolean lombokEnabled;
}
