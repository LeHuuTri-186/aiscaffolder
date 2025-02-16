package com.aiscaffolder.projecttemplateengine.mappers.impl;

import com.aiscaffolder.projecttemplateengine.domain.dto.RelationshipDto;
import com.aiscaffolder.projecttemplateengine.domain.entities.Relationship;
import com.aiscaffolder.projecttemplateengine.mappers.Mapper;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class RelationshipMapper implements Mapper<Relationship, RelationshipDto> {

    private final ModelMapper modelMapper;

    public RelationshipMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public RelationshipDto mapTo(Relationship relationship) {
        return modelMapper.map(relationship, RelationshipDto.class);
    }

    @Override
    public Relationship mapFrom(RelationshipDto relationshipDto) {
        return modelMapper.map(relationshipDto, Relationship.class);
    }
}
