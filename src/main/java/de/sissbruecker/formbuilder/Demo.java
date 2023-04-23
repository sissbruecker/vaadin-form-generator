package de.sissbruecker.formbuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.theokanning.openai.service.OpenAiService;
import de.sissbruecker.formbuilder.model.BeanModel;
import de.sissbruecker.formbuilder.model.BeanProperty;
import de.sissbruecker.formbuilder.model.FormGeneratorConfig;
import de.sissbruecker.formbuilder.model.FormModel;
import de.sissbruecker.formbuilder.services.CodeGenerator;
import de.sissbruecker.formbuilder.services.FormGenerator;
import de.sissbruecker.formbuilder.services.BeanParser;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Optional;

public class Demo {
    public static void main(String[] args) throws IOException {
        final String sourceFile = "./examples/InsuranceReport.java";
        final String language = "English";
        final boolean useCache = false;
        final String cacheFile = "./tmp/form.json";
        FormModel formModel;

        if (useCache && Files.exists(Path.of(cacheFile))) {
            ObjectMapper objectMapper = new ObjectMapper();
            String json = Files.readString(Path.of(cacheFile), Charset.defaultCharset());
            formModel = objectMapper.readValue(json, FormModel.class);
        } else {
            formModel = generateForm(sourceFile, language);
            if (useCache) {
                ObjectMapper objectMapper = new ObjectMapper();
                String json = objectMapper.writeValueAsString(formModel);
                Files.writeString(Path.of(cacheFile), json, Charset.defaultCharset());
            }
        }

        CodeGenerator codeGenerator = new CodeGenerator();
        String generatedCode = codeGenerator.generateCode(formModel);

        // Print generated code
        // System.out.println("Generated code:");
        // System.out.println(generatedCode);
    }

    private static FormModel generateForm(String sourceFile, String language) throws IOException {
        String source = Files.readString(Path.of(sourceFile), Charset.defaultCharset());
        BeanModel beanModel = new BeanParser().parse(source);
        printBean(beanModel);

        String openaiToken = System.getProperty("openai.token");
        OpenAiService openAiService = new OpenAiService(openaiToken, Duration.ofSeconds(60));
        FormGenerator formGenerator = new FormGenerator(openAiService);
        FormGeneratorConfig config = new FormGeneratorConfig();
        config.setLanguage(language);
        FormModel formModel = formGenerator.generateForm(beanModel, config);
        printForm(formModel);

        return formModel;
    }

    private static void printBean(BeanModel model) {
        System.out.println("Found bean: " + model.getClassName());
        System.out.println("  Properties:");
        for (BeanProperty property : model.getProperties()) {
            System.out.println("    " + property.getName() + " (" + property.getType() + ")");
        }
    }

    private static void printForm(FormModel formModel) {
        System.out.println("Generated form:");
        System.out.println("  Fields:");
        formModel.getOrderedFields().forEach(field -> {
            System.out.println("    " + field.toString());
        });
        System.out.println("  Layout:");
        formModel.getGroups().forEach(group -> {
            System.out.println("    " + group.toString());
        });
    }
}
