package de.sissbruecker.formbuilder.endpoints;

import com.vaadin.flow.server.auth.AnonymousAllowed;
import de.sissbruecker.formbuilder.model.BeanModel;
import de.sissbruecker.formbuilder.model.FormGeneratorConfig;
import de.sissbruecker.formbuilder.model.FormModel;
import de.sissbruecker.formbuilder.services.BeanParser;
import de.sissbruecker.formbuilder.services.FormGenerator;
import dev.hilla.Endpoint;
import dev.hilla.Nonnull;

@Endpoint
@AnonymousAllowed
public class FormGeneratorEndpoint {

    private final BeanParser beanParser;
    private final FormGenerator formGenerator;

    public FormGeneratorEndpoint(BeanParser beanParser, FormGenerator formGenerator) {
        this.beanParser = beanParser;
        this.formGenerator = formGenerator;
    }

    @Nonnull
    public FormModel generateForm(String sourceCode, FormGeneratorConfig config) {
        BeanModel beanModel = beanParser.parse(sourceCode);

        FormModel formModel = formGenerator.generateForm(beanModel, config);

        return formModel;
    }
}