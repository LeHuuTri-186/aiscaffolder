package com.aiscaffolder.aiscaffolder.domain.entities;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "product_configs")
@Data
@Slf4j
public class PromptInstructionConfig {
    @Id
    private String id;
    private String description;
    private String promptExample;
    private String instruction;
    private String context;

    @Version
    private long version;

    @Override
    public String toString() {
        return String.format("%s\n%s\n%s", context, promptExample, instruction);
    }
}
