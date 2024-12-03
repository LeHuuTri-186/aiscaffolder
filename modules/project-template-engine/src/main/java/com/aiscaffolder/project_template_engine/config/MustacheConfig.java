package com.aiscaffolder.project_template_engine.config;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.MustacheFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MustacheConfig {

    @Value("${mustache.template-directory}")
    private String templateDirectory;

    @Bean
    public MustacheFactory mustacheFactory() {
        // Create MustacheFactory with the configured directory
        return new DefaultMustacheFactory(templateDirectory);
    }
}

