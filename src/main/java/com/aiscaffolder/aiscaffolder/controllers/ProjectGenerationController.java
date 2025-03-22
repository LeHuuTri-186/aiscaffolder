package com.aiscaffolder.aiscaffolder.controllers;

import com.aiscaffolder.aiscaffolder.application.usecases.ProjectGenerationUseCase;
import com.aiscaffolder.aiscaffolder.domain.dto.ApplicationDto;
import com.aiscaffolder.aiscaffolder.domain.dto.EntityDto;
import com.aiscaffolder.aiscaffolder.domain.dto.EntityFieldDto;
import com.aiscaffolder.aiscaffolder.domain.entities.Application;
import com.aiscaffolder.aiscaffolder.exceptions.DuplicateEntity;
import com.aiscaffolder.aiscaffolder.exceptions.InvalidEntity;
import com.aiscaffolder.aiscaffolder.exceptions.UnsupportedJavaVersion;
import com.aiscaffolder.aiscaffolder.mappers.Mapper;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/project")
public class ProjectGenerationController {

    final ProjectGenerationUseCase projectGenerationUseCase;
    final Mapper<Application, ApplicationDto> mapper;

    @PostMapping("/generate")
    @CrossOrigin(origins = "http://localhost:5173")
    public ResponseEntity<Resource> generateProject(@Valid @RequestBody ApplicationDto applicationDto) throws IOException {
        validateEntities(applicationDto.getEntities());
        validateJavaVersion(applicationDto.getConfig().getJavaVersion());

        String timestamp = String.valueOf(System.currentTimeMillis());
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        String outputDirectory = "output/project-" + timestamp + "-" + uuid;

        Files.createDirectories(Paths.get(outputDirectory));

        projectGenerationUseCase.execute(mapper.mapFrom(applicationDto), outputDirectory);
        String zipFileName = applicationDto.getConfig().getArtifact() + ".zip";

        File zipFile = new File(zipFileName);
        Path zipFilePath = Paths.get(outputDirectory, zipFileName);

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

    private void validateEntities(List<EntityDto> entityDtos) {
        Set<String> nameSet = new HashSet<>();

        entityDtos.forEach(entity -> {
            if (!nameSet.add(entity.getEntityName())) {
                throw new DuplicateEntity("Duplicate entity name found: " + entity.getEntityName());
            }
            validateEntity(entity);
        });
    }

    private void validateEntity(EntityDto entityDto) {
        if (entityDto.getEntityName() == null || entityDto.getEntityName().isEmpty()) {
            throw new InvalidEntity("Invalid entity name value:" + entityDto.getEntityName());
        }

        validateEntityFields(entityDto.getEntityFields());
    }

    private void validateEntityFields(List<EntityFieldDto> entityFieldDtos) {
        Set<String> nameSet = new HashSet<>();

        entityFieldDtos.forEach(entity -> {
            if (null == entity.getFieldName() || entity.getFieldName().isEmpty()) {
                throw new InvalidEntity("Invalid field name value: " + entity.getFieldName());
            }

            if (!nameSet.add(entity.getFieldName())) {
                throw new DuplicateEntity("Duplicate entity field found: " + entity.getFieldName());
            }

            if ("id".equals(entity.getFieldName())) {
                throw new InvalidEntity("An id field should not be manually added: " + entity.getFieldName());
            }
        });
    }
}
