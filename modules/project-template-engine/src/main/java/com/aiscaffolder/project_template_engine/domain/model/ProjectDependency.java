package com.aiscaffolder.project_template_engine.domain.model;

import lombok.Builder;

@Builder
public record ProjectDependency (String groupId, String artifactId, String version, String scope) {}
