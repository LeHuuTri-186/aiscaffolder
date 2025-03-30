package com.aiscaffolder.aiscaffolder.application.services;

import com.aiscaffolder.aiscaffolder.domain.entities.PromptInstructionConfig;

import java.util.Optional;

public interface PromptInstructionService {
    Optional<PromptInstructionConfig> getBackEndPromptConfig();
}