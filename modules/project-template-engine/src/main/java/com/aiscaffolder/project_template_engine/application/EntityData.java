package com.aiscaffolder.project_template_engine.application;

import java.util.List;

public class EntityData {
    public List<Entity> entities;

    public static class Entity {
        public String name;
        public List<Field> fields;
    }

    public static class Field {
        public String name;
        public String type;
        public Boolean required;
        public Boolean unique;
    }
}

