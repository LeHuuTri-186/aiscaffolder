package com.aiscaffolder.aiscaffolder.application.services.impl;

import com.aiscaffolder.aiscaffolder.application.services.GenerateProjectService;
import com.aiscaffolder.aiscaffolder.application.services.ZipProjectService;
import com.aiscaffolder.aiscaffolder.domain.entities.*;
import com.aiscaffolder.aiscaffolder.domain.enums.BuildTool;
import com.aiscaffolder.aiscaffolder.domain.enums.CachingSolution;
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

            String fileContent = renderTemplate(TEMPLATE_PATH + templateName, application);

            writeFile(outputDir, relativeFilePath, fileContent);
        }

        generateCachingFiles(application.getEntities(), application.getConfig(), outputDir);

        generateApplicationFile(application.getConfig(), outputDir);

        generateDockerCompose(application.getConfig(), outputDir);

        generateRepositories(application.getEntities(), application.getConfig(), outputDir);

        generateConfigurations(application.getConfig(), outputDir);

        generateEntities(application.getEntities(), application.getConfig(), application.getRelationships(), outputDir);

        generateServices(application.getEntities(), application.getConfig(), outputDir);

        generateControllers(application.getEntities(), application.getConfig(), outputDir);

        generateDependencies(application.getConfig(), outputDir);

        zipProjectService.zipProject(outputDir,  "../../" + application.getConfig().getArtifact() + ".zip");
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

    private String uncapitalize(String str) {
        return Character.toLowerCase(str.charAt(0)) + str.substring(1);
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
    public void generateApplicationFile(Configuration config, String outputDir) {
        Map<String, Object> configMap = new HashMap<>();

        configMap.put("caching", config.getCaching() != CachingSolution.NO);
        configMap.put("packageName", config.getPackageName());
        configMap.put("name", config.getName());

        String entityFilePath = "src/main/java/" + config.getPackageName().replace('.', '/') + "/" + config.getName() + "Application.java";

        try {
            writeFile(outputDir, entityFilePath, renderTemplate(TEMPLATE_PATH + "MainApplication.java", configMap));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void generateWrapper() {

    }

    @Override
    public void generateDockerCompose(Configuration config, String outputDir) {

        if (config.getDatabaseType() == DatabaseType.NO) {
            return;
        }

        Map<String, Object> configMap = new HashMap<>();
        configMap.put("dbConfig", true);
        configMap.put("depends", true);
        configMap.put("name", config.getName());
        configMap.put("nameLower", config.getName().toLowerCase());
        configMap.put("port", config.getServerPort());
        configMap.put("javaVersion", config.getJavaVersion());
        boolean caching = config.getCaching() != CachingSolution.NO;

        configMap.put("caching", caching);
        configMap.put(config.getCaching().getValue(), caching);

        handleDatabaseType(config, configMap);

        try {
            writeFile(outputDir, "docker-compose.yml", renderTemplate(TEMPLATE_PATH + "docker-compose.yml", configMap));
            writeFile(outputDir, "Dockerfile", renderTemplate(TEMPLATE_PATH + "Dockerfile", configMap));
            writeFile(outputDir, ".dockerignore", renderTemplate(TEMPLATE_PATH + ".dockerignore", configMap));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void handleDatabaseType(Configuration config, Map<String, Object> configMap) {
        if (config.getDatabaseType() == DatabaseType.SQL) {
            configMap.put("isDatasource", true);
            configMap.put("isMySql", config.getProdDatabaseType().equalsIgnoreCase("mysql"));
            configMap.put("isOracle", config.getProdDatabaseType().equalsIgnoreCase("oracle"));
            configMap.put("isPostgres", config.getProdDatabaseType().equalsIgnoreCase("postgresql"));
            configMap.put("isMssql", config.getProdDatabaseType().equalsIgnoreCase("mssql"));
            configMap.put("isMariaDb", config.getProdDatabaseType().equalsIgnoreCase("mariadb"));
        }

        if (config.getCaching() == CachingSolution.REDIS) {
            configMap.put("isData", true);
        }
    }

    @Override
    public void generateCachingFiles(List<Entity> entities, Configuration configuration, String outputDir) {
        if (CachingSolution.NO == configuration.getCaching()) {
            return;
        }

        Map<String, Object> cachingContext = new HashMap<>();

        List<Map<String, String>> entitiesContext = new ArrayList<>();

        entities.forEach(entity -> {
            Map<String, String> entityContext = new HashMap<>();
            entityContext.put("entityName", uncapitalize(entity.getEntityName()));
            entitiesContext.add(entityContext);
        });

        cachingContext.put("entities", entitiesContext);
        cachingContext.put(configuration.getCaching().getValue(), true);
        cachingContext.put("packageName", configuration.getPackageName());

        if (CachingSolution.EHCACHE == configuration.getCaching()) {
            String configContent = renderTemplate(TEMPLATE_PATH + "ehcache.xml.mustache", cachingContext);

            String entityFilePath = "src/main/resources/ehcache.xml";

            try {
                writeFile(outputDir, entityFilePath, configContent);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        String configContent = renderTemplate(TEMPLATE_PATH + "cacheConfig.java.mustache", cachingContext);

        String entityFilePath = "src/main/java/" + configuration.getPackageName().replace('.', '/') + "/config/CacheConfig.java";

        try {
            writeFile(outputDir, entityFilePath, configContent);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void generateDependencies(Configuration configuration, String outputDir) {
        Map<String, Object> dependencies = new HashMap<>();

        dependencies.put("springBootVersion", configuration.getSpringBootVersion());
        dependencies.put("javaVersion", configuration.getJavaVersion());
        dependencies.put("group", configuration.getGroup());
        dependencies.put("artifact", configuration.getArtifact());

        boolean caching = configuration.getCaching() != CachingSolution.NO;

        dependencies.put("caching", caching);
        dependencies.put(configuration.getCaching().getValue(), caching);

        dependencies.put("hibernateEnabled", configuration.getHibernateEnabled());
        dependencies.put("lombokEnabled", configuration.getLombokEnabled());

        dependencies.put("jwtAuthEnabled", !configuration.getAuthenticationType().equalsIgnoreCase("no"));

        handleDatabaseType(configuration, dependencies);

        if (configuration.getBuildTool() == BuildTool.MAVEN) {
            dependencies.put("description", configuration.getDescription());
            dependencies.put("name", configuration.getName());
            String content = renderTemplate(TEMPLATE_PATH + "pom.xml.mustache", dependencies);

            try {
                writeFile(outputDir, "pom.xml", content);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            String content = renderTemplate(TEMPLATE_PATH + "build.gradle.mustache",  dependencies);

            try {
                writeFile(outputDir, "build.gradle", content);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void generateControllers(List<Entity> entities, Configuration configuration, String outputDir) {
        for (Entity entity : entities) {
            Map<String, Object> controllerContext = new HashMap<>();

            controllerContext.put("entity", entity.getEntityName());
            controllerContext.put("entityLower", uncapitalize(entity.getEntityName()));
            controllerContext.put("packageName", configuration.getPackageName());
            controllerContext.put("idFieldType", entity.getIdFieldType());

            String content = renderTemplate(TEMPLATE_PATH + "controller.java.mustache", controllerContext);

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
            Map<String, Object> serviceContext = new HashMap<>();

            serviceContext.put("entity", entity.getEntityName());
            serviceContext.put("entityLower", uncapitalize(entity.getEntityName()));
            serviceContext.put("packageName", configuration.getPackageName());
            serviceContext.put("idFieldType", entity.getIdFieldType());

            serviceContext.put("caching", configuration.getCaching() != CachingSolution.NO);

            String content = renderTemplate(TEMPLATE_PATH + "service.java.mustache", serviceContext);

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

            entityRepositoryContext.put("jpa", configuration.getHibernateEnabled());

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

            handleEntitiesRelationships(relationships, entity, relationshipContexts);

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

    private void handleEntitiesRelationships(List<Relationship> relationships, Entity entity, List<Map<String, Object>> relationshipContexts) {
        relationships.forEach(relationship -> {
            Map<String, Object> relationshipContext = new HashMap<>();
            Map<RelationshipType, String> relationshipMapping = Map.of(
                RelationshipType.ONE_TO_ONE, "isOneToOne",
                RelationshipType.MANY_TO_ONE, "isManyToOne",
                RelationshipType.ONE_TO_MANY, "isOneToMany",
                RelationshipType.MANY_TO_MANY, "isManyToMany"
            );

            String contextKey = relationshipMapping.get(relationship.getType());
            if (contextKey != null) {
                relationshipContext.put(contextKey, true);
            }

            relationshipContext.put("targetClass", relationship.getToEntity());
            relationshipContext.put("sourceClass", relationship.getFromEntity());
            relationshipContext.put("sourceClassLower", relationship.getFromEntity().toLowerCase());
            relationshipContext.put("targetClassLower", relationship.getToEntity().toLowerCase());
            relationshipContext.put("isBidirectional", relationship.getIsBidirectional());
            relationshipContext.put("nameLower", entity.getEntityName().toLowerCase());

            if (relationship.getFromEntity().equals(entity.getEntityName())) {
                relationshipContext.put("isSourceClass", true);
            }

            if (relationship.getToEntity().equals(entity.getEntityName())) {
                relationshipContext.put("isTargetClass", true);
            }

            relationshipContexts.add(relationshipContext);
        });
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

        boolean caching = config.getCaching() != CachingSolution.NO;

        configMap.put("caching", caching);
        configMap.put("nameLower", config.getName().toLowerCase());
        prodProfile.put("nameLower", config.getName().toLowerCase());
        configMap.put(config.getCaching().getValue(), caching);

        if (config.getDatabaseType() != DatabaseType.NO) {
            configMap.put("dbConfig", true);

            handleDatabaseType(config, configMap);

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