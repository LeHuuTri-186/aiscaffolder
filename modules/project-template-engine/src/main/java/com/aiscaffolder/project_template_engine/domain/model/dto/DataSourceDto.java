package com.aiscaffolder.project_template_engine.domain.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DataSourceDto {
    private String dbName;
    private String dbType;
    private String host;
    private String port;
    private String username;
    private String password;
    private String driver;
}
