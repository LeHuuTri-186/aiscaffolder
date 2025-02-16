package com.aiscaffolder.projecttemplateengine.mappers.impl;

import com.aiscaffolder.projecttemplateengine.domain.dto.ConfigurationDto;
import com.aiscaffolder.projecttemplateengine.domain.entities.Configuration;
import com.aiscaffolder.projecttemplateengine.mappers.Mapper;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class ConfigurationMapper implements Mapper<Configuration, ConfigurationDto> {
    public ConfigurationMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    private final ModelMapper modelMapper;

    @Override
    public ConfigurationDto mapTo(Configuration configuration) {
        return modelMapper.map(configuration, ConfigurationDto.class);
    }

    @Override
    public Configuration mapFrom(ConfigurationDto configurationDto) {
        return modelMapper.map(configurationDto, Configuration.class);
    }
}
