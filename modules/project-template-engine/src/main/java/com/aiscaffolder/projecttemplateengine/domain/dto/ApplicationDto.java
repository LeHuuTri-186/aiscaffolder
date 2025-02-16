package com.aiscaffolder.projecttemplateengine.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationDto {
    private ConfigurationDto config;
    private List<EntityDto> entities;
    private List<RelationshipDto> relationships;
    private List<DependencyDto> dependencies;

    @Override
    public String toString() {
        return "ApplicationDto{" +
                "config=" + config +
                ", entities=" + entities +
                ", relationships=" + relationships +
                ", dependencies=" + dependencies +
                '}';
    }
}
