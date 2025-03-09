package com.aiscaffolder.aiscaffolder.application.services;

import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

@Service
public class GradleWrapperService {

    private final MustacheFactory mustacheFactory;

    public GradleWrapperService(MustacheFactory mustacheFactory) {
        this.mustacheFactory = mustacheFactory;
    }

    public void generateGradleWrapper(String outputDir) throws IOException {
        // Generate gradlew and gradlew.bat for Linux/Mac/Windows
        renderTemplate("gradlew.mustache", outputDir + "/gradlew");
        renderTemplate("gradlew.bat.mustache", outputDir + "/gradlew.bat");

        // Generate gradle wrapper properties file
        renderTemplate("gradle-wrapper.properties.mustache", outputDir + "/gradle-wrapper.properties");

        // Set executable for gradlew (Linux/Mac)
        // setExecutable(Path.of(outputDir, "gradlew"));
    }

    private void renderTemplate(String templateName, String outputPath) throws IOException {
        Mustache mustache = mustacheFactory.compile("classpath:/templates/spring-boot/java/" + templateName);
        try (Writer writer = new FileWriter(outputPath)) {
            mustache.execute(writer, new Object()).flush();
        }
    }

    // Optional: Set executable for gradlew (Linux/Mac)
    // private void setExecutable(Path path) throws IOException {
    //     Files.setPosixFilePermissions(path, PosixFilePermissions.fromString("rwxr-xr-x"));
    // }
}
