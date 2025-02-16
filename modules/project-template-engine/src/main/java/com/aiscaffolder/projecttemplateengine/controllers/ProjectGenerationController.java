package com.aiscaffolder.projecttemplateengine.controllers;

import com.aiscaffolder.projecttemplateengine.application.usecase.ProjectGenerationUseCase;
import com.aiscaffolder.projecttemplateengine.domain.dto.ApplicationDto;
import com.aiscaffolder.projecttemplateengine.domain.entities.Application;
import com.aiscaffolder.projecttemplateengine.mappers.Mapper;
import com.aiscaffolder.projecttemplateengine.mappers.impl.ApplicationMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProjectGenerationController {

    final ProjectGenerationUseCase projectGenerationUseCase;
    final Mapper<Application, ApplicationDto> mapper;

    public ProjectGenerationController(ProjectGenerationUseCase projectGenerationUseCase, Mapper<Application, ApplicationDto> mapper) {
        this.projectGenerationUseCase = projectGenerationUseCase;
        this.mapper = mapper;
    }

    @PostMapping("/generate")
    public String generateProject(@RequestBody ApplicationDto applicationDto) {
        System.out.println(applicationDto.toString());
        System.out.println(mapper.mapFrom(applicationDto));
        projectGenerationUseCase.execute(mapper.mapFrom(applicationDto), "output");
        return "Generated project";
    }
}
