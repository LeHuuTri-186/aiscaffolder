package com.aiscaffolder.project_template_engine;

import com.aiscaffolder.application.usecase.ProjectGenerationUseCase;
import com.aiscaffolder.domain.model.ProjectDependency;
import com.aiscaffolder.domain.model.ProjectMetaData;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@SpringBootApplication(scanBasePackages = "com.aiscaffolder")
public class ProjectTemplateEngineApplication implements CommandLineRunner {

	private final ProjectGenerationUseCase projectGenerationUseCase;

    public ProjectTemplateEngineApplication(ProjectGenerationUseCase projectGenerationUseCase) {
        this.projectGenerationUseCase = projectGenerationUseCase;
    }

	@Override
	public void run(String... args) throws Exception {
		// Define project metadata
		ProjectMetaData metaData = ProjectMetaData.builder()
				.projectName("SampleProject")
				.basePackage("com.example.sample")
				.mainClassName("SampleApplication")
				.version("1.0.0")
				.groupId("com.example")
				.artifactId("sample-project")
				.springBootVersion("3.0.0")
				.javaVersion("17")
				.buildTool("Maven")
				.dependencies(List.of(
						new ProjectDependency("org.springframework.boot", "spring-boot-starter-web", "3.0.0"),
						new ProjectDependency("org.springframework.boot", "spring-boot-starter-data-jpa", "3.0.0")
				))
				.createdBy("John Doe")
				.contactEmail("john.doe@example.com")
				.organization("Example Corp")
				.createdOn(LocalDateTime.now())
				.customProperties(Map.of("database", "H2"))
				.build();

		// Output directory for generated project
		String outputDirectory = "output/sample-project";

		// Execute project generation
		projectGenerationUseCase.execute(metaData, outputDirectory);

		System.out.println("Project generated successfully at: " + outputDirectory);
	}

    public static void main(String[] args) {
		SpringApplication.run(ProjectTemplateEngineApplication.class, args);
	}

}
