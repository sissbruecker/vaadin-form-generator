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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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

        applyDeterministicFieldSpans(formModel);
        applyDeterministicFieldTypes(formModel);

        getSuggestions(formModel, config);

        return formModel;
    }

    private void getSuggestions(FormModel formModel, FormGeneratorConfig config) {
        StringBuilder prompt = new StringBuilder()
                .append(String.format("I have a list of properties from a Java class named `%s`:\n", formModel.getBeanModel().getClassName()));

        formModel.getFields().forEach(field -> prompt.append("- ").append(field.getPropertyName()).append(" (`").append(field.getPropertyType()).append("`)\n"));

        prompt.append("\n");
        prompt.append("I want to create a form for it, with one field for each property. ")
                .append("I want you to do the following:\n")
                .append("1. Describe the purpose of the form, and where it might be used, in a single paragraph\n")
                .append("2. Make suggestions for each form field:\n")
                .append(String.format("  2a. Suggest a label for the field. The labels should be in %s.\n", config.getLanguage()))
                .append("  2b. Suggest how many characters a user would typically have to enter into each field\n")
                .append("  2c. Suggest a UI control to use for each field. The available controls are `TextField` for single-line texts, `TextArea` for multi-line texts, `EmailField` for email addresses, `PasswordField` for passwords, and `Select` for selecting a value from a small number of options.\n")
                .append("3. Suggest a good order of the fields, based on how users would typically would want to fill out the form.\n")
                .append("4. Suggest which fields can be logically grouped together into form groups. Generate a label for each group, which should be in English.\n")
                .append("\n")
                .append("---")
                .append("\n")
                .append("Return your output in the following format:\n")
                .append("\n")
                .append("Purpose: <suggested purpose>\n")
                .append("\n")
                .append("Fields:\n")
                .append("- <Java property name> | <suggested label> | <suggested number of characters, digits only> | <suggested UI control>\n")
                .append("...\n")
                .append("\n")
                .append("Suggested order of fields:\n")
                .append("1. <Java property name>\n")
                .append("...\n")
                .append("\n")
                .append("Groups:\n")
                .append(String.format("- <suggested group name in %s>: <Java property name 1>, <Java property name 2>, ...\n", config.getLanguage()))
                .append("...");

        prompt.append("\n\n")
                .append("---")
                .append("\n\n");

        prompt.append("The following is an example output:")
                .append("\n\n")
                .append("Purpose: The form is used to create new user accounts on websites. It can be used on various types of websites such as social media, e-commerce, and news websites.\n")
                .append("\n")
                .append("Fields:\n")
                .append("- lastName | Last name | 20 | TextField\n")
                .append("- firstName | First name | 20 | TextField\n")
                .append("- password | Password | 20 | PasswordField\n")
                .append("- email | Email address | 50 | EmailField\n")
                .append("\n")
                .append("Suggested order of fields:\n")
                .append("1. firstName\n")
                .append("2. lastName\n")
                .append("3. email\n")
                .append("4. password\n")
                .append("\n")
                .append("Groups:\n")
                .append("- Personal information: firstName, lastName\n")
                .append("- Account information: email, password");


        logger.debug("suggestions prompt:\n{}", prompt);
        long start = System.currentTimeMillis();
        String reply = makeRequest(prompt.toString(), config);
        long end = System.currentTimeMillis();
        logger.debug("suggestions reply:\n{}", reply);
        logger.debug("suggestions request took {}s", (end - start) / 1000);
        parseReply(reply, formModel);
    }

    private void applyDeterministicFieldSpans(FormModel formModel) {
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

    private void applyDeterministicFieldTypes(FormModel formModel) {
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

    private String makeRequest(String prompt, FormGeneratorConfig config) {
        ChatCompletionRequest request = new ChatCompletionRequest();
        request.setModel(config.getModel());
        request.setTemperature(config.getTemperature());
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

    private void parseReply(String reply, FormModel formModel) {
        Map<String, String> sections = parseSections(reply);

        if (!sections.containsKey("Purpose")) {
            throw new IllegalStateException("Failed to parse purpose from reply");
        }
        if (!sections.containsKey("Fields")) {
            throw new IllegalStateException("Failed to parse fields from reply");
        }
        if (!sections.containsKey("Groups")) {
            throw new IllegalStateException("Failed to parse form groups from reply");
        }

        // Extract purpose
        formModel.setPurpose(sections.get("Purpose"));

        // Extract fields
        List<String> fields = extractListItems(sections.get("Fields"));

        IntStream.range(0, fields.size()).forEach(index -> {
            String fieldData = fields.get(index);
            String[] fieldValues = fieldData.split("\\|");
            String propertyName = fieldValues[0].trim();
            String suggestedLabel = fieldValues[1].trim();
            int suggestedCharacters = parseSuggestedFieldLength(fieldValues[2].trim());
            String suggestedFieldType = fieldValues[3].trim();

            FormField field = formModel.findFieldByProperty(propertyName);
            if (field == null) {
                throw new IllegalStateException("Could not find field with property name " + propertyName);
            }
            field.setOrder(index);
            field.setDisplayName(suggestedLabel);
            // Only use suggested colspan if we don't have a deterministic one
            if (field.getColSpan() == 0) {
                field.setColSpan(suggestedCharacters >= 50 ? 2 : 1);
            }
            // Only use suggested field type if we don't have a deterministic one
            if (field.getFieldType() == null) {
                FieldType fieldType = parseSuggestedFieldType(suggestedFieldType);
                field.setFieldType(fieldType);
            }
        });

        // Extract form groups
        List<String> formGroups = extractListItems(sections.get("Groups"));
        formGroups.forEach(groupData -> {
            String[] groupValues = groupData.split(":");
            String groupName = groupValues[0].trim();
            String[] groupProperties = groupValues[1].split(",");

            FieldGroup group = new FieldGroup();
            group.setName(groupName);
            for (String sectionField : groupProperties) {
                String propertyName = sectionField.trim();
                group.getProperties().add(propertyName);
            }

            formModel.getGroups().add(group);
        });
    }

    private Map<String, String> parseSections(String reply) {
        Map<String, String> sections = new HashMap<>();
        String[] lines = reply.split("\n");

        String currentSection = null;
        StringBuilder currentSectionLines = new StringBuilder();

        for (String line : lines) {
            // detect new section
            Pattern sectionStartPattern = Pattern.compile("^([\\w\\s]+):");
            Matcher sectionStartMatcher = sectionStartPattern.matcher(line);
            if (sectionStartMatcher.find()) {
                // close previous section
                if (currentSection != null && currentSectionLines.length() > 0) {
                    sections.put(currentSection, currentSectionLines.toString().trim());
                }
                // start new section
                currentSection = sectionStartMatcher.group(1);
                currentSectionLines = new StringBuilder();
                // remove section header from line
                line = line.replace(sectionStartMatcher.group(), "");
            }
            // add line to section
            currentSectionLines.append(line).append("\n");
        }

        // close final section
        if (currentSection != null && currentSectionLines.length() > 0) {
            sections.put(currentSection, currentSectionLines.toString().trim());
        }

        return sections;
    }

    private List<String> extractListItems(String section) {
        String[] fieldList = section.split("\n");
        return Stream.of(fieldList).map(field -> field.replaceAll("^- ", "")).toList();
    }

    private int parseSuggestedFieldLength(String suggestedFieldLength) {
        // Extract digits using regex
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(suggestedFieldLength);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group());
        } else {
            logger.warn("Failed to parse suggested field length: {}", suggestedFieldLength);
            return 0;
        }
    }

    private FieldType parseSuggestedFieldType(String suggestedFieldType) {
        try {
            // Only take first word, and ignore further suggestions for other fields or values
            suggestedFieldType = suggestedFieldType.split(" ")[0];
            return FieldType.valueOf(suggestedFieldType);
        } catch (IllegalArgumentException e) {
            logger.warn("Failed to parse suggested field type: {}", suggestedFieldType);
            return FieldType.TextField;
        }
    }
}
