package com.aiscaffolder.project_template_engine.mappers.impl;

import com.aiscaffolder.project_template_engine.domain.model.ProjectTemplate;
import com.aiscaffolder.project_template_engine.domain.model.dto.ProjectTemplateDto;
import com.aiscaffolder.project_template_engine.mappers.Mapper;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class ProjectTemplateMapper implements Mapper<ProjectTemplate, ProjectTemplateDto> {

    private ModelMapper modelMapper;

    ProjectTemplateMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public ProjectTemplateDto mapTo(ProjectTemplate projectTemplate) {
        return modelMapper.map(projectTemplate, ProjectTemplateDto.class);
    }

    @Override
    public ProjectTemplate mapFrom(ProjectTemplateDto projectTemplateDto) {
        return modelMapper.map(projectTemplateDto, ProjectTemplate.class);
    }
}
