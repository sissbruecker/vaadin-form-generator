package de.sissbruecker.formbuilder.config;

import com.theokanning.openai.service.OpenAiService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class AppConfig {
    @Value("${openai.token}")
    public String openaiToken;

    @Bean
    public OpenAiService openAiService() {
        return new OpenAiService(openaiToken, Duration.ofSeconds(60));
    }
}
