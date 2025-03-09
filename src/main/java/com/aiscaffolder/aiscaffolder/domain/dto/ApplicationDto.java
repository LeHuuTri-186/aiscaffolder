package com.aiscaffolder.aiscaffolder.domain.dto;

import jakarta.validation.Valid;
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
    @Valid
    private ConfigurationDto config;
    private List<EntityDto> entities;
    private List<RelationshipDto> relationships;
    private List<DependencyDto> dependencies;
}
