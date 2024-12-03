package com.aiscaffolder.domain.model;

import lombok.*;

import java.util.Map;


@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ProjectTemplate {
    private String name;
    private String description;
    private Map<String, String> files;
}