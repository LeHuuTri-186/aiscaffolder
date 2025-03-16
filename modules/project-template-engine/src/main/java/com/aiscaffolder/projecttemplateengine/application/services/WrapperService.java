package com.aiscaffolder.projecttemplateengine.application.services;

import java.nio.file.Path;

public interface WrapperService {
    void generateWrapper(String outputDir) throws Exception;
    void setExecutable(Path executable);
}
