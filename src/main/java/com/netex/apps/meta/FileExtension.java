package com.netex.apps.meta;

import javafx.util.StringConverter;

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

    public static class FileExtensionConvert extends StringConverter<FileExtension> {

        @Override
        public String toString(FileExtension object) {
            return object == null ? "[None]" : object.getDescription() + ": " + object.getExtension();
        }

        @Override
        public FileExtension fromString(String raw) {
            if (raw == null) return null;

            FileExtension extension;
            int pos = raw.indexOf(":");
            if (pos == -1) {
                //Treat the raw as file extension not description
                extension = new FileExtension(null, raw);
            } else {
                //Ignoring raw bounds check for brevity
                extension = new FileExtension(raw.substring(0, pos), raw.substring(pos + 2));
            }
            return extension;
        }
    }

}
