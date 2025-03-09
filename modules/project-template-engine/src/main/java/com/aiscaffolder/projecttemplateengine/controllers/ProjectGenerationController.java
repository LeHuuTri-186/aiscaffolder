package com.aiscaffolder.projecttemplateengine.controllers;

import com.aiscaffolder.projecttemplateengine.application.usecases.ProjectGenerationUseCase;
import com.aiscaffolder.projecttemplateengine.domain.dto.ApplicationDto;
import com.aiscaffolder.projecttemplateengine.domain.entities.Application;
import com.aiscaffolder.projecttemplateengine.exceptions.UnsupportedJavaVersion;
import com.aiscaffolder.projecttemplateengine.mappers.Mapper;
import jakarta.validation.Valid;
import org.springframework.boot.system.JavaVersion;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


import java.io.File;
import java.nio.file.Paths;
import java.util.Arrays;

@RestController
public class ProjectGenerationController {

    final ProjectGenerationUseCase projectGenerationUseCase;
    final Mapper<Application, ApplicationDto> mapper;

    public ProjectGenerationController(ProjectGenerationUseCase projectGenerationUseCase, Mapper<Application, ApplicationDto> mapper) {
        this.projectGenerationUseCase = projectGenerationUseCase;
        this.mapper = mapper;
    }

    @PostMapping("/generate")
    public ResponseEntity<Resource> generateProject(@Valid @RequestBody ApplicationDto applicationDto) throws IOException {

        validateJavaVersion(applicationDto.getConfig().getJavaVersion());

        // Tạo thư mục riêng biệt dựa vào timestamp
        String timestamp = String.valueOf(System.currentTimeMillis());
        String outputDirectory = "output/project-" + timestamp;

        Files.createDirectories(Paths.get(outputDirectory));

        projectGenerationUseCase.execute(mapper.mapFrom(applicationDto), outputDirectory);
        String zipFileName = "output.zip";
//        Path zipFilePath = Paths.get(outputDir, zipFileName);
//        String zipFilePath = "output.zip";
        File zipFile = new File(zipFileName);
        Path zipFilePath = (Path) Paths.get(outputDirectory, zipFileName);

//        if (!zipFile.exists()) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND)
//                    .body(null);
//        }

        Resource resource = new FileSystemResource(zipFilePath.toString());

        if (!resource.exists()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        // Set response headers
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + zipFile.getName());
        headers.add(HttpHeaders.CONTENT_TYPE, "application/zip");

        return ResponseEntity.ok()
                .headers(headers)
                .body(resource);
    }

    private void validateJavaVersion(int javaVersion) {
        if (javaVersion == 17 || javaVersion == 21 || javaVersion == 23) {
            return;
        }

        throw new UnsupportedJavaVersion("Unsupported Java Version: " + javaVersion + ". Supported Java Versions: 17, 21, 23");
    }
}
