package de.sissbruecker.formbuilder.model;

public class FormField {
    private final BeanProperty beanProperty;

    private String displayName;

    public FormField(BeanProperty beanProperty) {
        this.beanProperty = beanProperty;
        this.displayName = beanProperty.getName();
    }

    public BeanProperty getBeanProperty() {
        return beanProperty;
    }

    public String getPropertyName() {
        return beanProperty.getName();
    }

    public String getPropertyType() {
        return beanProperty.getType();
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}
