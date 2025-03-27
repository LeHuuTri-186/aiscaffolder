package com.aiscaffolder.aiscaffolder.application.services.impl;

import com.aiscaffolder.aiscaffolder.application.services.GenerateProjectService;
import com.aiscaffolder.aiscaffolder.application.services.ZipProjectService;
import com.aiscaffolder.aiscaffolder.domain.entities.*;
import com.aiscaffolder.aiscaffolder.domain.enums.DatabaseType;
import com.aiscaffolder.aiscaffolder.domain.enums.RelationshipType;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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
            String relativeFilePath = resolvePlaceholders(fileEntry.getKey(), application.getConfig());
            String templateName = fileEntry.getValue();

            log.debug(templateName);

            String fileContent = renderTemplate(TEMPLATE_PATH + templateName, application);

            writeFile(outputDir, relativeFilePath, fileContent);
        }
        generateRepositories(application.getEntities(), application.getConfig(), outputDir);

        generateConfigurations(application.getConfig(), outputDir);

        generateEntities(application.getEntities(), application.getConfig(), application.getRelationships(), outputDir);

        generateServices(application.getEntities(), application.getConfig(), outputDir);

        generateControllers(application.getEntities(), application.getConfig(), outputDir);

        log.info(outputDir);

        zipProjectService.zipProject(outputDir, outputDir + "/" + application.getConfig().getArtifact() + ".zip");
    }

    @Override
    public String renderTemplate(String templateName, Object application) {
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

    @Override
    public void writeFile(String outputDir, String relativePath, String content) throws Exception {
        Path filePath = Path.of(outputDir, relativePath);
        Files.createDirectories(filePath.getParent());
        Files.writeString(filePath, content);
    }

    @Override
    public void generateProject() {

    }

    @Override
    public void generateWrapper() {

    }

    @Override
    public void generateDependencies(List<Dependency> dependencies, Configuration configuration, String outputDir) {
    }

    @Override
    public void generateControllers(List<Entity> entities, Configuration configuration, String outputDir) {
        for (Entity entity : entities) {
            Map<String, Object> entityRepositoryContext = new HashMap<>();

            entityRepositoryContext.put("entity", entity.getEntityName());
            entityRepositoryContext.put("entityLower", entity.getEntityName().toLowerCase());
            entityRepositoryContext.put("packageName", configuration.getPackageName());
            entityRepositoryContext.put("idFieldType", entity.getIdFieldType());

            String content = renderTemplate(TEMPLATE_PATH + "controller.java.mustache", entityRepositoryContext);

            String entityFilePath = "src/main/java/" + configuration.getPackageName().replace('.', '/')
                    + "/controllers/" + entity.getEntityName() + "Controller.java";
            try {
                writeFile(outputDir, entityFilePath, content);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void generateServices(List<Entity> entities, Configuration configuration, String outputDir) {
        for (Entity entity : entities) {
            Map<String, Object> entityRepositoryContext = new HashMap<>();

            entityRepositoryContext.put("entity", entity.getEntityName());
            entityRepositoryContext.put("entityLower", entity.getEntityName().toLowerCase());
            entityRepositoryContext.put("packageName", configuration.getPackageName());
            entityRepositoryContext.put("idFieldType", entity.getIdFieldType());

            String content = renderTemplate(TEMPLATE_PATH + "service.java.mustache", entityRepositoryContext);

            String entityFilePath = "src/main/java/" + configuration.getPackageName().replace('.', '/')
                    + "/services/" + entity.getEntityName() + "Service.java";
            try {
                writeFile(outputDir, entityFilePath, content);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void generateRepositories(List<Entity> entities, Configuration configuration, String outputDir) {
        for (Entity entity : entities) {
            Map<String, Object> entityRepositoryContext = new HashMap<>();

            if (configuration.getHibernateEnabled()) {
                entityRepositoryContext.put("jpa", entity.getEntityName());
            }

            entityRepositoryContext.put("entity", entity.getEntityName());
            entityRepositoryContext.put("packageName", configuration.getPackageName());
            entityRepositoryContext.put("idFieldType", entity.getIdFieldType());

            String content = renderTemplate(TEMPLATE_PATH + "repositoryInterface.java.mustache", entityRepositoryContext);

            String entityFilePath = "src/main/java/" + configuration.getPackageName().replace('.', '/')
                    + "/repositories/" + entity.getEntityName() + "Repository.java";
            try {
                writeFile(outputDir, entityFilePath, content);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void generateEntities(List<Entity> entities, Configuration configuration, List<Relationship> relationships, String outputDir) {
        for (Entity entity : entities) {

            Map<String, Object> entityContext = new HashMap<>();

            generateEntityContext(configuration, entity, entityContext);

            List<Map<String, Object>> relationshipContexts = new ArrayList<>();

            relationships.forEach(relationship -> {
                Map<String, Object> relationshipContext = new HashMap<>();
                if (relationship.getType() == RelationshipType.ONE_TO_ONE) {
                    relationshipContext.put("isOneToOne", true);
                } else if (relationship.getType() == RelationshipType.MANY_TO_ONE) {
                    relationshipContext.put("isManyToOne", true);
                } else if (relationship.getType() == RelationshipType.ONE_TO_MANY) {
                    relationshipContext.put("isOneToMany", true);
                } else if (relationship.getType() == RelationshipType.MANY_TO_MANY) {
                    relationshipContext.put("isManyToMany", true);
                }

                relationshipContext.put("targetClass", relationship.getToEntity());
                relationshipContext.put("sourceClass", relationship.getFromEntity());
                relationshipContext.put("sourceClassLower", relationship.getFromEntity().toLowerCase());
                relationshipContext.put("targetClassLower", relationship.getToEntity().toLowerCase());
                relationshipContext.put("isBidirectional", relationship.getIsBidirectional());
                relationshipContext.put("nameLower",entity.getEntityName().toLowerCase());

                if (relationship.getFromEntity().equals(entity.getEntityName())) {
                    relationshipContext.put("isSourceClass", true);
                }

                if (relationship.getToEntity().equals(entity.getEntityName())) {
                    relationshipContext.put("isTargetClass", true);
                }

                relationshipContexts.add(relationshipContext);

                log.info("relationshipContexts: {}", relationshipContext);
            });

            entityContext.put("relationships", relationshipContexts);

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

    private void generateEntityContext(Configuration configuration, Entity entity, Map<String, Object> entityContext) {
        entityContext.put("name", entity.getEntityName());
        entityContext.put("nameLower", entity.getEntityName().toLowerCase());

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

        if (entity.getEntityFields().isEmpty()) {
            return;
        }

        for (EntityField field : entity.getEntityFields()) {
            Map<String, Object> fieldContext = new HashMap<>();
            fieldContext.put("fieldName", field.getFieldName());
            fieldContext.put("fieldType", field.getFieldType());
            fieldContext.put("fieldNameUpper", capitalize(field.getFieldName()));

            entityFields.add(fieldContext);
        }

        entityFields.getLast().put("isLastField", true);

        entityContext.put("fields", entityFields);
    }

    @Override
    public void generateConfigurations(Configuration config, String outputDir) {
        Map<String, Object> configMap = new HashMap<>();
        Map<String, Object> devProfile = new HashMap<>();
        Map<String, Object> prodProfile = new HashMap<>();

        if (config.getDatabaseType() != DatabaseType.NO) {
            configMap.put("dbConfig", true);

            if (config.getDatabaseType() == DatabaseType.SQL) {
                configMap.put("isDatasource", true);
                configMap.put("isMySql", config.getProdDatabaseType().equalsIgnoreCase("mysql"));
                configMap.put("isOracle", config.getProdDatabaseType().equalsIgnoreCase("oracle"));
                configMap.put("isPostgres", config.getProdDatabaseType().equalsIgnoreCase("postgresql"));
                configMap.put("isMssql", config.getProdDatabaseType().equalsIgnoreCase("mssql"));
                configMap.put("isMariaDb", config.getProdDatabaseType().equalsIgnoreCase("mariadb"));
            } else {
                configMap.put("isData", false);
            }

            if (!config.getProdDatabaseType().equalsIgnoreCase("no")) {
                prodProfile.put("isDatasource", true);
                prodProfile.put("isMySql", config.getProdDatabaseType().equalsIgnoreCase("mysql"));
                prodProfile.put("isOracle", config.getProdDatabaseType().equalsIgnoreCase("oracle"));
                prodProfile.put("isPostgres", config.getProdDatabaseType().equalsIgnoreCase("postgresql"));
                prodProfile.put("isMssql", config.getProdDatabaseType().equalsIgnoreCase("mssql"));
                prodProfile.put("isMariaDb", config.getProdDatabaseType().equalsIgnoreCase("mariadb"));
            }
        }

        configMap.put("serverPort", config.getServerPort());
        configMap.put("name", config.getName());

        try {
            writeFile(outputDir, "src/main/resources/application.yml", renderTemplate(TEMPLATE_PATH + "application.yml", configMap));
            writeFile(outputDir, "src/main/resources/application-dev.yml", renderTemplate(TEMPLATE_PATH + "application-dev.yml", configMap));
            writeFile(outputDir, "src/main/resources/application-prod.yml", renderTemplate(TEMPLATE_PATH + "application-prod.yml", prodProfile));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}