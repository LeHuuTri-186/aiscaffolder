package com.aiscaffolder.project_template_engine.application.service;

import com.aiscaffolder.project_template_engine.application.usecase.EntityGenerator;
import com.aiscaffolder.project_template_engine.domain.model.ProjectMetaData;
import com.aiscaffolder.project_template_engine.domain.model.ProjectTemplate;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
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
    public void generateProject(ProjectTemplate projectTemplate, ProjectMetaData metaData, String outputDir,  String entitiesFilePath) throws Exception {

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
        // Tạo entity class từ file JSON
        generateEntitiesFromJson(entitiesFilePath, outputDir, metaData);
    }

    /**
     * Generate CRUD Controllers for each entity.
     * @param outputDir The output directory where the controller will be generated.
     * @param entity The entity information (name, fields, etc.)
     * @param basePackage The base package of the application.
     * @throws Exception If file write or template render errors occur.
     */
    public void generateCRUDControllers(String outputDir, EntityGenerator.Entity entity, ProjectMetaData metaData) throws Exception {
        // Prepare data for Mustache template
        Map<String, Object> templateData = new HashMap<>();

        templateData.put("className", entity.name);
        templateData.put("classNameLowercase", entity.name.substring(0, 1).toLowerCase() + entity.name.substring(1));
        templateData.put("basePackage", metaData.getBasePackage());
        templateData.put("fields", entity.fields); // Pass the list of fields to the template

        // Render the CRUD controller using the Mustache template
        String controllerContent = renderEntityTemplate("classpath:/templates/spring-boot/controller.mustache", templateData);

        // Write the controller to the appropriate file location
        String controllerFilePath = "src/main/java/" + metaData.getBasePackage().replace(".", "/") + "/controller/" + entity.name + "Controller.java";
        writeFile(outputDir, controllerFilePath, controllerContent);
    }

    /**
     * Phương thức này tạo các entity classes từ file JSON.
     * @param entitiesFilePath Đường dẫn đến file JSON chứa thông tin các entities.
     * @param outputDir Thư mục đầu ra.
     * @throws Exception Nếu có lỗi khi tạo entity.
     */
    public void generateEntitiesFromJson(String entitiesFilePath, String outputDir, ProjectMetaData metaData) throws Exception {
        // Đọc và phân tích file JSON
        EntityGenerator.EntityData entityData = EntityGenerator.parseJson(entitiesFilePath);

        // Tạo entity class cho từng entity trong file JSON
        for (EntityGenerator.Entity entity : entityData.entities) {
            generateEntityClass(entity, outputDir, metaData);
            generateCRUDControllers(outputDir, entity, metaData);
        }
    }


    private void generateEntityClass(EntityGenerator.Entity entity, String outputDir, ProjectMetaData metaData) throws Exception {
        // Định nghĩa dữ liệu template cho entity
        Map<String, Object> templateData = Map.of(
                "className", entity.name,
                "basePackage", metaData.getBasePackage(), // Thêm basePackage vào template data
                "fields", entity.fields
        );

        // Render template cho entity class
        String entityContent = renderEntityTemplate("classpath:/entity/entity.mustache", templateData);

        // Ghi nội dung vào file entity class
        writeFile(outputDir, "src/main/java/" + metaData.getBasePackage().replace('.', '/') + "/" + entity.name + ".java", entityContent);
    }

    /**
     * Renders a Mustache template with the given metadata.
     *
     * @param templateName the name of the template to render
     * @param metaData     the metadata to pass to the template
     * @return the rendered template content as a string
     */
    public String renderTemplate(String templateName, ProjectMetaData metaData) {
        try {
            // Ensure the resolved template name ends with ".mustache"
            String resolvedTemplateName = templateName.endsWith(".mustache") ? templateName : templateName + ".mustache";
            System.out.println(resolvedTemplateName);
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
     * Render một Mustache template cho entity với dữ liệu dạng Map.
     * @param templateName Tên của template cần render.
     * @param templateData Dữ liệu cần thay thế vào template.
     * @return Nội dung đã render.
     */
    public String renderEntityTemplate(String templateName, Map<String, Object> templateData) {
        try {
            String resolvedTemplateName = templateName.endsWith(".mustache") ? templateName : templateName + ".mustache";
            System.out.println("Entity: " + resolvedTemplateName);
            Mustache mustache = mustacheFactory.compile(resolvedTemplateName);
            StringWriter writer = new StringWriter();
            mustache.execute(writer, templateData).flush();
            return writer.toString();
        } catch (Exception e) {
            throw new RuntimeException("Failed to render entity template: " + templateName, e);
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
    public void writeFile(String outputDir, String relativePath, String fileContent) throws Exception {
        Path filePath = Path.of(outputDir, relativePath);
        Files.createDirectories(filePath.getParent());
        Files.writeString(filePath, fileContent);
        System.out.println(relativePath);
    }

    private void generateMavenWrapper(String outputDir) throws Exception {
        Path baseDir = Path.of(outputDir);

        // Write mvnw (Linux/macOS)
        Path mvnwPath = baseDir.resolve("mvnw");
        mvnwPath.toFile().setExecutable(true);
    }
}
