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

import java.io.File;
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
    public ResponseEntity<Resource> generateProject(@Valid @RequestBody ApplicationDto applicationDto) {

        validateJavaVersion(applicationDto.getConfig().getJavaVersion());

        projectGenerationUseCase.execute(mapper.mapFrom(applicationDto), "output");
        String zipFilePath = "output.zip";
        File zipFile = new File(zipFilePath);

        if (!zipFile.exists()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null);
        }

        Resource resource = new FileSystemResource(zipFile);

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
