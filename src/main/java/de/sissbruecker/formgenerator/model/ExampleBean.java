package de.sissbruecker.formgenerator.model;

import dev.hilla.Nonnull;

public class ExampleBean {
    @Nonnull
    private String filename;
    @Nonnull
    private String source;

    public ExampleBean(String filename, String source) {
        this.filename = filename;
        this.source = source;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}
