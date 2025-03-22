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
        if (!sourceDir.exists()) {
            log.error("[Error][zipService]: The specified source directory does not exist: {}", sourceDirPath);
            return;
        }

        String zipCommand;
        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            // Windows (PowerShell)
            zipCommand = String.format("powershell Compress-Archive -Path \"%s\" -DestinationPath \"%s\"", sourceDirPath, zipFilePath);
        } else {
            // macOS/Linux
            zipCommand = String.format("zip -r \"%s\" \"%s\"", zipFilePath, sourceDirPath);
        }

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
                log.debug("ZIP created successfully: {}", zipFilePath);
            } else {
                log.error("Failed to create ZIP. Exit code: {}", exitCode);
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
