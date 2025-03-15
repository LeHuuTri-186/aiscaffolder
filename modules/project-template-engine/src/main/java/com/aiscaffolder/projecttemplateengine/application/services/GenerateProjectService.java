package com.aiscaffolder.projecttemplateengine.application.services;

import com.aiscaffolder.projecttemplateengine.domain.entities.Configuration;
import com.aiscaffolder.projecttemplateengine.domain.entities.Entity;

import java.util.List;

public interface GenerateProjectService {
    void generateProject();
    void generateWrapper();
    void generateEntities(List<Entity> entities, Configuration configuration, String outputDir);
    void generateConfigurations(Configuration configuration, String outputDir);
}
