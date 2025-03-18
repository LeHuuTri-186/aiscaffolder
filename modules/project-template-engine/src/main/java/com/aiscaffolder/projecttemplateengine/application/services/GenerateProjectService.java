package com.aiscaffolder.projecttemplateengine.application.services;

import com.aiscaffolder.projecttemplateengine.domain.entities.Configuration;
import com.aiscaffolder.projecttemplateengine.domain.entities.Dependency;
import com.aiscaffolder.projecttemplateengine.domain.entities.Entity;
import com.aiscaffolder.projecttemplateengine.domain.entities.Relationship;

import java.util.List;

public interface GenerateProjectService {
    void generateProject();
    void generateWrapper();
    void generateDependencies(List<Dependency> dependencies, Configuration configuration, String outputDir);
    void generateControllers(List<Entity> entities, Configuration configuration, String outputDir);
    void generateServices(List<Entity> entities, Configuration configuration, String outputDir);
    void generateRepositories(List<Entity> entities, Configuration configuration, String outputDir);
    void generateEntities(List<Entity> entities, Configuration configuration, List<Relationship> relationships, String outputDir);
    void generateConfigurations(Configuration configuration, String outputDir);
}
