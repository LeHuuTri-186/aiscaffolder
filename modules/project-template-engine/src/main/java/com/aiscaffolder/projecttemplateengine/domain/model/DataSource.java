package com.aiscaffolder.projecttemplateengine.domain.model;

import lombok.*;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class DataSource {
    private String dbName;
    private String dbType;
    private String host;
    private String port;
    private String username;
    private String password;
    private String driver;
}
