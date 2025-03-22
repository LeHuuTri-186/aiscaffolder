package com.aiscaffolder.aiscaffolder.application.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class ApiGenerationService {
    @Value("${api-keys.gemini.key}")
    private String geminiKey;
    private final String GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=";
    private final RestTemplate restTemplate = new RestTemplate();
    private final String SYSTEM_INSTRUCTION = """
            Generate a JSON configuration file for an AI scaffolder that defines a Spring Boot project setup. The JSON should adhere to the provided schema with clearly defined sections for application configuration, entities, relationships, and dependencies. Follow the specific instructions for field types and relationships as outlined below.
            
            Configuration (config)
            Define the following base properties for the application configuration:
            
            Name: (String) The name of the application.
            Group: (String) The group ID (e.g., com.example).
            Artifact: (String) The artifact ID (e.g., sample).
            Package Name: (String) The package name (e.g., com.example.sample).
            Description: (String) A description of the application.
            Authentication Type: (String) Options: jwt, session, oauth2, no.
            Server Port: (Integer) The port on which the server will run (e.g., 8080).
            Database Type: (String) Options: sql, mongodb, cassandra, couchbase, no.
            Dev Database Type: (String) The database type used for development (e.g., h2).
            Prod Database Type: (String) The database type used for production: postgresql, mysql, mssql, mariadb, oracle.
            Build Tool: (String) Options: maven, gradle.
            caching: (String) Options: no, redis, ehcache, hazelcast.
            Application Type: (String) Options: monolith, microservice, gateway.
            Spring Boot Version: (String) The version of Spring Boot (e.g., 3.4.2).
            Java Version: (Integer) The version of Java 21, 17, 23.
            lombokEnabled: (Boolean) Enable lombok plugin.
            hibernateEnabled: (Boolean) Enable hibernate, should be false if using NoSQL.
            
            Entities (entities)
            Each entity should have:
            
            entityName: The name of the entity (e.g., User, Project, Task).
            idFieldType: The type of the Id field (e.g., String, Integer, Long, UUID)
            idFieldName: name of id field (e.g., id)
            entityFields: A list of fields, each field should include:
            fieldName: The name of the field (e.g., id, username).
            fieldType:\s
            - The type of the field (e.g., String, Integer, LocalDate, UUID).
            - Valid field types include: String, Integer, Long, BigDecimal, Float, Double, Boolean, LocalDate, UUID.
            Relationships (relationships)
            Define how entities relate to each other using the following options:
            
            oneToMany
            manyToMany
            oneToOne
            manyToOne
            Each relationship should include:
            
            From Entity: The source entity in the relationship.
            To Entity: The destination entity in the relationship.
            
            is Bidirectional (boolean): The source entity should be a bidirectional relationship.
            
            Only one side of the relationship should be defined, and whether the relationship is bidirectional or unidirectional should be handle by the flag isBidirectional.
            
            Example Structure:
            {
                 "config": {
                     "name": "BookManagementApp",
                     "group": "com.example",
                     "artifact": "bookmanagement",
                     "packageName": "com.example.bookmanagement",
                     "description": "A Spring Boot application for managing books and authors.",
                     "authenticationType": "jwt",
                     "serverPort": 0,
                     "databaseType": "sql",
                     "devDatabaseType": "h2",
                     "prodDatabaseType": "postgresql",
                     "buildTool": "maven",
                     "caching": "hazelcast",
                     "applicationType": "monolith",
                     "springBootVersion": "3.4.2",
                     "javaVersion": 21,
                     "lombokEnabled": false,
                     "hibernateEnabled": true
                 },
                 "entities": [
                     {
                         "entityName": "Post",
                         "idFieldType": "Long",
                         "idFieldName": "id",
                         "entityFields": [
                             {
                                 "fieldName": "title",
                                 "fieldType": "String"
                             },
                             {
                                 "fieldName": "content",
                                 "fieldType": "String"
                             }
                         ]
                     },
                     {
                         "entityName": "User",
                         "idFieldType": "Long",
                         "idFieldName": "id",
                         "entityFields": [
                             {
                                 "fieldName": "name",
                                 "fieldType": "String"
                             },
                             {
                                 "fieldName": "age",
                                 "fieldType": "Integer"
                             }
                         ]
                     }
                 ],
                 "relationships": [
                     {
                         "type": "oneToMany",
                         "fromEntity": "User",
                         "toEntity": "Post",
                         "isBidirectional": false
                     }
                 ],
                 "dependencies": [
                     {
                         "groupId": "org.springframework.boot",
                         "artifactId": "spring-boot-starter-data-jpa",
                         "version": "3.4.2",
                         "scope": "compile"
                     },
                     {
                         "groupId": "org.springframework.boot",
                         "artifactId": "spring-boot-starter-web",
                         "version": "3.4.2",
                         "scope": "compile"
                     },
                     {
                         "groupId": "org.postgresql",
                         "artifactId": "postgresql",
                         "version": "42.6.0",
                         "scope": "runtime"
                     },
                     {
                         "groupId": "com.h2database",
                         "artifactId": "h2",
                         "version": "2.2.224",
                         "scope": "runtime"
                     },
                     {
                         "groupId": "org.springframework.boot",
                         "artifactId": "spring-boot-starter-test",
                         "version": "3.4.2",
                         "scope": "test"
                     }
                 ]
            }
            
            Generate a JSON file following the schema.
            Ensure that all fields and relationships are logically structured.
            Use valid field types as defined in the document (e.g., String, Integer, UUID, etc.).
    """;

    public Map<String, Object> generateAIJson(String prompt) {
        Map<String, Object> requestBody = new HashMap<>();
        Map<String, Object> systemInstructionParts = new HashMap<>();
        Map<String, String> textInstruction = new HashMap<>();
        Map<String, Object> contents = new HashMap<>();
        Map<String, Object> promptDetail = new HashMap<>();
        Map<String, Object> responseMimeType = new HashMap<>();
        List<Map<String,Object>> chatHistory = new ArrayList<>();
        responseMimeType.put("response_mime_type", "application/json");
        contents.put("text", prompt);
        promptDetail.put("role", "user");
        promptDetail.put("parts", contents);
        chatHistory.add(promptDetail);
        textInstruction.put("text", SYSTEM_INSTRUCTION);
        systemInstructionParts.put("parts", textInstruction);
        requestBody.put("contents", chatHistory);
        requestBody.put("system_instruction", systemInstructionParts);
        requestBody.put("generationConfig", responseMimeType);

        log.info(requestBody.toString());

        Map<String, Object> response = restTemplate.postForObject(GEMINI_API_URL + geminiKey, requestBody, Map.class);

        return response != null ? response : new HashMap<>();
    }
}
