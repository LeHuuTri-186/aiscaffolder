package com.aiscaffolder.aiscaffolder.application.services.impl;

import com.aiscaffolder.aiscaffolder.application.services.WrapperService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Slf4j
@Service
public class MavenWrapperServiceImpl implements WrapperService {

    @Override
    public void generateWrapper(String outputDir) throws IOException {
        File projectDir = new File(outputDir);

        if (!projectDir.exists()) {
            log.warn("The specified folder does not exist: {}", outputDir);
            return;
        }

        try {
            ProcessBuilder processBuilder = new ProcessBuilder("mvn", "wrapper:wrapper");
            processBuilder.directory(projectDir);
            processBuilder.redirectErrorStream(true);

            Process process = processBuilder.start();

            int exitCode = process.waitFor();

            if (exitCode == 0) {
                log.info("Maven Wrapper generated successfully in: {}", outputDir);
            } else {
                log.info("Failed to generate Maven Wrapper. Exit code: {}", exitCode);
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}

