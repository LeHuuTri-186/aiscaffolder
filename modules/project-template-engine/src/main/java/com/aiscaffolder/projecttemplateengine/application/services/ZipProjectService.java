package com.aiscaffolder.projecttemplateengine.application.services;

import java.io.*;
import java.nio.file.*;
import java.util.zip.*;

import org.springframework.stereotype.Service;

import static java.nio.file.Files.walk;

@Service
public class ZipProjectService {

    public void zipProject(String sourceDirPath, String zipFilePath) throws IOException {
        File sourceDir = new File(sourceDirPath);
        if (!sourceDir.exists()) {
            System.out.println("❌ Error: The specified source directory does not exist: " + sourceDirPath);
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
            processBuilder.inheritIO(); // Redirect output to console
            Process process = processBuilder.start();
            int exitCode = process.waitFor();

            if (exitCode == 0) {
                System.out.println("✅ ZIP created successfully: " + zipFilePath);
            } else {
                System.out.println("❌ Failed to create ZIP. Exit code: " + exitCode);
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
