import { css, html, LitElement, PropertyValues } from "lit";
import { customElement, property, state } from "lit/decorators.js";
import "@vaadin/tabsheet";
import "@vaadin/form-layout";
import "@vaadin/text-field";
import "@vaadin/text-area";
import "@vaadin/email-field";
import "@vaadin/integer-field";
import "@vaadin/number-field";
import "@vaadin/password-field";
import "@vaadin/date-picker";
import "@vaadin/time-picker";
import "@vaadin/date-time-picker";
import "@vaadin/checkbox";
import "@vaadin/select";
import "@vaadin/combo-box";
import FormGeneratorConfig from "Frontend/generated/de/sissbruecker/formbuilder/model/FormGeneratorConfig";
import FormModel from "Frontend/generated/de/sissbruecker/formbuilder/model/FormModel";
import {
  FlowFormRenderer,
  HillaLitFormRenderer,
  LitTemplateFormRenderer,
} from "Frontend/form-renderers";

@customElement("form-generator-output")
export class Output extends LitElement {
  static styles = css`
    :host {
      height: 100%;
    }

    h3 {
      margin: var(--lumo-space-m) 0 0 0;
    }

    h3:first-of-type {
      margin-top: 0;
    }

    vaadin-tabsheet {
      height: 100%;
    }

    vaadin-scroller {
      width: 100%;
    }

    div[tab] {
      padding: var(--lumo-space-m);
    }
  `;

  @property()
  private formConfig: FormGeneratorConfig | null = null;
  @property()
  private formModel: FormModel | null = null;
  @state()
  private flowFormCode: string = "";
  @state()
  private hillaLitFormCode: string = "";

  protected willUpdate(changedProperties: PropertyValues) {
    if (
      changedProperties.has("formConfig") ||
      changedProperties.has("formModel")
    ) {
      if (this.formConfig && this.formModel) {
        this.flowFormCode = new FlowFormRenderer().renderForm(
          this.formModel,
          this.formConfig
        );
        this.hillaLitFormCode = new HillaLitFormRenderer().renderForm(
          this.formModel,
          this.formConfig
        );
      } else {
        this.flowFormCode = "";
      }
    }
  }

  render() {
    return html`
      <vaadin-tabsheet>
        <vaadin-tabs slot="tabs">
          <vaadin-tab id="preview-tab">Preview</vaadin-tab>
          <vaadin-tab id="flow-code-tab">Source code (Flow)</vaadin-tab>
          <vaadin-tab id="hilla-lit-code-tab"
            >Source code (Hilla / Lit)</vaadin-tab
          >
          <vaadin-tab id="bean-source-code-tab">Bean source code</vaadin-tab>
        </vaadin-tabs>

        <div tab="preview-tab">${this.renderPreview()}</div>
        <div tab="flow-code-tab">
          <form-code-view
            .code="${this.flowFormCode}"
            .language="java"
          ></form-code-view>
        </div>
        <div tab="hilla-lit-code-tab">
          <form-code-view
            .code="${this.hillaLitFormCode}"
            .language="html"
          ></form-code-view>
        </div>
        <div tab="bean-source-code-tab">
          <form-code-view
            .code="${this.formConfig?.beanSourceCode}"
            .language="java"
          ></form-code-view>
        </div>
      </vaadin-tabsheet>
    `;
  }

  private renderPreview() {
    if (!this.formConfig || !this.formModel) {
      return null;
    }

    const renderer = new LitTemplateFormRenderer();
    return renderer.renderForm(this.formModel, this.formConfig);
  }
}
