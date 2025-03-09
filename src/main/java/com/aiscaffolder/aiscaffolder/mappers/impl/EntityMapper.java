package com.aiscaffolder.aiscaffolder.mappers.impl;

import com.aiscaffolder.aiscaffolder.domain.dto.EntityDto;
import com.aiscaffolder.aiscaffolder.domain.entities.Entity;
import com.aiscaffolder.aiscaffolder.mappers.Mapper;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class EntityMapper implements Mapper<Entity, EntityDto> {
    private final ModelMapper modelMapper;

    public EntityMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public EntityDto mapTo(Entity entity) {
        return modelMapper.map(entity, EntityDto.class);
    }

    @Override
    public Entity mapFrom(EntityDto entityDto) {
        return modelMapper.map(entityDto, Entity.class);
    }
}
