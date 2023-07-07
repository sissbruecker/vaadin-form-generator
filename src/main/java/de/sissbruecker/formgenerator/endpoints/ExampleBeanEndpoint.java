package de.sissbruecker.formgenerator.endpoints;

import com.vaadin.flow.server.auth.AnonymousAllowed;
import de.sissbruecker.formgenerator.model.ExampleBean;
import dev.hilla.Endpoint;
import dev.hilla.Nonnull;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

@Endpoint
@AnonymousAllowed
public class ExampleBeanEndpoint {

    private static final List<String> EXAMPLE_FILES = List.of(
            "InsuranceReport.java",
            "Pet.java",
            "Patient.java",
            "Employee.java",
            "UniversityApplicant.java",
            "PresidentialApplicant.java"
    );

    @Nonnull
    public List<@Nonnull ExampleBean> loadExampleBeans() {
        return EXAMPLE_FILES.stream().map(filename -> {
            try {
                return loadExample(filename);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).filter(Objects::nonNull).toList();
    }

    private static ExampleBean loadExample(String filename) throws IOException {
        InputStream inputStream = ExampleBeanEndpoint.class.getClassLoader().getResourceAsStream("META-INF/resources/examples/" + filename);
        if (inputStream == null) {
            return null;
        }
        String sourceCode = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        return new ExampleBean(filename, sourceCode);
    }
}