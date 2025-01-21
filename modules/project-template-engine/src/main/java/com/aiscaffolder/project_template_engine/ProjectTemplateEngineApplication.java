package com.aiscaffolder.project_template_engine;

import com.aiscaffolder.project_template_engine.application.usecase.ProjectGenerationUseCase;
import com.aiscaffolder.project_template_engine.domain.model.DataSource;
import com.aiscaffolder.project_template_engine.domain.model.ProjectDependency;
import com.aiscaffolder.project_template_engine.domain.model.ProjectMetaData;
import com.aiscaffolder.project_template_engine.domain.model.ProjectTemplate;
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
						.groupId("mysql")
						.artifactId("mysql-connector-java")
						.scope("runtime")
						.version("8.0.33")
						.build(),
				ProjectDependency.builder()
						.groupId("org.springframework.boot")
						.artifactId("spring-boot-starter")
						.build()
		);

		ProjectMetaData metaData = ProjectMetaData.builder()
				.projectName("SampleProject")
				.basePackage("com.example.sample")
				.groupId("com.example")
				.artifactId("sample-project")
				.javaVersion("17")
				.build();


		ProjectTemplate projectTemplate = ProjectTemplate.builder()
				.springBootVersion("3.4.0").activeProfile("dev").dependencies(dependencies).dataSources(List.of(
						DataSource.builder()
								.dbName("mydb")
								.dbType("mysql")
								.host("localhost")
								.port("3306")
								.username("root")
								.password("password")
								.driver("com.mysql.cj.jdbc.Driver")
								.build()
				)).createdOn(LocalDateTime.now().toString()).projectMetaData(metaData)
				.build();


		// Output directory for generated project
		String outputDirectory = "output/sample-project";

		// Execute project generation
		projectGenerationUseCase.execute(projectTemplate, outputDirectory);

		System.out.println("Project generated successfully at: " + outputDirectory);
	}

    public static void main(String[] args) {
		SpringApplication.run(ProjectTemplateEngineApplication.class, args);
	}

}
