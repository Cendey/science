package com.netex.apps.meta;

public class FileExtension {
    private String description;
    private String extension;

    public FileExtension(String description, String extension) {
        this.description = description;
        this.extension = extension;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    @Override
    public String toString() {
        return "[" + description + " : " + extension + "]";
    }
}
