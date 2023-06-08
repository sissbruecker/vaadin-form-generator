package de.sissbruecker.formbuilder.services;

import com.theokanning.openai.completion.chat.ChatCompletionChoice;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatCompletionResult;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;
import de.sissbruecker.formbuilder.model.BeanModel;
import de.sissbruecker.formbuilder.model.FieldGroup;
import de.sissbruecker.formbuilder.model.FieldType;
import de.sissbruecker.formbuilder.model.FormField;
import de.sissbruecker.formbuilder.model.FormGeneratorConfig;
import de.sissbruecker.formbuilder.model.FormModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Service
public class FormGenerator {
    private static final Logger logger = LoggerFactory.getLogger(FormGenerator.class);
    private final OpenAiService openAiService;

    public FormGenerator(OpenAiService openAiService) {
        this.openAiService = openAiService;
    }

    public FormModel generateForm(BeanModel beanModel, FormGeneratorConfig config) {
        FormModel formModel = new FormModel(beanModel);

        suggestFieldNames(formModel, config);
        suggestPurpose(formModel);
        suggestFieldOrder(formModel);
        // suggestFieldLength(formModel);
        determineFieldSpans(formModel);
        suggestFieldSpans(formModel);
        determineFieldTypes(formModel);
        suggestFieldTypes(formModel);
        suggestFieldGroups(formModel, config);

        return formModel;
    }

    private void suggestFieldNames(FormModel formModel, FormGeneratorConfig config) {
        StringBuilder prompt = new StringBuilder()
                .append(String.format("I have a list of properties from a Java class named `%s`. ", formModel.getBeanModel().getClassName()))
                .append("I want to create a form for it, with one field for each property. ")
                .append("Please suggest human readable field labels for the following properties, which include the property name and its Java type:\n");
        formModel.getFields().forEach(field -> prompt.append("- ").append(field.getPropertyName()).append(" (`").append(field.getPropertyType()).append("`)\n"));
        if (config.getLanguage() != null) {
            prompt.append(String.format("The labels should be in %s. ", config.getLanguage()));
        }
        prompt
                .append("The labels should be short and descriptive. ")
                .append("Return the labels as comma-separated values, all in one line. ")
                .append("Do not include the provided property names or Java types in the output. ");
        logger.debug("suggestFieldNames prompt:\n{}", prompt);

        String reply = makeRequest(prompt.toString());
        logger.debug("suggestFieldNames reply:\n{}", reply);

        List<String> suggestedFieldNames = extractFieldList(reply, formModel.getFields().size());
        IntStream.range(0, suggestedFieldNames.size()).forEach(i -> formModel.getFields().get(i).setDisplayName(suggestedFieldNames.get(i)));
    }

    private void suggestPurpose(FormModel formModel) {
        StringBuilder prompt = new StringBuilder()
                .append(explainFormFields(formModel))
                .append(String.format("We also know that the form has been created from a Java class named `%s`. ", formModel.getBeanModel().getClassName()))
                .append("What would you say is the purpose of this form? Provide a short description, do not mention the individual fields.");
        logger.debug("suggestPurpose prompt:\n{}", prompt);

        String reply = makeRequest(prompt.toString());
        logger.debug("suggestPurpose reply:\n{}", reply);

        formModel.setPurpose(reply);
    }

    private void suggestFieldOrder(FormModel formModel) {
        StringBuilder prompt = new StringBuilder()
                .append(explainFormFields(formModel))
                .append(explainPurpose(formModel))
                .append("Please suggest a good order for the fields. ")
                .append("Only return the fields in their new order, as comma-separated values, all in one line. ");
        logger.debug("suggestFieldOrder prompt:\n{}", prompt);

        String reply = makeRequest(prompt.toString());
        logger.debug("suggestFieldOrder reply:\n{}", reply);

        List<String> fieldList = extractFieldList(reply, formModel.getFields().size());
        formModel.getFields().forEach(field -> field.setOrder(fieldList.indexOf(field.getDisplayName())));
    }

    private void suggestFieldLength(FormModel formModel) {
        StringBuilder prompt = new StringBuilder()
                .append(explainFormFields(formModel))
                .append(explainPurpose(formModel))
                .append("Please suggest how many characters a user would typically need to enter in each of the fields. ")
                .append("Return the result as CSV, with the field name in the first column, and the maximum number of characters in the second column.");
        logger.debug("suggestedFieldLengths prompt:\n{}", prompt);

        String reply = makeRequest(prompt.toString());
        logger.debug("suggestedFieldLengths reply:\n{}", reply);
    }

