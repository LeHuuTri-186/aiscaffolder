package com.aiscaffolder.projecttemplateengine.application.services.impl;

import com.aiscaffolder.projecttemplateengine.application.services.GenerateProjectService;
import com.aiscaffolder.projecttemplateengine.application.services.ZipProjectService;
import com.aiscaffolder.projecttemplateengine.domain.entities.*;
import com.aiscaffolder.projecttemplateengine.domain.enums.BuildTool;
import com.aiscaffolder.projecttemplateengine.domain.enums.DatabaseType;
import com.aiscaffolder.projecttemplateengine.domain.enums.RelationshipType;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermission;
import java.util.*;


@Slf4j
@Service
@AllArgsConstructor
public class ProjectGenerationServiceImpl implements GenerateProjectService {

    public final String TEMPLATE_PATH = "classpath:/templates/spring-boot/java/";
    private final MustacheFactory mustacheFactory;
    private final ZipProjectService zipProjectService;

    private String resolvePlaceholders(String path, Configuration config) {
        if (path.contains("{{mainClassName}}")) {
            path = path.replace("{{mainClassName}}", config.getName() + "Application");
        }
        if (path.contains("{{basePackagePath}}")) {
            String basePackagePath = config.getPackageName().replace('.', '/');
            path = path.replace("{{basePackagePath}}", basePackagePath);
        }
        return path;
    }

    public void generateProject(Application application, String outputDir, Map<String, String> files) throws Exception {
        for (Map.Entry<String, String> fileEntry : files.entrySet()) {
            // Resolve placeholders in file paths and template names
            String relativeFilePath = resolvePlaceholders(fileEntry.getKey(), application.getConfig());
            String templateName = fileEntry.getValue();

            System.out.println(templateName);

            // Render the template
            String fileContent = renderTemplate(TEMPLATE_PATH + templateName, application);

            // Write the rendered content to the resolved file path
            writeFile(outputDir, relativeFilePath, fileContent);
        }
        generateConfigurations(application.getConfig(), outputDir);

        generateEntities(application.getEntities(), application.getConfig(), outputDir);

        log.info("output dir: {}", outputDir);
        if (application.getConfig().getBuildTool() == BuildTool.MAVEN) {
            setExecutable(Path.of(outputDir, "mvnw"));
        } else if (application.getConfig().getBuildTool() == BuildTool.GRADLE) {
            setExecutable(Path.of(outputDir, "gradlew"));
        }

        log.info(outputDir);

        zipProjectService.zipProject(outputDir, outputDir + "/" + application.getConfig().getArtifact() + ".zip");
    }

    private String renderTemplate(String templateName, Object application) {
        try {
            // Ensure the resolved template name ends with ".mustache"
            String resolvedTemplateName = templateName.endsWith(".mustache") ? templateName : templateName + ".mustache";

            Mustache mustache = mustacheFactory.compile(resolvedTemplateName);
            try (StringWriter writer = new StringWriter()) {
                mustache.execute(writer, application).flush();
                return writer.toString();
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to render template: " + templateName, e);
        }
    }

    private String capitalize(String str) {
        return Character.toUpperCase(str.charAt(0)) + str.substring(1);
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

    @Override
    public void generateProject() {

    }

    @Override
    public void generateWrapper() {

    }

    @Override
    public void generateEntities(List<Entity> entities, Configuration configuration, String outputDir) {
        for (Entity entity : entities) {

            Map<String, Object> entityContext = new HashMap<>();

            entityContext.put("name", entity.getEntityName());

            if (null == entity.getIdFieldName()) {
                entityContext.put("idFieldName", "id");
                entityContext.put("idFieldNameUpper", "Id");
            } else {
                entityContext.put("idFieldName", entity.getIdFieldName());
                entityContext.put("idFieldNameUpper", capitalize(entity.getIdFieldName()));
            }

            if (null == entity.getIdFieldType()) {
                entityContext.put("idFieldType", "Integer");
            } else {
                entityContext.put("idFieldType", entity.getIdFieldType());
            }

            entityContext.put("packageName", configuration.getPackageName());

            entityContext.put("lombokEnabled", false);

            if (null == configuration.getLombokEnabled() || configuration.getLombokEnabled()) {
                entityContext.put("lombokEnabled", true);
            }

            entityContext.put("hibernateEnabled", false);

            if (null == configuration.getHibernateEnabled() || configuration.getHibernateEnabled()) {
                entityContext.put("hibernateEnabled", true);
            }

            List<Map<String, Object>> entityFields = new ArrayList<>();

            entityContext.put("hasField", !entity.getEntityFields().isEmpty());

            for (EntityField field : entity.getEntityFields()) {
                Map<String, Object> fieldContext = new HashMap<>();
                fieldContext.put("fieldName", field.getFieldName());
                fieldContext.put("fieldType", field.getFieldType());
                fieldContext.put("fieldNameUpper", capitalize(field.getFieldName()));

                entityFields.add(fieldContext);
            }

            entityFields.getLast().put("isLastField", true);

            entityContext.put("fields", entityFields);

            String content = renderTemplate(TEMPLATE_PATH + "entity.java", entityContext);

            String entityFilePath = "src/main/java/" + configuration.getPackageName().replace('.', '/')
                    + "/domain/" + entity.getEntityName() + ".java";

            try {
                writeFile(outputDir, entityFilePath, content);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void generateConfigurations(Configuration config, String outputDir) {
        Map<String, Object> configMap = new HashMap<>();
        Map<String, Object> devProfile = new HashMap<>();
        Map<String, Object> prodProfile = new HashMap<>();

        configMap.put("hasDbConfig", config.getDatabaseType() != DatabaseType.NO);
        configMap.put("isSQL", config.getDatabaseType() == DatabaseType.SQL);

        configMap.put("serverPort", config.getServerPort());
        configMap.put("name", config.getName());

        try {
            writeFile(outputDir, "src/main/resources/application.yml", renderTemplate(TEMPLATE_PATH + "application.yml", configMap));
            writeFile(outputDir, "src/main/resources/application-dev.yml", renderTemplate(TEMPLATE_PATH + "application-dev.yml", configMap));
            writeFile(outputDir, "src/main/resources/application-prod.yml", renderTemplate(TEMPLATE_PATH + "application-prod.yml", configMap));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}