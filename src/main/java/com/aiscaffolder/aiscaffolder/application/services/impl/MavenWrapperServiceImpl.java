package com.aiscaffolder.aiscaffolder.application.services.impl;

import com.aiscaffolder.aiscaffolder.application.services.WrapperService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
@Service
public class MavenWrapperServiceImpl implements WrapperService {

    @Override
    public void generateWrapper(String outputDir) throws IOException {
        File projectDir = new File(outputDir);

        if (!projectDir.exists()) {
            log.warn("The specified folder does not exist: {}", outputDir);
            return;
        }

        try {
            // Create .mvn/wrapper directory
            File mvnWrapperDir = new File(projectDir, ".mvn/wrapper");
            if (!mvnWrapperDir.exists()) {
                mvnWrapperDir.mkdirs();
            }

            // Copy maven-wrapper.properties
            copyResourceFile("templates/spring-boot/java/maven-wrapper.properties.mustache", 
                new File(mvnWrapperDir, "maven-wrapper.properties"));

            // Copy mvnw script
            copyResourceFile("templates/spring-boot/java/mvnw.mustache", 
                new File(projectDir, "mvnw"));

            // Copy mvnw.cmd script
            copyResourceFile("templates/spring-boot/java/mvnw.cmd.mustache", 
                new File(projectDir, "mvnw.cmd"));

            // Set executable permissions for mvnw on Unix-like systems
            if (!System.getProperty("os.name").toLowerCase().contains("windows")) {
                new File(projectDir, "mvnw").setExecutable(true);
            }

            log.info("Maven Wrapper generated successfully in: {}", outputDir);
        } catch (IOException e) {
            log.error("Failed to generate Maven Wrapper", e);
            throw e;
        }
    }

    private void copyResourceFile(String resourcePath, File targetFile) throws IOException {
        ClassPathResource resource = new ClassPathResource(resourcePath);
        String content = StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
        
        try (FileOutputStream outputStream = new FileOutputStream(targetFile)) {
            outputStream.write(content.getBytes(StandardCharsets.UTF_8));
        }
    }
}

