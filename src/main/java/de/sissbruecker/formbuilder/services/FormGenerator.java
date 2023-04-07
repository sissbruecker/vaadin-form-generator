package de.sissbruecker.formbuilder.services;

import com.theokanning.openai.completion.chat.ChatCompletionChoice;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatCompletionResult;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;
import de.sissbruecker.formbuilder.model.BeanModel;
import de.sissbruecker.formbuilder.model.FormGeneratorConfig;
import de.sissbruecker.formbuilder.model.FormModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.IntStream;

public class FormGenerator {
    private static final Logger logger = LoggerFactory.getLogger(FormGenerator.class);
    private final OpenAiService openAiService;

    public FormGenerator(OpenAiService openAiService) {
        this.openAiService = openAiService;
    }

    public FormModel generateForm(BeanModel beanModel, FormGeneratorConfig config) {
        FormModel formModel = new FormModel(beanModel);

        suggestFieldNames(formModel, config);

        return formModel;
    }

    private void suggestFieldNames(FormModel formModel, FormGeneratorConfig config) {
        StringBuilder prompt = new StringBuilder()
                .append(String.format("I have a list of properties from a Java class named `%s`. ", formModel.getBeanModel().getClassName()))
                .append("I want to generate a form for it. ")
                .append("Please suggest human readable field names for the following properties, which include the property name and its Java type:\n");
        formModel.getFields().forEach(field -> prompt.append("- ").append(field.getPropertyName()).append(" | ").append(field.getPropertyType()).append("\n"));
        prompt.append("The field names should be short and descriptive. ");
        if (config.getLanguage() != null) {
            prompt.append(String.format("The field names should be in `%s`. ", config.getLanguage()));
        }
        prompt.append("Just return the suggested field names, one per provided property, one per line. ");
        prompt.append("Do not include the provided property names in the output. ");

        ChatCompletionRequest request = new ChatCompletionRequest();
        request.setModel("gpt-3.5-turbo");
        request.setTemperature(0.2);
        request.setMessages(List.of(createMessage("user", prompt.toString())));
        logger.debug("suggestFieldNames prompt:\n{}", prompt);

        ChatCompletionResult result = openAiService.createChatCompletion(request);
        ChatCompletionChoice chatCompletionChoice = result.getChoices().get(0);
        String reply = chatCompletionChoice.getMessage().getContent();
        logger.debug("suggestFieldNames reply:\n{}", reply);

        String[] suggestedName = reply.split("\n");
        if (suggestedName.length != formModel.getFields().size()) {
            throw new IllegalStateException("Number of suggested names does not match number of fields");
        }

        IntStream.range(0, suggestedName.length).forEachOrdered(i -> formModel.getFields().get(i).setDisplayName(suggestedName[i]));
    }

    private ChatMessage createMessage(String role, String content) {
        ChatMessage message = new ChatMessage();
        message.setRole(role);
        message.setContent(content);

        return message;
    }
}
