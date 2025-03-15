package com.aiscaffolder.projecttemplateengine.application.services.impl;

import com.aiscaffolder.projecttemplateengine.application.services.WrapperService;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class MavenWrapperServiceImpl implements WrapperService {

    private final MustacheFactory mustacheFactory;

    public MavenWrapperServiceImpl(MustacheFactory mustacheFactory) {
        this.mustacheFactory = mustacheFactory;
    }

    @Override
    public void generateWrapper(String outputDir) throws IOException {
        renderTemplate("mvnw.mustache", outputDir + "/mvnw");
        renderTemplate("mvnw.cmd.mustache", outputDir + "/mvnw.cmd");
        Files.createDirectories(Path.of(outputDir + "/.mvn/wrapper"));
        renderTemplate("maven-wrapper.properties.mustache", outputDir + "/.mvn/wrapper/maven-wrapper.properties");
    }

    @Override
    public void setExecutable(Path executable) {

    }

    private void renderTemplate(String templateName, String outputPath) throws IOException {
        Mustache mustache = mustacheFactory.compile("classpath:/templates/spring-boot/java/" + templateName);
        try (Writer writer = new FileWriter(outputPath)) {
            mustache.execute(writer, new Object()).flush();
        }
    }
}

