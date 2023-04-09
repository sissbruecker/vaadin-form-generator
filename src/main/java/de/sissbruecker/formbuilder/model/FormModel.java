package de.sissbruecker.formbuilder.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class FormModel {
    private BeanModel beanModel;
    private String purpose;

    private List<FormField> fields = new ArrayList<>();

    private List<FieldGroup> groups = new ArrayList<>();

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
}
