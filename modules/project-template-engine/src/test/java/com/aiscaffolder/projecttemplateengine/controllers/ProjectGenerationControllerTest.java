package com.aiscaffolder.projecttemplateengine.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest(properties = "spring.mustache.prefix=classpath:/templates/")
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class ProjectGenerationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void generateProject() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.post("/generate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"config\":{\"name\":\"test\",\"group\":\"com.test\",\"artifact\":\"book\",\"packageName\":\"com.example.app\",\"authenticationType\":\"JWT\",\"serverPort\":8080,\"databaseType\":\"SQL\",\"devDatabaseType\":\"H2\",\"prodDatabaseType\":\"PostgreSQL\",\"buildTool\":\"Maven\",\"applicationType\":\"MONOLITH\",\"springBootVersion\":\"3.4.2\",\"javaVersion\":\"21\",\"description\":\"Bookapplication\"},\"entities\":[{\"entityName\":\"User\",\"entityFields\":[{\"fieldName\":\"username\",\"fieldType\":\"String\"},{\"fieldName\":\"email\",\"fieldType\":\"String\"}]},{\"entityName\":\"Post\",\"entityFields\":[{\"fieldName\":\"title\",\"fieldType\":\"String\"},{\"fieldName\":\"content\",\"fieldType\":\"String\"}]}],\"relationships\":[{\"type\":\"MANY_TO_MANY\",\"fromEntity\":\"User\",\"toEntity\":\"Post\",\"fromEntityField\":\"id\",\"toEntityField\":\"userId\"},{\"type\":\"ONE_TO_ONE\",\"fromEntity\":\"User\",\"toEntity\":\"Post\",\"fromEntityField\":\"id_author\",\"toEntityField\":\"author_Id\"}],\"dependencies\":[{\"groupId\":\"org.springframework.boot\",\"artifactId\":\"spring-boot-starter-data-jpa\",\"version\":\"2.7.0\",\"scope\":\"compile\"},{\"groupId\":\"org.postgresql\",\"artifactId\":\"postgresql\",\"version\":\"42.3.3\",\"scope\":\"runtime\"}]}")
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        );
    }
}
