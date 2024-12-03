package com.aiscaffolder.application.service;

import com.aiscaffolder.domain.model.*;
import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

@Service
public class ProjectGenerationService {

    private final MustacheFactory mustacheFactory;

    public ProjectGenerationService(final MustacheFactory mustacheFactory) {
        this.mustacheFactory = mustacheFactory;
    }

    /**
     * Resolves placeholders in a file path, such as {{mainClassName}}, with values from metadata.
     *
     * @param path     the original file path with placeholders
     * @param metaData the metadata containing values to replace placeholders
     * @return the resolved file path
     */
    private String resolvePlaceholders(String path, ProjectMetaData metaData) {
        if (path.contains("{{mainClassName}}")) {
            path = path.replace("{{mainClassName}}", metaData.getMainClassName());
        }
        if (path.contains("{{basePackagePath}}")) {
            String basePackagePath = metaData.getBasePackage().replace('.', '/');
            path = path.replace("{{basePackagePath}}", basePackagePath);
        }
        return path;
    }


    /**
     * Generates a project structure based on the given template and metadata.
     *
     * @param projectTemplate the project template with file mappings
     * @param metaData        the metadata to populate templates
     * @param outputDir       the directory where the project will be generated
     * @throws Exception if any errors occur during generation
     */
    public void generateProject(ProjectTemplate projectTemplate, ProjectMetaData metaData, String outputDir) throws Exception {

        for (Map.Entry<String, String> fileEntry : projectTemplate.getFiles().entrySet()) {
            // Resolve placeholders in file paths and template names
            String relativeFilePath = resolvePlaceholders(fileEntry.getKey(), metaData);
            String templateName = fileEntry.getValue();

            System.out.println(templateName);

            // Render the template
            String fileContent = renderTemplate("classpath:/templates/spring-boot/" + templateName, metaData);

            // Write the rendered content to the resolved file path
            writeFile(outputDir, relativeFilePath, fileContent);
            generateMavenWrapper(outputDir);
        }
    }

    /**
     * Renders a Mustache template with the given metadata.
     *
     * @param templateName the name of the template to render
     * @param metaData     the metadata to pass to the template
     * @return the rendered template content as a string
     */
    private String renderTemplate(String templateName, ProjectMetaData metaData) {
        try {
            // Ensure the resolved template name ends with ".mustache"
            String resolvedTemplateName = templateName.endsWith(".mustache") ? templateName : templateName + ".mustache";

            // Debug log for resolved template name

            Mustache mustache = mustacheFactory.compile(resolvedTemplateName);
            try (StringWriter writer = new StringWriter()) {
                mustache.execute(writer, metaData).flush();
                return writer.toString();
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to render template: " + templateName, e);
        }
    }

    /**
     * Writes content to a file, creating directories if necessary.
     *
     * @param outputDir      the base output directory
     * @param relativePath   the file's relative path
     * @param fileContent    the content to write to the file
     * @throws Exception if file writing fails
     */
    private void writeFile(String outputDir, String relativePath, String fileContent) throws Exception {
        Path filePath = Path.of(outputDir, relativePath);
        Files.createDirectories(filePath.getParent());
        Files.writeString(filePath, fileContent);
    }

    private void generateMavenWrapper(String outputDir) throws Exception {
        Path baseDir = Path.of(outputDir);

        // Write mvnw (Linux/macOS)
        Path mvnwPath = baseDir.resolve("mvnw");
        mvnwPath.toFile().setExecutable(true);
    }
}
