package com.aiscaffolder.projecttemplateengine.mappers.impl;

import com.aiscaffolder.projecttemplateengine.domain.dto.EntityFieldDto;
import com.aiscaffolder.projecttemplateengine.domain.entities.EntityField;
import com.aiscaffolder.projecttemplateengine.mappers.Mapper;
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
