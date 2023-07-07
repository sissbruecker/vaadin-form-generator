package de.sissbruecker.formgenerator.model;

import dev.hilla.Nonnull;

public class BeanProperty {
    @Nonnull
    private String name;
    @Nonnull
    private String type;

    public BeanProperty() {
    }

    public BeanProperty(String name, String type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
