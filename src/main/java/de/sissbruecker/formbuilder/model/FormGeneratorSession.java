package de.sissbruecker.formbuilder.model;

import dev.hilla.Nonnull;

public class FormGeneratorSession {
    @Nonnull
    private FormGeneratorConfig config = new FormGeneratorConfig();
    private FormModel model;

    public FormGeneratorSession() {
    }

    public FormGeneratorSession(@Nonnull FormGeneratorConfig config, FormModel model) {
        this.config = config;
        this.model = model;
    }

    public FormGeneratorConfig getConfig() {
        return config;
    }

    public void setConfig(FormGeneratorConfig config) {
        this.config = config;
    }

    public FormModel getModel() {
        return model;
    }

    public void setModel(FormModel model) {
        this.model = model;
    }
}
