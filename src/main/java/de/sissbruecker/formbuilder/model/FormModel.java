package de.sissbruecker.formbuilder.model;

import java.util.List;
import java.util.Optional;

public class FormModel {
    private final BeanModel beanModel;

    private final List<FormField> fields;

    public FormModel(BeanModel beanModel) {
        this.beanModel = beanModel;
        this.fields = beanModel.getProperties().stream().map(FormField::new).toList();
    }

    public BeanModel getBeanModel() {
        return beanModel;
    }

    public List<FormField> getFields() {
        return fields;
    }

    public Optional<FormField> getFieldByPropertyName(String propertyName) {
        return fields.stream().filter(field -> field.getPropertyName().equals(propertyName)).findFirst();
    }
}
