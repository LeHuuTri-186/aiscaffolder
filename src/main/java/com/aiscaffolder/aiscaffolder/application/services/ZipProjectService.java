package com.aiscaffolder.aiscaffolder.application.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Slf4j
@Service
public class ZipProjectService {

    public void zipProject(String sourceDirPath, String zipFilePath) throws IOException {
        File sourceDir = new File(sourceDirPath);
        if (!sourceDir.exists() || !sourceDir.isDirectory()) {
            log.error("[Error][zipService]: The specified source directory does not exist: {}", sourceDirPath);
            return;
        }

        // Extract project name from the path (e.g., output/project-/myProject → myProject)
        String[] pathParts = sourceDirPath.split("/");
        String projectName = pathParts[pathParts.length - 1];

        String zipCommand;
        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            // Windows (PowerShell) - Ensure path is correctly formatted
            zipCommand = String.format(
                    "powershell Compress-Archive -Path \"%s\\*\" -DestinationPath \"%s\" -Force",
                    sourceDirPath, zipFilePath
            );
        } else {
            // Linux/macOS - Move inside project folder before zipping
            zipCommand = String.format(
                    "(cd \"%s\" && zip -r \"../%s.zip\" .)",
                    sourceDirPath, projectName
            );
        }

        log.info("[zipService]: Executing command: {}", zipCommand);

        try {
            ProcessBuilder processBuilder;
            if (System.getProperty("os.name").toLowerCase().contains("win")) {
                processBuilder = new ProcessBuilder("cmd", "/c", zipCommand);
            } else {
                processBuilder = new ProcessBuilder("sh", "-c", zipCommand);
            }

            processBuilder.directory(new File(".")); // Set working directory
            Process process = processBuilder.start();
            int exitCode = process.waitFor();

            if (exitCode == 0) {
                log.debug("✅ ZIP created successfully: {}", zipFilePath);
            } else {
                log.error("❌ Failed to create ZIP. Exit code: {}", exitCode);
            }

        } catch (IOException | InterruptedException e) {
            log.error("Exception during zipping: ", e);
        }
    }
}
