package com.aiscaffolder.projecttemplateengine.application.usecase;

import com.aiscaffolder.projecttemplateengine.application.service.ProjectGenerationService;
import com.aiscaffolder.projecttemplateengine.domain.entities.Application;
import com.aiscaffolder.projecttemplateengine.domain.entities.Entity;
import com.aiscaffolder.projecttemplateengine.domain.model.ProjectTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ProjectGenerationUseCase {

    private final ProjectGenerationService projectGenerationService;

    public ProjectGenerationUseCase(ProjectGenerationService projectGenerationService) {
        this.projectGenerationService = projectGenerationService;
    }

    public void execute(Application application, String outputDirectory) {
        Map<String, String> files = new HashMap<>();

        generateProjectFiles(files, application.getEntities());

        String basePackagePath = application.getConfig().getPackageName().replace('.', '/');

        Map<String, String> updatedFiles = new HashMap<>();
        files.forEach((path, template) -> {
            String updatedKey = path
                    .replace("{{basePackagePath}}", basePackagePath)
                    .replace("{{projectName}}", application.getConfig().getName() + "Application");
            updatedFiles.put(updatedKey, template);
        });


        System.out.println("Generated Files: " + updatedFiles);
        try {
            projectGenerationService.generateProject(application, outputDirectory, updatedFiles);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    private void generateProjectFiles(Map<String, String> files, List<Entity> entities) {
        // Java source files
        generateJavaSourceFiles(files);

        // Maven wrapper
        generateMavenWrapper(files);

        // Resources folder
        generateJavaResources(files);

        // Test files
        generateTestFiles(files);

        // Git
        generateGitFiles(files);

        // entities
        for (Entity entity : entities) {
            generateEntityFiles(files, entity.getEntityName());
        }
    }

    private void generateTestFiles(Map<String, String> files) {
        files.put("src/test/java/{{basePackagePath}}/{{mainClassName}}Tests.java", "MainApplicationTests.java");
    }

    private void generateGitFiles(Map<String, String> files) {
        files.put(".gitignore", ".gitignore");
        files.put(".gitattributes", ".gitattributes");
    }

    private void generateJavaResources(Map<String, String> files) {
        files.put("src/main/resources/application.yml", "application.yml");
    }

    private void generateJavaSourceFiles(Map<String, String> files) {
        files.put("pom.xml", "pom.xml");  // `pom.xml.mustache` template generates `pom.xml`
        files.put("src/main/java/{{basePackagePath}}/{{mainClassName}}.java", "MainApplication.java");
    }

    private void generateEntityFiles(Map<String, String> files, String entityName) {
        files.put("src/main/java/{{basePackagePath}}/domain/" + entityName + ".java", "entity.java");
    }

    private void generateMavenWrapper(Map<String, String> files) {
        files.put("mvnw", "mvnw");
        files.put("mvnw.cmd", "mvnw.cmd");
        files.put(".mvn/wrapper/maven-wrapper.properties", "maven-wrapper.properties");
    }
}
