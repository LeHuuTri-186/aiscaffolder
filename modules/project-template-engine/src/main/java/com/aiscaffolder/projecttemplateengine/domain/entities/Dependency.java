package com.aiscaffolder.projecttemplateengine.domain.entities;

import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
public class Dependency {
    private String groupId;
    private String artifactId;
    private String version;
    private String scope;
}
