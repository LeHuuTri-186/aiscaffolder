package com.aiscaffolder.projecttemplateengine.mappers.impl;

import com.aiscaffolder.projecttemplateengine.domain.dto.ApplicationDto;
import com.aiscaffolder.projecttemplateengine.domain.entities.Application;
import com.aiscaffolder.projecttemplateengine.mappers.Mapper;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class ApplicationMapper implements Mapper<Application, ApplicationDto> {

    private final ModelMapper modelMapper;

    public ApplicationMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public ApplicationDto mapTo(Application application) {
        return modelMapper.map(application, ApplicationDto.class);
    }

    @Override
    public Application mapFrom(ApplicationDto applicationDto) {
        return modelMapper.map(applicationDto, Application.class);
    }
}
