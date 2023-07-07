package de.sissbruecker.formgenerator.endpoints;

import com.vaadin.flow.server.auth.AnonymousAllowed;
import de.sissbruecker.formgenerator.model.BeanModel;
import de.sissbruecker.formgenerator.model.FormGeneratorConfig;
import de.sissbruecker.formgenerator.model.FormGeneratorSession;
import de.sissbruecker.formgenerator.model.FormModel;
import de.sissbruecker.formgenerator.services.BeanParser;
import de.sissbruecker.formgenerator.services.FormGenerator;
import dev.hilla.Endpoint;
import dev.hilla.Nonnull;

@Endpoint
@AnonymousAllowed
public class FormGeneratorEndpoint {

    private final BeanParser beanParser;
    private final FormGenerator formGenerator;
    private final FormGeneratorSession session;

    public FormGeneratorEndpoint(BeanParser beanParser, FormGenerator formGenerator, FormGeneratorSession session) {
        this.beanParser = beanParser;
        this.formGenerator = formGenerator;
        this.session = session;
    }

    @Nonnull
    public FormGeneratorSession getSession() {
        // Create copy, because Jackson can not serialize Spring bean proxies
        return new FormGeneratorSession(session.getConfig(), session.getModel());
    }

    @Nonnull
    public FormModel generateForm(FormGeneratorConfig config) {
        BeanModel beanModel = beanParser.parse(config.getBeanSourceCode());
        FormModel formModel = formGenerator.generateForm(beanModel, config);

        session.setConfig(config);
        session.setModel(formModel);

        return formModel;
    }
}