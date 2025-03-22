package com.aiscaffolder.aiscaffolder.application.services.impl;

import com.aiscaffolder.aiscaffolder.application.services.WrapperService;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class GradleWrapperServiceImpl implements WrapperService {

    private final MustacheFactory mustacheFactory;

    @Override
    public void generateWrapper(String outputDir) throws Exception {
        File projectDir = new File(outputDir);

        if (!projectDir.exists()) {
            log.warn("The specified folder does not exist: {}", outputDir);

            return;
        }

        try {
            ProcessBuilder processBuilder = new ProcessBuilder("gradle", "wrapper");
            processBuilder.directory(projectDir);
            processBuilder.redirectErrorStream(true);

            Process process = processBuilder.start();

            int exitCode = process.waitFor();
            if (exitCode == 0) {
                log.debug("Gradle Wrapper generated successfully in: {}", outputDir);
            } else {
                log.debug("Failed to generate Gradle Wrapper. Exit code: {}", exitCode);
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void generateGradleSettings(String outputDir, String artifact) throws Exception {
        Map<String, String> settings = new HashMap<>();
        settings.put("artifact", artifact);

        String content = renderTemplate("classpath:/templates/spring-boot/java/settings.gradle.mustache", settings);

        writeFile(outputDir, "settings.gradle", content);
    }

    private void writeFile(String outputDir, String relativePath, String fileContent) throws Exception {
        Path filePath = Path.of(outputDir, relativePath);
        Files.createDirectories(filePath.getParent());
        Files.writeString(filePath, fileContent);
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
}
