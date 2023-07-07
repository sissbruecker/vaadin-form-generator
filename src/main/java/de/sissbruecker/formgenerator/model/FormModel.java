package de.sissbruecker.formgenerator.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import dev.hilla.Nonnull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class FormModel {
    @Nonnull
    private BeanModel beanModel;
    @Nonnull
    private String purpose;

    @Nonnull
    private List<@Nonnull FormField> fields = new ArrayList<>();

    @Nonnull
    private List<@Nonnull FieldGroup> groups = new ArrayList<>();

    public FormModel() {
    }

    public FormModel(BeanModel beanModel) {
        this.beanModel = beanModel;
        this.fields = beanModel.getProperties().stream().map(FormField::new).toList();
    }

    public BeanModel getBeanModel() {
        return beanModel;
    }

    public void setBeanModel(BeanModel beanModel) {
        this.beanModel = beanModel;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public List<FormField> getFields() {
        return fields;
    }

    @JsonIgnore
    public List<FormField> getOrderedFields() {
        return fields.stream().sorted(Comparator.comparing(FormField::getOrder)).toList();
    }

    public void setFields(List<FormField> fields) {
        this.fields = fields;
    }

    public List<FieldGroup> getGroups() {
        return groups;
    }

    public void setGroups(List<FieldGroup> groups) {
        this.groups = groups;
    }

    public FormField findFieldByProperty(String propertyName) {
        return fields.stream().filter(f -> f.getBeanProperty().getName().equals(propertyName)).findFirst().orElse(null);
    }
}
