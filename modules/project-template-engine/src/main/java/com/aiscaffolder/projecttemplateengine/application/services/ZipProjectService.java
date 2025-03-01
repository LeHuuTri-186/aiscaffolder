package com.aiscaffolder.projecttemplateengine.application.services;

import java.io.*;
import java.nio.file.*;
import java.util.zip.*;

import org.springframework.stereotype.Service;

import static java.nio.file.Files.walk;

@Service
public class ZipProjectService {

    public void zipProject(String sourceDirPath, String zipFilePath) throws IOException {
        Path zipPath = Paths.get(zipFilePath);
        try (ZipOutputStream zipOutputStream = new ZipOutputStream(Files.newOutputStream(zipPath))) {
            Path sourcePath = Paths.get(sourceDirPath);
            walk(sourcePath)
                    .filter(path -> !Files.isDirectory(path))
                    .forEach(path -> {
                        ZipEntry zipEntry = new ZipEntry(sourcePath.relativize(path).toString());
                        try {
                            zipOutputStream.putNextEntry(zipEntry);
                            Files.copy(path, zipOutputStream);
                            zipOutputStream.closeEntry();
                        } catch (IOException e) {
                            throw new UncheckedIOException(e);
                        }
                    });
        }
    }
}
