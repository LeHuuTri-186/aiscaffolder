package com.aiscaffolder.project_template_engine.application.usecase;

import com.aiscaffolder.project_template_engine.application.EntityData;
import com.aiscaffolder.project_template_engine.application.service.ProjectGenerationService;
import com.aiscaffolder.project_template_engine.domain.model.ProjectMetaData;
import com.aiscaffolder.project_template_engine.domain.model.ProjectTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ProjectGenerationUseCase {

    private final ProjectGenerationService projectGenerationService;

    public ProjectGenerationUseCase(ProjectGenerationService projectGenerationService) {
        this.projectGenerationService = projectGenerationService;
    }

    /**
     * Executes the project generation logic.
     *
     * @param metaData        The metadata for the project.
     * @param outputDirectory The directory where the project should be created.
     * @throws Exception if project generation fails.
     */
    public void execute(ProjectMetaData metaData, String outputDirectory, String entitiesFilePath) throws Exception {
        // Define a sample ProjectTemplate using a mutable map
        Map<String, String> files = new HashMap<>();
        files.put("pom.xml", "pom.xml");  // `pom.xml.mustache` template generates `pom.xml`
        files.put("src/main/java/{{basePackagePath}}/{{mainClassName}}.java", "MainApplication.java");
        files.put("mvnw", "mvnw");
        files.put("mvnw.cmd", "mvnw.cmd");
        files.put(".mvn/wrapper/maven-wrapper.properties", "maven-wrapper.properties");

        // Replace dots in basePackage with slashes for file paths
        String basePackagePath = metaData.getBasePackage().replace('.', '/');

        Map<String, String> updatedFiles = new HashMap<>();

        files.forEach((path, template) -> {
            String updatedKey = path
                    .replace("{{basePackagePath}}", basePackagePath)
                    .replace("{{mainClassName}}", metaData.getMainClassName());
            updatedFiles.put(updatedKey, template);
        });

// Replace the old map with the updated one
        files = updatedFiles;

        ProjectTemplate projectTemplate = ProjectTemplate.builder()
                .files(files)
                .build();

        System.out.println(files);

        // Generate the project
        projectGenerationService.generateProject(projectTemplate, metaData, outputDirectory, entitiesFilePath);
    }
}
