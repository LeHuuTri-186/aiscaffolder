package com.aiscaffolder.aiscaffolder.mappers.impl;

import com.aiscaffolder.aiscaffolder.domain.dto.EntityFieldDto;
import com.aiscaffolder.aiscaffolder.domain.entities.EntityField;
import com.aiscaffolder.aiscaffolder.mappers.Mapper;
import org.springframework.stereotype.Component;

@Component
public class EntityFieldMapper implements Mapper<EntityFieldDto, EntityField> {
    @Override
    public EntityField mapTo(EntityFieldDto entityFieldDto) {
        return null;
    }

    @Override
    public EntityFieldDto mapFrom(EntityField entityField) {
        return null;
    }
}
