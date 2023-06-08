import { css, html, LitElement, PropertyValues } from "lit";
import { customElement, state } from "lit/decorators.js";
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
import "@vaadin/form-layout";
import "@vaadin/tabsheet";
import "@vaadin/split-layout";
import { SelectChangeEvent } from "@vaadin/select";
import ExampleBean from "Frontend/generated/de/sissbruecker/formbuilder/model/ExampleBean";
import {
  ExampleBeanEndpoint,
  FormGeneratorEndpoint,
} from "Frontend/generated/endpoints";
import FormModel from "Frontend/generated/de/sissbruecker/formbuilder/model/FormModel";
import { TextAreaChangeEvent } from "@vaadin/text-area";
import { TextFieldChangeEvent } from "@vaadin/text-field";
import FormGeneratorConfig from "Frontend/generated/de/sissbruecker/formbuilder/model/FormGeneratorConfig";
import { CheckboxCheckedChangedEvent } from "@vaadin/checkbox";
import {
  FlowFormRenderer,
  LitTemplateFormRenderer,
} from "Frontend/form-renderers";
import "./code-view";
import { Notification } from "@vaadin/notification";

interface ExampleBeanOption {
  label: string;
  value: string;
}

@customElement("form-generator")
export class GeneratorView extends LitElement {
  static get styles() {
    return css`
      :host {
        display: block;
        max-width: 1280px;
        height: 100%;
        margin-left: auto;
        margin-right: auto;
        background: white;
      }

      vaadin-split-layout {
        height: 100%;
      }

      .configuration {
        width: 40%;
        padding: 10px;
      }

      .configuration .bean-code {
        width: 100%;
        height: 300px;
      }

      .output {
        width: 60%;
      }

      .output,
      .output vaadin-tabsheet {
        height: 100%;
      }

      .output vaadin-scroller {
        width: 100%;
      }
    `;
  }

  @state()
  private exampleBeans: ExampleBean[] = [];
  @state()
  private formConfig: FormGeneratorConfig | null = null;
  @state()
  private formModel: FormModel | null = null;
  private flowFormCode: string = "";

  get exampleBeanOptions(): ExampleBeanOption[] {
    return this.exampleBeans.map((bean) => ({
      label: bean.filename,
      value: bean.filename,
    }));
  }

  protected async firstUpdated() {
    this.exampleBeans = await ExampleBeanEndpoint.loadExampleBeans();
    const session = await FormGeneratorEndpoint.getSession();

    this.formConfig = session.config || null;
    this.formModel = session.model || null;
  }

  protected update(changedProperties: PropertyValues) {
    super.update(changedProperties);

    if (
      changedProperties.has("formConfig") ||
      changedProperties.has("formModel")
    ) {
      if (this.formConfig && this.formModel) {
        const renderer = new FlowFormRenderer();
        this.flowFormCode = renderer.renderForm(
          this.formModel,
          this.formConfig
        );
      } else {
        this.flowFormCode = "";
      }
    }
  }

  render() {
    if (!this.formConfig) {
      return null;
    }

    return html`
      <vaadin-split-layout>
        <div class="configuration">
          <h2>Configuration</h2>
          <vaadin-select
            label="Load demo bean"
            theme="small"
            .items="${this.exampleBeanOptions}"
            @change="${this.handleSelectExampleBean}"
          ></vaadin-select>
          <br />
          <vaadin-text-area
            label="Bean source code"
            class="bean-code"
            placeholder="Paste source code here or select a demo bean"
            .value="${this.formConfig.beanSourceCode}"
            @change="${(event: TextAreaChangeEvent) =>
              (this.formConfig = {
                ...this.formConfig!,
                beanSourceCode: event.target.value,
              })}"
          ></vaadin-text-area>
          <vaadin-text-field
            label="Form language"
            .value="${this.formConfig.language}"
            @change="${(event: TextFieldChangeEvent) =>
              (this.formConfig = {
                ...this.formConfig!,
                language: event.target.value,
              })}"
          ></vaadin-text-field>
          <br />
          <vaadin-checkbox
            label="Add group header"
            ?checked="${this.formConfig.addGroupHeader}"
            @checked-changed="${(e: CheckboxCheckedChangedEvent) =>
              (this.formConfig = {
                ...this.formConfig!,
                addGroupHeader: e.detail.value,
              })}"
          >
          </vaadin-checkbox>
          <br />
          <br />
          <vaadin-button
            theme="primary"
            ?disabled="${!this.formConfig.beanSourceCode}"
            @click="${this.generateForm}"
            >Generate form
          </vaadin-button>
        </div>
        <div class="output">
          <vaadin-tabsheet>
            <vaadin-tabs slot="tabs">
              <vaadin-tab id="preview-tab">Preview</vaadin-tab>
              <vaadin-tab id="flow-code-tab">Source code (Flow)</vaadin-tab>
            </vaadin-tabs>

            <div tab="preview-tab">${this.renderPreview()}</div>
            <div tab="flow-code-tab">
              <form-code-view .code="${this.flowFormCode}"></form-code-view>
            </div>
          </vaadin-tabsheet>
        </div>
      </vaadin-split-layout>
    `;
  }

  private renderPreview() {
    if (!this.formConfig || !this.formModel) {
      return null;
    }

    const renderer = new LitTemplateFormRenderer();
    return renderer.renderForm(this.formModel, this.formConfig);
  }

  async handleSelectExampleBean(event: SelectChangeEvent) {
    const selectedBean = this.exampleBeans.find(
      (bean) => bean.filename === event.target.value
    );

    if (!selectedBean) {
      return;
    }

    this.formConfig = {
      ...this.formConfig!,
      beanSourceCode: selectedBean.source,
    };
  }

  async generateForm() {
    try {
      this.formModel = await FormGeneratorEndpoint.generateForm(
        this.formConfig!
      );
    } catch (error) {
      Notification.show("Error generating form. Please try again.", {
        theme: "error",
      });
    }
  }
}
