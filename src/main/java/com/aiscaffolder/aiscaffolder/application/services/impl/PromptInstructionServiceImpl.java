package com.aiscaffolder.aiscaffolder.application.services.impl;

import com.aiscaffolder.aiscaffolder.application.services.PromptInstructionService;
import com.aiscaffolder.aiscaffolder.common.constants.Constants;
import com.aiscaffolder.aiscaffolder.domain.entities.PromptInstructionConfig;
import com.aiscaffolder.aiscaffolder.repository.PromptInstructionConfigRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@AllArgsConstructor
@Service
public class PromptInstructionServiceImpl implements PromptInstructionService {

    private final PromptInstructionConfigRepository repository;

    @Override
    public Optional<PromptInstructionConfig> getBackEndPromptConfig() {
        return repository.findById(Constants.PROMPT_INSTRUCTION_CONFIG_ID);
    }
}
