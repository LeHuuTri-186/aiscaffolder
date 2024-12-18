package com.aiscaffolder.project_template_engine;

import com.aiscaffolder.project_template_engine.application.usecase.ProjectGenerationUseCase;
import com.aiscaffolder.project_template_engine.domain.model.ProjectDependency;
import com.aiscaffolder.project_template_engine.domain.model.ProjectMetaData;
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
		// Define project metadata
		List<ProjectDependency> dependencies = List.of(
				ProjectDependency.builder()
						.groupId("org.springframework.boot")
						.artifactId("spring-boot-starter-web")
						.version("3.4.0")
						.build()
		);

		ProjectMetaData metaData = ProjectMetaData.builder()
				.projectName("SampleProject")
				.basePackage("com.example.sample")
				.mainClassName("SampleApplication")
				.version("1.0.0")
				.groupId("com.example")
				.artifactId("sample-project")
				.springBootVersion("3.4.0")
				.javaVersion("17")
				.dependencies(dependencies)
				.createdOn(LocalDateTime.now())
				.build();

		// Output directory for generated project
		String outputDirectory = "output/sample-project";

		// Path to the JSON file containing entity data
		String jsonFilePath = "entities.json";

		// Execute project generation
		projectGenerationUseCase.execute(metaData, outputDirectory, jsonFilePath);

		System.out.println("Project and entities generated successfully at: " + outputDirectory);

	}

    public static void main(String[] args) {
		SpringApplication.run(ProjectTemplateEngineApplication.class, args);
	}

}
