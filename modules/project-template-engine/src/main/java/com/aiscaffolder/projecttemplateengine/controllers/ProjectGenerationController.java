package com.aiscaffolder.projecttemplateengine.controllers;

import com.aiscaffolder.projecttemplateengine.application.usecase.ProjectGenerationUseCase;
import com.aiscaffolder.projecttemplateengine.domain.dto.ApplicationDto;
import com.aiscaffolder.projecttemplateengine.domain.entities.Application;
import com.aiscaffolder.projecttemplateengine.mappers.Mapper;
import com.aiscaffolder.projecttemplateengine.mappers.impl.ApplicationMapper;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@RestController
public class ProjectGenerationController {

    final ProjectGenerationUseCase projectGenerationUseCase;
    final Mapper<Application, ApplicationDto> mapper;

    public ProjectGenerationController(ProjectGenerationUseCase projectGenerationUseCase, Mapper<Application, ApplicationDto> mapper) {
        this.projectGenerationUseCase = projectGenerationUseCase;
        this.mapper = mapper;
    }

    @PostMapping("/generate")
    public ResponseEntity<Resource> generateProject(@RequestBody ApplicationDto applicationDto) {
        System.out.println(applicationDto.toString());
        System.out.println(mapper.mapFrom(applicationDto));
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
}
