package de.sissbruecker.formbuilder.model;

import dev.hilla.Nonnull;

public class FormGeneratorConfig {
    @Nonnull
    private String beanSourceCode = "";
    @Nonnull
    private String language = "English";
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

    public boolean isAddGroupHeader() {
        return addGroupHeader;
    }

    public void setAddGroupHeader(boolean addGroupHeader) {
        this.addGroupHeader = addGroupHeader;
    }
}
