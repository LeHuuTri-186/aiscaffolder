package com.aiscaffolder.projecttemplateengine.application.usecases;

import com.aiscaffolder.projecttemplateengine.application.services.GradleWrapperService;
import com.aiscaffolder.projecttemplateengine.application.services.MavenWrapperService;
import com.aiscaffolder.projecttemplateengine.application.services.ProjectGenerationService;
import com.aiscaffolder.projecttemplateengine.domain.entities.Application;
import com.aiscaffolder.projecttemplateengine.domain.entities.Entity;
import com.aiscaffolder.projecttemplateengine.domain.enums.BuildTool;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ProjectGenerationUseCase {

    private final ProjectGenerationService projectGenerationService;
    private final MavenWrapperService mavenWrapperService;
    private final GradleWrapperService gradleWrapperService;

    public ProjectGenerationUseCase(ProjectGenerationService projectGenerationService, MavenWrapperService mavenWrapperService, GradleWrapperService gradleWrapperService) {
        this.projectGenerationService = projectGenerationService;
        this.mavenWrapperService = mavenWrapperService;
        this.gradleWrapperService = gradleWrapperService;

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

        // Generate Maven Wrapper
        try {
            if (application.getConfig().getBuildTool() == BuildTool.MAVEN) {
                mavenWrapperService.generateMavenWrapper(outputDirectory);
                files.put("pom.xml", "pom.xml.mustache");
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to generate Maven Wrapper", e);
        }

        // Generate Gradle Wrapper
        try {
            if (application.getConfig().getBuildTool() == BuildTool.GRADLE) {
                gradleWrapperService.generateGradleWrapper(outputDirectory);
                files.put("build.gradle", "build.gradle.mustache");
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to generate Gradle Wrapper", e);
        }

        try {
            projectGenerationService.generateProject(application, outputDirectory, updatedFiles);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    private void generateProjectFiles(Map<String, String> files, List<Entity> entities) {
        // Java source files
        generateJavaSourceFiles(files);

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
        // files.put("pom.xml", "pom.xml");  // `pom.xml.mustache` template generates `pom.xml`
        files.put("src/main/java/{{basePackagePath}}/{{mainClassName}}.java", "MainApplication.java");
    }

    private void generateEntityFiles(Map<String, String> files, String entityName) {
        files.put("src/main/java/{{basePackagePath}}/domain/" + entityName + ".java", "entity.java");
    }
}
