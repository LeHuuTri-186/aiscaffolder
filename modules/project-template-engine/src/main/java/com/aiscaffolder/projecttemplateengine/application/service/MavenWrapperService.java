package com.aiscaffolder.projecttemplateengine.application.service;

import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.*;

@Service
public class MavenWrapperService {

    private final MustacheFactory mustacheFactory;

    public MavenWrapperService(MustacheFactory mustacheFactory) {
        this.mustacheFactory = mustacheFactory;
    }

    public void generateMavenWrapper(String outputDir) throws IOException {
        renderTemplate("mvnw.mustache", outputDir + "/mvnw");
        renderTemplate("mvnw.cmd.mustache", outputDir + "/mvnw.cmd");
        renderTemplate("maven-wrapper.properties.mustache", outputDir + "/maven-wrapper.properties");

        // Đặt quyền thực thi cho mvnw (Linux/Mac)
        // setExecutable(Path.of(outputDir, "mvnw"));
    }

    private void renderTemplate(String templateName, String outputPath) throws IOException {
        Mustache mustache = mustacheFactory.compile("classpath:/templates/spring-boot/java/" + templateName);
        try (Writer writer = new FileWriter(outputPath)) {
            mustache.execute(writer, new Object()).flush();
        }
    }

//    private void setExecutable(Path path) throws IOException {
//        try {
//            Files.setPosixFilePermissions(path, PosixFilePermissions.fromString("rwxr-xr-x"));
//        } catch (UnsupportedOperationException e) {
//            path.toFile().setExecutable(true);
//        }
//    }
}

