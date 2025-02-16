package com.aiscaffolder.projecttemplateengine.mappers.impl;

import com.aiscaffolder.projecttemplateengine.domain.model.ProjectTemplate;
import com.aiscaffolder.projecttemplateengine.domain.model.dto.ProjectTemplateDto;
import com.aiscaffolder.projecttemplateengine.mappers.Mapper;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class ProjectTemplateMapper implements Mapper<ProjectTemplate, ProjectTemplateDto> {

    private final ModelMapper modelMapper;

    ProjectTemplateMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public ProjectTemplateDto mapTo(ProjectTemplate source) {
        return modelMapper.map(source, ProjectTemplateDto.class);
    }

    @Override
    public ProjectTemplate mapFrom(ProjectTemplateDto target) {
        return modelMapper.map(target, ProjectTemplate.class);
    }
}
