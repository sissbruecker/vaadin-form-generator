package de.sissbruecker.formgenerator.model;

import java.util.ArrayList;
import java.util.List;

public class BeanModel {
    private String className;

    private List<BeanProperty> properties = new ArrayList<>();

    public BeanModel() {
    }

    public BeanModel(String className, List<BeanProperty> properties) {
        this.className = className;
        this.properties = properties;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public List<BeanProperty> getProperties() {
        return properties;
    }

    public void setProperties(List<BeanProperty> properties) {
        this.properties = properties;
    }
}
