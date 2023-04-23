package de.sissbruecker.formbuilder.endpoints;

import com.vaadin.flow.server.auth.AnonymousAllowed;
import de.sissbruecker.formbuilder.model.ExampleBean;
import dev.hilla.Endpoint;
import dev.hilla.Nonnull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

@Endpoint
@AnonymousAllowed
public class ExampleBeanEndpoint {

    @Nonnull
    public List<@Nonnull ExampleBean> loadExampleBeans() throws IOException {
        return Files.list(Path.of("./examples")).map(beanFile -> {
            try {
                return new ExampleBean(beanFile.getFileName().toString(), Files.readString(beanFile));
            } catch (IOException e) {
                return null;
            }
        }).filter(Objects::nonNull).toList();
    }
}