package com.netex.apps.meta;

import javafx.util.StringConverter;

public class FileExtensions {
    private String description;
    private String extension;

    public FileExtensions(String description, String extension) {
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
        return String.format("[%s : %s]", description, extension);
    }

    public static class FileExtensionConvert extends StringConverter<FileExtensions> {

        @Override
        public String toString(FileExtensions object) {
            return object == null ? "[None]" : String.format("%s: %s", object.getDescription(), object.getExtension());
        }

        @Override
        public FileExtensions fromString(String raw) {
            if (raw == null) return null;

            FileExtensions extension;
            int pos = raw.indexOf(":");
            if (pos == -1) {
                //Treat the raw as file extension not description
                extension = new FileExtensions(null, raw);
            } else {
                //Ignoring raw bounds check for brevity
                extension = new FileExtensions(raw.substring(0, pos), raw.substring(pos + 2));
            }
            return extension;
        }
    }

}
