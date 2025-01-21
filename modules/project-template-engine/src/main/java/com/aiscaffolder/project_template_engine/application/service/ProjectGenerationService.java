package com.aiscaffolder.project_template_engine.application.service;

import com.aiscaffolder.project_template_engine.domain.model.ProjectMetaData;
import com.aiscaffolder.project_template_engine.domain.model.ProjectTemplate;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermission;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

@Service
public class ProjectGenerationService {

    private final MustacheFactory mustacheFactory;

    public ProjectGenerationService(final MustacheFactory mustacheFactory) {
        this.mustacheFactory = mustacheFactory;
    }

    private String resolvePlaceholders(String path, ProjectMetaData metaData) {
        if (path.contains("{{mainClassName}}")) {
            path = path.replace("{{mainClassName}}", metaData.getProjectName() + "Application");
        }
        if (path.contains("{{basePackagePath}}")) {
            String basePackagePath = metaData.getBasePackage().replace('.', '/');
            path = path.replace("{{basePackagePath}}", basePackagePath);
        }
        return path;
    }

    public void generateProject(ProjectTemplate template, String outputDir, Map<String, String> files) throws Exception {

        for (Map.Entry<String, String> fileEntry : files.entrySet()) {
            // Resolve placeholders in file paths and template names
            String relativeFilePath = resolvePlaceholders(fileEntry.getKey(), template.getProjectMetaData());
            String templateName = fileEntry.getValue();

            System.out.println(templateName);

            // Render the template
            String fileContent = renderTemplate("classpath:/templates/spring-boot/" + templateName, template);

            // Write the rendered content to the resolved file path
            writeFile(outputDir, relativeFilePath, fileContent);

            Path baseDir = Path.of(outputDir);
            Path mvnwPath = baseDir.resolve("mvnw");
            setExecutable(mvnwPath);
        }
    }

    private String renderTemplate(String templateName, ProjectTemplate template) {
        try {
            // Ensure the resolved template name ends with ".mustache"
            String resolvedTemplateName = templateName.endsWith(".mustache") ? templateName : templateName + ".mustache";

            // Debug log for resolved template name

            Mustache mustache = mustacheFactory.compile(resolvedTemplateName);
            try (StringWriter writer = new StringWriter()) {
                mustache.execute(writer, template).flush();
                return writer.toString();
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to render template: " + templateName, e);
        }
    }

    private void writeFile(String outputDir, String relativePath, String fileContent) throws Exception {
        Path filePath = Path.of(outputDir, relativePath);
        Files.createDirectories(filePath.getParent());
        Files.writeString(filePath, fileContent);
    }

    private void setExecutable(Path path) throws Exception {
        try {
            Set<PosixFilePermission> permissions = EnumSet.of(
                    PosixFilePermission.OWNER_EXECUTE,
                    PosixFilePermission.OWNER_READ,
                    PosixFilePermission.OWNER_WRITE,
                    PosixFilePermission.GROUP_EXECUTE,
                    PosixFilePermission.GROUP_READ,
                    PosixFilePermission.OTHERS_EXECUTE,
                    PosixFilePermission.OTHERS_READ
            );
            Files.setPosixFilePermissions(path, permissions);
        } catch (UnsupportedOperationException e) {
            // Fallback for non-POSIX systems (Windows)
            var b = path.toFile().setExecutable(true);
        }
    }
}
