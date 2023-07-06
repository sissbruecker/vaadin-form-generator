package de.sissbruecker.formbuilder.model;

import dev.hilla.Nonnull;

public class FormGeneratorConfig {
    @Nonnull
    private String beanSourceCode = "";
    @Nonnull
    private String language = "English";
    @Nonnull
    private double temperature = 0;
    @Nonnull
    private boolean addGroupHeader = true;

    public String getBeanSourceCode() {
        return beanSourceCode;
    }

    public void setBeanSourceCode(String beanSourceCode) {
        this.beanSourceCode = beanSourceCode;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public boolean isAddGroupHeader() {
        return addGroupHeader;
    }

    public void setAddGroupHeader(boolean addGroupHeader) {
        this.addGroupHeader = addGroupHeader;
    }
}
