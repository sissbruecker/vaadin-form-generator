package de.sissbruecker.formbuilder;

import com.theokanning.openai.service.OpenAiService;
import de.sissbruecker.formbuilder.model.BeanModel;
import de.sissbruecker.formbuilder.model.BeanProperty;
import de.sissbruecker.formbuilder.model.FormGeneratorConfig;
import de.sissbruecker.formbuilder.model.FormModel;
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
        final String sourceFile = "./examples/Pet.java";
        final String language = "German";

        String source = Files.readString(Path.of(sourceFile), Charset.defaultCharset());
        Optional<BeanModel> maybeModel = new BeanParser().parse(source);

        if (maybeModel.isEmpty()) {
            System.out.println("Could not parse source code");
            return;
        }

        BeanModel beanModel = maybeModel.get();
        printBean(beanModel);

        String openaiToken = System.getProperty("openai.token");
        OpenAiService openAiService = new OpenAiService(openaiToken, Duration.ofSeconds(60));
        FormGenerator formGenerator = new FormGenerator(openAiService);
        FormGeneratorConfig config = new FormGeneratorConfig();
        config.setLanguage(language);
        FormModel formModel = formGenerator.generateForm(beanModel, config);
        printForm(formModel);
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
        formModel.getFields().forEach(field -> {
            System.out.println("  " + field.getDisplayName() + " (" + field.getPropertyName() + ", " + field.getPropertyType() + ")");
        });
    }
}
