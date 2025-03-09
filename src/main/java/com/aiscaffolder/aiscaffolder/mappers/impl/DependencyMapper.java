package com.aiscaffolder.aiscaffolder.mappers.impl;

import com.aiscaffolder.aiscaffolder.domain.dto.DependencyDto;
import com.aiscaffolder.aiscaffolder.domain.entities.Dependency;
import com.aiscaffolder.aiscaffolder.mappers.Mapper;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class DependencyMapper implements Mapper<Dependency, DependencyDto> {

    private final ModelMapper modelMapper;

    public DependencyMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public DependencyDto mapTo(Dependency dependency) {
        return modelMapper.map(dependency, DependencyDto.class);
    }

    @Override
    public Dependency mapFrom(DependencyDto dependencyDto) {
        return modelMapper.map(dependencyDto, Dependency.class);
    }
}
