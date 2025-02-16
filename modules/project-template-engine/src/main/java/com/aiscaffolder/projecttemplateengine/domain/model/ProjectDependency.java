package com.aiscaffolder.projecttemplateengine.domain.model;

import lombok.Builder;

@Builder
public record ProjectDependency (String groupId, String artifactId, String version, String scope) {}
