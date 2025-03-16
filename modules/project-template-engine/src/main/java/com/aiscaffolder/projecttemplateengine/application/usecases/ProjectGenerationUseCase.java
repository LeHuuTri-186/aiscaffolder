package com.aiscaffolder.projecttemplateengine.application.usecases;

import com.aiscaffolder.projecttemplateengine.application.services.impl.GradleWrapperServiceImpl;
import com.aiscaffolder.projecttemplateengine.application.services.impl.MavenWrapperServiceImpl;
import com.aiscaffolder.projecttemplateengine.application.services.impl.ProjectGenerationServiceImpl;
import com.aiscaffolder.projecttemplateengine.application.services.SlackNotificationService;
import com.aiscaffolder.projecttemplateengine.domain.entities.Application;
import com.aiscaffolder.projecttemplateengine.domain.entities.Entity;
import com.aiscaffolder.projecttemplateengine.domain.enums.BuildTool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class ProjectGenerationUseCase {

    private final ProjectGenerationServiceImpl projectGenerationServiceImpl;
    private final MavenWrapperServiceImpl mavenWrapperServiceImpl;
    private final GradleWrapperServiceImpl gradleWrapperServiceImpl;
    private final SlackNotificationService slackNotificationService;

    public ProjectGenerationUseCase(ProjectGenerationServiceImpl projectGenerationServiceImpl, MavenWrapperServiceImpl mavenWrapperServiceImpl, GradleWrapperServiceImpl gradleWrapperServiceImpl, SlackNotificationService slackNotificationService) {
        this.projectGenerationServiceImpl = projectGenerationServiceImpl;
        this.mavenWrapperServiceImpl = mavenWrapperServiceImpl;
        this.gradleWrapperServiceImpl = gradleWrapperServiceImpl;

        this.slackNotificationService = slackNotificationService;
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

        // Generate Maven Wrapper
        try {
            if (application.getConfig().getBuildTool() == BuildTool.MAVEN) {
                mavenWrapperServiceImpl.generateWrapper(outputDirectory);
                updatedFiles.put("pom.xml", "pom.xml.mustache");

                log.info("Added pom.xml to the project");
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to generate Maven Wrapper", e);
        }

        // Generate Gradle Wrapper
        try {
            if (application.getConfig().getBuildTool() == BuildTool.GRADLE) {
                gradleWrapperServiceImpl.generateGradleWrapper(outputDirectory);
                updatedFiles.put("build.gradle", "build.gradle.mustache");
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to generate Gradle Wrapper", e);
        }

        try {
            projectGenerationServiceImpl.generateProject(application, outputDirectory, updatedFiles);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void generateProjectFiles(Map<String, String> files, List<Entity> entities) {
        // Java source files
        generateJavaSourceFiles(files);

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

    private void generateJavaSourceFiles(Map<String, String> files) {
        files.put("src/main/java/{{basePackagePath}}/{{mainClassName}}.java", "MainApplication.java");
    }
}
