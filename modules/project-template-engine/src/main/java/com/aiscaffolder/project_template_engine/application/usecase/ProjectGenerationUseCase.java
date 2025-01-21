package com.aiscaffolder.project_template_engine.application.usecase;

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

    public void execute(ProjectTemplate projectTemplate, String outputDirectory) throws Exception {
        // Define a sample ProjectTemplate using a mutable map
        Map<String, String> files = new HashMap<>();

        generateProjectFiles(files);

        // Replace dots in basePackage with slashes for file paths
        String basePackagePath = projectTemplate.getProjectMetaData().getBasePackage().replace('.', '/');

        Map<String, String> updatedFiles = new HashMap<>();
        files.forEach((path, template) -> {
            String updatedKey = path
                    .replace("{{basePackagePath}}", basePackagePath)
                    .replace("{{projectName}}", projectTemplate.getProjectMetaData().getProjectName() + "Application");
            updatedFiles.put(updatedKey, template);
        });


        System.out.println("Generated Files: " + updatedFiles);
        projectGenerationService.generateProject(projectTemplate, outputDirectory, updatedFiles);
    }

    private void generateProjectFiles(Map<String, String> files) {
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

    private void generateMavenWrapper(Map<String, String> files) {
        files.put("mvnw", "mvnw");
        files.put("mvnw.cmd", "mvnw.cmd");
        files.put(".mvn/wrapper/maven-wrapper.properties", "maven-wrapper.properties");
    }
}