    private void determineFieldSpans(FormModel formModel) {
        List<String> simpleTypes = List.of(
                "boolean",
                "Boolean",
                "short",
                "Short",
                "int",
                "Integer",
                "long",
                "Long",
                "float",
                "Float",
                "double",
                "Double",
                "Number",
                "Date",
                "LocalDate",
                "LocalTime",
                "LocalDateTime"
        );

        formModel.getFields().stream()
                .filter(field -> simpleTypes.contains(field.getPropertyType()))
                .forEach(field -> field.setColSpan(1));
    }

    private void suggestFieldSpans(FormModel formModel) {
        StringBuilder prompt = new StringBuilder()
                .append(explainFormFields(formModel))
                .append(explainPurpose(formModel))
                .append("The form visually consists of two columns. ")
                .append("Fields that typically require entering only a few characters should only span one column. ")
                .append("Fields that might require entering more characters should span two columns. ")
                .append("Based on the field names, please suggest how many columns each field should span. ")
                .append("Only return the column span as digits, one for each field in the same order as the provided fields, as comma-separated values, all in one line. ");
        logger.debug("suggestFieldSpans prompt:\n{}", prompt);

        String reply = makeRequest(prompt.toString());
        logger.debug("suggestFieldSpans reply:\n{}", reply);

        List<String> suggestedColSpans = extractFieldList(reply, formModel.getFields().size());
        List<FormField> orderedFields = formModel.getOrderedFields();
        IntStream.range(0, suggestedColSpans.size()).forEach(i -> {
            // Only use suggestion if field does not have deterministic span already
            FormField formField = orderedFields.get(i);
            if (formField.getColSpan() == 0) {
                formField.setColSpan(Integer.parseInt(suggestedColSpans.get(i)));
            }
        });
    }

    private void determineFieldTypes(FormModel formModel) {
        Map<String, FieldType> fieldTypeMap = new HashMap<>();

        fieldTypeMap.put("boolean", FieldType.Checkbox);
        fieldTypeMap.put("Boolean", FieldType.Checkbox);
        fieldTypeMap.put("short", FieldType.IntegerField);
        fieldTypeMap.put("Short", FieldType.IntegerField);
        fieldTypeMap.put("int", FieldType.IntegerField);
        fieldTypeMap.put("Integer", FieldType.IntegerField);
        fieldTypeMap.put("long", FieldType.IntegerField);
        fieldTypeMap.put("Long", FieldType.IntegerField);
        fieldTypeMap.put("float", FieldType.NumberField);
        fieldTypeMap.put("Float", FieldType.NumberField);
        fieldTypeMap.put("double", FieldType.NumberField);
        fieldTypeMap.put("Double", FieldType.NumberField);
        fieldTypeMap.put("Number", FieldType.NumberField);
        fieldTypeMap.put("Date", FieldType.DatePicker);
        fieldTypeMap.put("LocalDate", FieldType.DatePicker);
        fieldTypeMap.put("LocalTime", FieldType.TimePicker);
        fieldTypeMap.put("LocalDateTime", FieldType.DateTimePicker);

        formModel.getFields().forEach(field -> {
            if (fieldTypeMap.containsKey(field.getPropertyType())) {
                field.setFieldType(fieldTypeMap.get(field.getPropertyType()));
            }
        });
    }

    private void suggestFieldTypes(FormModel formModel) {
        StringBuilder prompt = new StringBuilder()
                .append(explainFormFields(formModel, true))
                .append(explainPurpose(formModel))
                .append("Please suggest a good UI control for each field. ")
                .append("The following types of UI controls are available:\n")
                .append("- TextField (for short text)\n")
                .append("- TextArea (for longer text)\n")
                .append("- EmailField (for email addresses)\n")
                .append("- IntegerField (for integers)\n")
                .append("- NumberField (for decimal numbers)\n")
                .append("- PasswordField (for passwords)\n")
                .append("- DatePicker (for dates)\n")
                .append("- TimePicker (for times)\n")
                .append("- DateTimePicker (for date and time)\n")
                .append("- Checkbox (for booleans)\n")
                .append("- Select (for selecting from a list of values)\n")
                .append("- ComboBox (for selecting from a list of values and entering custom values)\n")
                .append("\n")
                .append("Only return the exact name of the UI control, one for each field, in the same order as the provided fields, as comma-separated values, all in one line. ");
        logger.debug("suggestFieldTypes prompt:\n{}", prompt);

        String reply = makeRequest(prompt.toString());
        logger.debug("suggestFieldTypes reply:\n{}", reply);

        List<String> fieldList = extractFieldList(reply, formModel.getFields().size());
        List<FormField> orderedFields = formModel.getOrderedFields();
        IntStream.range(0, orderedFields.size()).forEach(i -> {
            // Only use suggestion if field does not have deterministic type already
            FormField formField = orderedFields.get(i);
            if (formField.getFieldType() != null) {
                return;
            }

            String fieldTypeName = fieldList.get(i);
            FieldType fieldType;
            try {
                fieldType = FieldType.valueOf(fieldTypeName);
            } catch (IllegalArgumentException e) {
                fieldType = FieldType.TextField;
            }
            formField.setSuggestedFieldType(fieldType);
        });
    }

