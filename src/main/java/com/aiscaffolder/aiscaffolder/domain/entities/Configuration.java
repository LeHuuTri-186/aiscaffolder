package com.aiscaffolder.aiscaffolder.domain.entities;

import com.aiscaffolder.aiscaffolder.domain.enums.ApplicationType;
import com.aiscaffolder.aiscaffolder.domain.enums.BuildTool;
import com.aiscaffolder.aiscaffolder.domain.enums.DatabaseType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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
    private DatabaseType databaseType;
    private String devDatabaseType;
    private String prodDatabaseType;
    private BuildTool buildTool;
    private ApplicationType applicationType;
    private String description;
    private String springBootVersion;
    private int javaVersion;
    private String group;
    private String artifact;
    private String name;
    private String packageName;
    private boolean ormEnabled;
    private boolean lombokEnabled;
}
