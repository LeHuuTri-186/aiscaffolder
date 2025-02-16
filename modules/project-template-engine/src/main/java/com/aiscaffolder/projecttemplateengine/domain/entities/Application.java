package com.aiscaffolder.projecttemplateengine.domain.entities;

import lombok.*;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Application {
    private Configuration config;
    private List<Entity> entities;
    private List<Relationship> relationships;
    private List<Dependency> dependencies;
}
