package com.aiscaffolder.projecttemplateengine;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.aiscaffolder")
public class ProjectTemplateEngineApplication implements CommandLineRunner {

	@Override
	public void run(String... args) throws Exception {
	}

    public static void main(String[] args) {
		SpringApplication.run(ProjectTemplateEngineApplication.class, args);
	}
}
