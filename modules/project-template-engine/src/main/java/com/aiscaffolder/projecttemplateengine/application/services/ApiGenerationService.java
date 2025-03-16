package com.aiscaffolder.projecttemplateengine.application.services;

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
            Authentication Type: (String) Options: JWT, SESSION, OAUTH2.
            Server Port: (Integer) The port on which the server will run (e.g., 8080).
            Database Type: (String) Options: SQL, MONGO, CASSANDRA, COUCHBASE.
            Dev Database Type: (String) The database type used for development (e.g., H2).
            Prod Database Type: (String) The database type used for production (e.g., PostgreSQL).
            Build Tool: (String) Options: Maven, Gradle.
            Application Type: (String) Options: MONOLITH, MICROSERVICE, GATEWAY.
            Spring Boot Version: (String) The version of Spring Boot (e.g., 3.4.2).
            Java Version: (String) The version of Java (e.g., 21).
            Entities (entities)
            Each entity should have:
            
            entityName: The name of the entity (e.g., User, Project, Task).
            entityFields: A list of fields, each field should include:
            fieldName: The name of the field (e.g., id, username).
            fieldType:\s
            - The type of the field (e.g., String, Integer, LocalDate, UUID).
            - Valid field types include: String, Integer, Long, BigDecimal, Float, Double, Enum, Boolean, LocalDate, ZoneDateTime, Instant, Duration, UUID, Blob, AnyBlob, ImageBlob, TextBlob.
            Relationships (relationships)
            Define how entities relate to each other using the following options:
            
            ONE_TO_MANY
            MANY_TO_MANY
            ONE_TO_ONE
            MANY_TO_ONE
            Each relationship should include:
            
            From Entity: The source entity in the relationship.
            To Entity: The destination entity in the relationship.
            From Field: The field in the source entity that holds the relationship.
            To Field: The field in the destination entity that holds the relationship.
            Short form for Bidirectional Relationships: If both fromField and toField are omitted, the relationship is considered bidirectional by default.
            
            Example Structure:
            {
              "config": {
                "name": "SampleApp",
                "group": "com.example",
                "artifact": "sample",
                "packageName": "com.example.sample",
                "authenticationType": "JWT",
                "serverPort": 8080,
                "databaseType": "SQL",
                "devDatabaseType": "H2",
                "prodDatabaseType": "PostgreSQL",
                "buildTool": "Maven",
                "applicationType": "MONOLITH",
                "springBootVersion": "3.4.2",
                "javaVersion": "21",
                "description": "AI-generated Spring Boot project"
              },
              "entities": [
                {
                  "entityName": "User",
                  "entityFields": [
                    { "fieldName": "id", "fieldType": "Long" },
                    { "fieldName": "username", "fieldType": "String" },
                    { "fieldName": "email", "fieldType": "String" },
                    { "fieldName": "createdAt", "fieldType": "LocalDate" }
                  ]
                },
                {
                  "entityName": "Project",
                  "entityFields": [
                    { "fieldName": "id", "fieldType": "Long" },
                    { "fieldName": "name", "fieldType": "String" },
                    { "fieldName": "description", "fieldType": "String" },
                    { "fieldName": "startDate", "fieldType": "LocalDate" },
                    { "fieldName": "endDate", "fieldType": "LocalDate" }
                  ]
                }
              ],
              "relationships": [
                {
                  "type": "ONE_TO_MANY",
                  "fromEntity": "Project",
                  "fromField": "tasks",
                  "toEntity": "Task",
                  "toField": "project"
                },
                {
                  "type": "MANY_TO_ONE",
                  "fromEntity": "Task",
                  "fromField": "user",
                  "toEntity": "User",
                  "toField": "tasks"
                }
              ],
              "dependencies": [
                {
                  "groupId": "org.springframework.boot",
                  "artifactId": "spring-boot-starter-data-jpa",
                  "version": "2.7.0",
                  "scope": "compile"
                },
                {
                  "groupId": "org.postgresql",
                  "artifactId": "postgresql",
                  "version": "42.3.3",
                  "scope": "runtime"
                }
              ]
            }
            
            Generate a JSON file following the schema.
            Ensure that all fields and relationships are logically structured.
            Use valid field types as defined in the document (e.g., String, Integer, UUID, etc.).
            Keep it fully structured and formatted correctly.
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
