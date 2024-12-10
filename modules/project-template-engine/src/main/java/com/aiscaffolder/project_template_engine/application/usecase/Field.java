package com.aiscaffolder.project_template_engine.application.usecase;

public class Field {
    private String name;
    private String type;
    private boolean required;
    private boolean unique;
    private String nameCapitalized; // New property added

    public Field() {}

    // Constructor
    public Field(String name, String type, boolean required, boolean unique) {
        this.name = name;
        this.type = type;
        this.required = required;
        this.unique = unique;
        this.nameCapitalized = capitalizeFirstLetter(name); // Automatically set the nameCapitalized
    }

    // Getters and setters for all fields
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        this.nameCapitalized = capitalizeFirstLetter(name); // Recalculate nameCapitalized
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public boolean isUnique() {
        return unique;
    }

    public void setUnique(boolean unique) {
        this.unique = unique;
    }

    public String getNameCapitalized() {
        return nameCapitalized;
    }

    public void setNameCapitalized(String nameCapitalized) {
        this.nameCapitalized = nameCapitalized;
    }

    // Utility method for capitalizing the first letter of a string
    private String capitalizeFirstLetter(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }

    @Override
    public String toString() {
        return "Field{name='" + name + "', type='" + type + "', required=" + required + ", unique=" + unique + ", nameCapitalized='" + nameCapitalized + "'}";
    }
}