    private void suggestFieldGroups(FormModel formModel, FormGeneratorConfig config) {
        StringBuilder prompt = new StringBuilder()
                .append(explainFormFields(formModel))
                .append(explainPurpose(formModel))
                .append("Please suggest which fields can be logically grouped together, and also suggest a name for each group. ")
                .append(String.format("The group name should be in %s. ", config.getLanguage()))
                .append("Try to keep the number of groups low. ")
                .append("Return one group per line. ")
                .append("Each line should contain the group name, followed by the fields in that group, all separated by comma. For example:\n")
                .append("First group name, Some field, Another field\n")
                .append("Second group name, Some field, Another field\n")
                .append("...\n");
        logger.debug("suggestFieldGroups prompt:\n{}", prompt);

        String reply = makeRequest(prompt.toString());
        logger.debug("suggestFieldGroups reply:\n{}", reply);

        List<String> groupList = reply.lines().toList();
        List<FieldGroup> groups = groupList.stream().map(group -> {
            String[] groupParts = group.split(",");
            String groupName = groupParts[0].trim();
            List<String> fieldNames = Arrays.stream(groupParts).skip(1).map(String::trim).toList();
            FieldGroup fieldGroup = new FieldGroup();
            fieldGroup.setName(groupName);
            fieldGroup.setProperties(
                    formModel.getOrderedFields().stream()
                            .filter(field -> fieldNames.contains(field.getDisplayName()))
                            .map(FormField::getPropertyName)
                            .collect(Collectors.toList())
            );
            return fieldGroup;
        }).toList();

        formModel.setGroups(groups);
    }

    private String makeRequest(String prompt) {
        ChatCompletionRequest request = new ChatCompletionRequest();
        request.setModel("gpt-3.5-turbo");
        request.setTemperature(0.0);
        request.setMessages(List.of(createMessage("user", prompt)));

        ChatCompletionResult result = openAiService.createChatCompletion(request);
        ChatCompletionChoice chatCompletionChoice = result.getChoices().get(0);
        return chatCompletionChoice.getMessage().getContent();
    }

    private ChatMessage createMessage(String role, String content) {
        ChatMessage message = new ChatMessage();
        message.setRole(role);
        message.setContent(content);

        return message;
    }

    private String explainFormFields(FormModel formModel) {
        return explainFormFields(formModel, false);
    }

    private String explainFormFields(FormModel formModel, boolean explainJavaTypes) {
        StringBuilder prompt = new StringBuilder()
                .append(String.format("A `%s` form has the following fields:\n", formModel.getBeanModel().getClassName()));
        formModel.getOrderedFields().forEach(field -> {
            prompt.append("- ").append(field.getDisplayName());
            if (explainJavaTypes) {
                prompt.append(" (`").append(field.getPropertyType()).append("`)");
            }
            prompt.append("\n");
        });

        return prompt.toString();
    }

    private String explainPurpose(FormModel formModel) {
        if (formModel.getPurpose() == null || formModel.getPurpose().isBlank()) {
            return "";
        }

        return formModel.getPurpose() + "\n";
    }

    private String stripNonAlphaNumeric(String input) {
        return input.replaceAll("[^a-zA-Z0-9\\s]", "");
    }

    private List<String> extractFieldList(String reply, int expectedSize) {
        String[] fieldList = reply.split(",");
        if (expectedSize > 0 && fieldList.length != expectedSize) {
            throw new IllegalStateException("Number of returned items does not match expected list size");
        }
        return Stream.of(fieldList).map(String::trim).toList();
    }
}
