package com.aiscaffolder.projecttemplateengine.application.services;

import com.aiscaffolder.projecttemplateengine.domain.entities.*;
import com.aiscaffolder.projecttemplateengine.domain.enums.BuildTool;
import com.aiscaffolder.projecttemplateengine.domain.enums.RelationshipType;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;


@Service
public class ProjectGenerationService {

    private final MustacheFactory mustacheFactory;
    private final ZipProjectService zipProjectService;

    public ProjectGenerationService(final MustacheFactory mustacheFactory, ZipProjectService zipProjectService) {
        this.mustacheFactory = mustacheFactory;
        this.zipProjectService = zipProjectService;
    }

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

    private String getBuildFile(Application application) {
        return application.getConfig().getBuildTool() == BuildTool.MAVEN ? "pom.xml" : "build.gradle";
    }

    public void generateProject(Application application, String outputDir, Map<String, String> files) throws Exception {
        for (Map.Entry<String, String> fileEntry : files.entrySet()) {
            // Resolve placeholders in file paths and template names
            String relativeFilePath = resolvePlaceholders(fileEntry.getKey(), application.getConfig());
            String templateName = fileEntry.getValue();

            System.out.println(templateName);

            // Render the template
            String fileContent = renderTemplate("classpath:/templates/spring-boot/java/" + templateName, application);

            // Write the rendered content to the resolved file path
            writeFile(outputDir, relativeFilePath, fileContent);


        }

        generateEntityFiles(application, outputDir);

//        Path baseDir = Path.of(outputDir);
//        Path mvnwPath = baseDir.resolve("mvnw");
//        setExecutable(mvnwPath);
        if (application.getConfig().getBuildTool() == BuildTool.MAVEN) {
            setExecutable(Path.of(outputDir, "mvnw"));
        } else if (application.getConfig().getBuildTool() == BuildTool.GRADLE) {
            setExecutable(Path.of(outputDir, "gradlew"));
        }

        System.out.println(outputDir);

        zipProjectService.zipProject(outputDir, outputDir + "/output.zip");
    }

    private String renderTemplate(String templateName, Object application) {
        try {
            // Ensure the resolved template name ends with ".mustache"
            String resolvedTemplateName = templateName.endsWith(".mustache") ? templateName : templateName + ".mustache";

            // Debug log for resolved template name

            Mustache mustache = mustacheFactory.compile(resolvedTemplateName);
            try (StringWriter writer = new StringWriter()) {
                mustache.execute(writer, application).flush();
                return writer.toString();
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to render template: " + templateName, e);
        }
    }


    private void generateEntityFiles(Application application, String outputDir) throws Exception {
        for (Entity entity : application.getEntities()) {
            // Create context for Mustache template
            Map<String, Object> context = new HashMap<>();
            context.put("config", application.getConfig());
            context.put("entity", prepareEntityContext(entity));
            context.put("relationships", getEntityRelationships(entity, application.getRelationships()));

            // Render entity template
            String entityContent = renderTemplate("classpath:/templates/spring-boot/java/entity.java.mustache", context);

            // Determine file path
            String entityFilePath = "src/main/java/" + application.getConfig().getPackageName().replace('.', '/')
                    + "/domain/" + entity.getEntityName() + ".java";

            // Write entity file

            System.out.println(context);
            writeFile(outputDir, entityFilePath, entityContent);

            System.out.println("Generated entity: " + entity.getEntityName() + ".java");
        }
    }

    private Map<String, Object> prepareEntityContext(Entity entity) {
        Map<String, Object> entityContext = new HashMap<>();
        entityContext.put("entityName", entity.getEntityName());

        // Process entity fields and add capitalized versions
        List<Map<String, String>> entityFields = new ArrayList<>();
        for (EntityField field : entity.getEntityFields()) {
            Map<String, String> fieldContext = new HashMap<>();
            fieldContext.put("fieldName", field.getFieldName());
            fieldContext.put("fieldType", field.getFieldType());
            fieldContext.put("fieldNameUpper", capitalize(field.getFieldName()));

            entityFields.add(fieldContext);
        }

        entityContext.put("entityFields", entityFields);
        return entityContext;
    }

    private List<Map<String, Object>> getEntityRelationships(Entity entity, List<Relationship> relationships) {
        List<Map<String, Object>> entityRelationships = new ArrayList<>();
        Set<String> processedSelfRefs = new HashSet<>();

        for (Relationship rel : relationships) {
            boolean isOwner = rel.getFromEntity().equals(entity.getEntityName());
            boolean isInverse = rel.getToEntity().equals(entity.getEntityName());

            if (!isOwner && !isInverse) {
                continue;
            }

            if (rel.getFromEntity().equals(rel.getToEntity())) {
                String selfKey = rel.getType() + ":" + rel.getFromEntity();
                if (processedSelfRefs.contains(selfKey)) {
                    continue;
                } else {
                    processedSelfRefs.add(selfKey);
                }
            }

            boolean isBidirectional = (rel.getFromEntityField() == null && rel.getToEntityField() == null) ||
                    (rel.getFromEntityField() != null && rel.getToEntityField() != null);
            boolean isUnidirectional = !isBidirectional;
            boolean isInverseBidirectional = isBidirectional && isInverse; // Inverse side of bidirectional

            Map<String, Object> relationshipContext = new HashMap<>();
            relationshipContext.put("type", rel.getType());
            relationshipContext.put("fromEntity", rel.getFromEntity());
            relationshipContext.put("toEntity", rel.getToEntity());
            relationshipContext.put("fromEntityField", rel.getFromEntityField());
            relationshipContext.put("toEntityField", rel.getToEntityField());
            relationshipContext.put("isBidirectional", isBidirectional && isOwner); // Generate only on owner side
            relationshipContext.put("isUnidirectional", isUnidirectional);
            relationshipContext.put("isInverseBidirectional", isInverseBidirectional);
            relationshipContext.put("isOneToMany", rel.getType() == RelationshipType.ONE_TO_MANY);
            relationshipContext.put("isManyToOne", rel.getType() == RelationshipType.MANY_TO_ONE);
            relationshipContext.put("isOneToOne", rel.getType() == RelationshipType.ONE_TO_ONE);
            relationshipContext.put("isManyToMany", rel.getType() == RelationshipType.MANY_TO_MANY);
            relationshipContext.put("isOwner", isOwner);

            String targetEntity = isOwner ? rel.getToEntity() : rel.getFromEntity();
            String targetEntityVarName = Character.toLowerCase(targetEntity.charAt(0)) + targetEntity.substring(1);
            String targetEntityVarNamePlural = targetEntityVarName + "s";

            relationshipContext.put("targetEntity", targetEntity);
            relationshipContext.put("targetEntityVarName", targetEntityVarName);
            relationshipContext.put("targetEntityVarNamePlural", targetEntityVarNamePlural);
            relationshipContext.put("targetEntityVarNameUpper", capitalize(targetEntityVarName));
            relationshipContext.put("targetEntityVarNameUpperPlural", capitalize(targetEntityVarNamePlural));

            entityRelationships.add(relationshipContext);
        }

        return entityRelationships;
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
}
