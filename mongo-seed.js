db = db.getSiblingDB(process.env.MONGO_DB || 'aiscaffolder'); // Database name

db.createCollection("product_configs");

db.product_configs.insertMany([
    {
        "_id": "BACK_END_PROMPT_CONFIG",
        "description": "Instruction for generating Spring boot back-end JSON",
        "promptExample": `Example Structure:
        {
        analysis: "The project is a Book Management Service. It includes entities like Book, Author, and Category. Books can have multiple authors and belong to multiple categories.  The system needs to manage these relationships effectively. Authentication type is JWT. Caching mechanism is Redis. Lombok and Hibernate are enabled.",
        project: {
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
                     "groupId": "org.postgresql",
                     "artifactId": "postgresql",
                     "version": "42.6.0",
                     "scope": "runtime"
                 }
             ]
        }
    }`,
        "instruction": `    Based on the given prompt:
    1. Analyze the possible use cases and project structures.
    2. Generate the entities based on the analysis result.
    3. Generate the possible relationships based on the analysis result.
    4. Generate the suitable configurations based on the analysis result.
    5. Generate allowed dependencies while omitting the restricted ones.
    6. Return the generated project JSON and remarks on the key selling point of the project.`,
        "context": `Generate a JSON configuration file that defines a Spring Boot project setup. The JSON should adhere to the provided schema with clearly defined sections for application configuration, entities, relationships, and dependencies. Follow the specific instructions for field types and relationships as outlined below.

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
    fieldType:
    - The type of the field (e.g., String, Integer, LocalDate, UUID).
    - Valid field types include: String, Integer, Long, Float, Double, Boolean, LocalDate, UUID.
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
    
    Dependencies for the following artifactId: { lombok, spring-boot-starter-security, jjwt, spring-boot-starter-web, spring-security-web, spring-boot-starter-data-jpa, spring-boot-starter-cache, ehcache, spring-boot-starter-web, spring-boot-starter-tomcat, spring-boot-starter-test } should not be included.
    Any other dependencies that are not listed but required should be included:
    Example:
    {
         "groupId": "org.postgresql",
         "artifactId": "postgresql",
         "version": "42.6.0",
         "scope": "runtime"
    }
    `,
        "version": 1
    },
]);
