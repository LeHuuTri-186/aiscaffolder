package com.aiscaffolder.aiscaffolder.repository;

import com.aiscaffolder.aiscaffolder.domain.entities.PromptInstructionConfig;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PromptInstructionConfigRepository extends MongoRepository<PromptInstructionConfig, String> {
}
