package com.aiscaffolder.aiscaffolder.application.services;

import com.aiscaffolder.aiscaffolder.domain.entities.Configuration;
import com.aiscaffolder.aiscaffolder.domain.entities.Dependency;
import com.aiscaffolder.aiscaffolder.domain.entities.Entity;
import com.aiscaffolder.aiscaffolder.domain.entities.Relationship;

import java.util.List;

public interface GenerateProjectService {
    void generateProject();
    void generateApplicationFile(Configuration configuration, String outputDir);
    void generateWrapper();
    void generateDockerCompose(Configuration configuration, String outputDir);
    void generateCachingFiles(List<Entity> entities, Configuration configuration, String outputDir);
    void generateDependencies(Configuration configuration, String outputDir);
    void generateControllers(List<Entity> entities, Configuration configuration, String outputDir);
    void generateServices(List<Entity> entities, Configuration configuration, String outputDir);
    void generateRepositories(List<Entity> entities, Configuration configuration, String outputDir);
    void generateEntities(List<Entity> entities, Configuration configuration, List<Relationship> relationships, String outputDir);
    void generateConfigurations(Configuration configuration, String outputDir);
    void writeFile(String outputDir, String relativePath, String content) throws Exception;
    String renderTemplate(String templateName, Object application);
}
