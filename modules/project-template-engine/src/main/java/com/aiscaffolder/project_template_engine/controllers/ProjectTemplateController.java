package com.aiscaffolder.project_template_engine.controllers;

import com.aiscaffolder.project_template_engine.application.service.ProjectGenerationService;
import com.aiscaffolder.project_template_engine.domain.model.dto.ProjectTemplateDto;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProjectTemplateController {
    private ProjectGenerationService projectGenerationService;

    public ProjectTemplateController(ProjectGenerationService projectGenerationService) {
        this.projectGenerationService = projectGenerationService;
    }

    @PostMapping(path = "/generate-project")
    public ProjectTemplateDto createProject(@RequestBody ProjectTemplateDto projectTemplateDto) {
        return new ProjectTemplateDto();
    }
}
