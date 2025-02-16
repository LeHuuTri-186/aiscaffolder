package com.aiscaffolder.projecttemplateengine;

import com.aiscaffolder.projecttemplateengine.application.usecase.ProjectGenerationUseCase;
import com.aiscaffolder.projecttemplateengine.domain.model.DataSource;
import com.aiscaffolder.projecttemplateengine.domain.model.ProjectDependency;
import com.aiscaffolder.projecttemplateengine.domain.model.ProjectMetaData;
import com.aiscaffolder.projecttemplateengine.domain.model.ProjectTemplate;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.LocalDateTime;
import java.util.List;

@SpringBootApplication(scanBasePackages = "com.aiscaffolder")
public class ProjectTemplateEngineApplication implements CommandLineRunner {

	private final ProjectGenerationUseCase projectGenerationUseCase;

    public ProjectTemplateEngineApplication(ProjectGenerationUseCase projectGenerationUseCase) {
        this.projectGenerationUseCase = projectGenerationUseCase;
    }

	@Override
	public void run(String... args) throws Exception {
	}

    public static void main(String[] args) {
		SpringApplication.run(ProjectTemplateEngineApplication.class, args);
	}

}
