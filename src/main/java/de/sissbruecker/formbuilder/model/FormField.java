package de.sissbruecker.formbuilder.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import dev.hilla.Nonnull;

public class FormField {
    @Nonnull
    private BeanProperty beanProperty;

    @Nonnull
    private String displayName;
    @Nonnull
    private int order;
    @Nonnull
    private int colSpan;
    @Nonnull
    private FieldType fieldType;
    @Nonnull
    private FieldType suggestedFieldType;
    @Nonnull
    private int suggestedCharacters;

    public FormField() {
    }

    public FormField(BeanProperty beanProperty) {
        this.beanProperty = beanProperty;
        this.displayName = beanProperty.getName();
    }

    public BeanProperty getBeanProperty() {
        return beanProperty;
    }

    @JsonIgnore
    public String getPropertyName() {
        return beanProperty.getName();
    }

    @JsonIgnore
    public String getPropertyType() {
        return beanProperty.getType();
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public int getOrder() {
        return order;
    }

    public int getColSpan() {
        return colSpan;
    }

    public void setColSpan(int colSpan) {
        this.colSpan = colSpan;
    }

    public FieldType getFieldType() {
        return fieldType;
    }

    public void setFieldType(FieldType fieldType) {
        this.fieldType = fieldType;
    }

    public FieldType getSuggestedFieldType() {
        return suggestedFieldType;
    }

    public void setSuggestedFieldType(FieldType suggestedFieldType) {
        this.suggestedFieldType = suggestedFieldType;
    }

    @JsonIgnore
    public FieldType getEffectiveFieldType() {
        return fieldType != null ? fieldType : suggestedFieldType;
    }

    public int getSuggestedCharacters() {
        return suggestedCharacters;
    }

    public void setSuggestedCharacters(int suggestedCharacters) {
        this.suggestedCharacters = suggestedCharacters;
    }

    @Override
    public String toString() {
        return "FormField{" +
               "displayName='" + displayName + '\'' +
               ", fieldType=" + getEffectiveFieldType() +
               ", order=" + order +
               ", colSpan=" + colSpan +
               '}';
    }
}
