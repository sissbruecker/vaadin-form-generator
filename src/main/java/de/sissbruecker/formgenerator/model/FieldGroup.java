package de.sissbruecker.formgenerator.model;

import dev.hilla.Nonnull;

import java.util.ArrayList;
import java.util.List;

public class FieldGroup {
    @Nonnull
    private String name;
    @Nonnull
    private List<@Nonnull String> properties = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getProperties() {
        return properties;
    }

    public void setProperties(List<String> properties) {
        this.properties = properties;
    }

    @Override
    public String toString() {
        return "FieldGroup{" +
               "name='" + name + '\'' +
               ", properties=" + properties +
               '}';
    }
}
