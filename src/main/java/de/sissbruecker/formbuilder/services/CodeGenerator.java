package de.sissbruecker.formbuilder.services;

import de.sissbruecker.formbuilder.model.FieldType;
import de.sissbruecker.formbuilder.model.FormField;
import de.sissbruecker.formbuilder.model.FormModel;

public class CodeGenerator {
    public String generateCode(FormModel formModel) {
        StringBuilder source = new StringBuilder();
        source.append("FormLayout formLayout = new FormLayout();\n");
        source.append("formLayout.setResponsiveSteps(\n");
        source.append("        new FormLayout.ResponsiveStep(\"0\", 1),\n");
        source.append("        new FormLayout.ResponsiveStep(\"500px\", 2));\n");
        source.append("\n");

        formModel.getOrderedFields().forEach(field -> {
            source.append(renderField(field));
        });

        return source.toString();
    }

    private String renderField(FormField field) {
        StringBuilder source = new StringBuilder();
        FieldType fieldType = field.getEffectiveFieldType();

        source.append(fieldType).append(" ").append(field.getPropertyName()).append(" = new ").append(fieldType).append("(\"").append(field.getDisplayName()).append("\");\n");
        source.append("formLayout.add(").append(field.getPropertyName()).append(", ").append(field.getColSpan()).append(");\n");
        source.append("\n");

        return source.toString();
    }
}
