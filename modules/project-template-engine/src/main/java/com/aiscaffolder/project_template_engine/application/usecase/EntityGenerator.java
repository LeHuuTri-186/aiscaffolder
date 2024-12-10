package com.aiscaffolder.project_template_engine.application.usecase;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class EntityGenerator {

    public static class EntityData {
        public List<Entity> entities;
    }

    public static class Entity {
        public String name;
        public List<Field> fields;
    }

    /**
     * Phương thức phân tích (parse) file JSON thành đối tượng EntityData
     * @param jsonFilePath Đường dẫn đến file JSON
     * @return Đối tượng EntityData chứa danh sách các Entity
     * @throws IOException Nếu có lỗi khi đọc hoặc phân tích file JSON
     */
    public static EntityData parseJson(String jsonFilePath) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(new File(jsonFilePath), EntityData.class);
    }
}
