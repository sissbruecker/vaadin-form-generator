import { css, html, LitElement } from "lit";
import { customElement, state } from "lit/decorators.js";
import "@vaadin/text-field";
import "@vaadin/checkbox";
import "@vaadin/select";
import "@vaadin/split-layout";
import "@vaadin/upload";
import { Upload, UploadBeforeEvent, UploadI18n } from "@vaadin/upload";
import "@vaadin/custom-field";
import { SelectChangeEvent } from "@vaadin/select";
import { Notification } from "@vaadin/notification";
import { TextFieldChangeEvent } from "@vaadin/text-field";
import { CheckboxCheckedChangedEvent } from "@vaadin/checkbox";
import FormGeneratorConfig from "Frontend/generated/de/sissbruecker/formbuilder/model/FormGeneratorConfig";
import ExampleBean from "Frontend/generated/de/sissbruecker/formbuilder/model/ExampleBean";
import {
  ExampleBeanEndpoint,
  FormGeneratorEndpoint,
} from "Frontend/generated/endpoints";
import FormModel from "Frontend/generated/de/sissbruecker/formbuilder/model/FormModel";
import "./code-view";
import "./empty";
import "./output";

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
        height: 100%;
        margin-left: auto;
        margin-right: auto;
        background: white;
      }

      h1 {
        margin: 0 0 var(--lumo-space-m) 0;
        font-size: var(--lumo-font-size-xl);
      }

      h2 {
        margin: var(--lumo-space-s) 0;
        font-size: var(--lumo-font-size-m);
      }

      vaadin-upload {
        display: inline-block;
      }

      .layout {
        height: 100%;
        display: flex;
      }

      .configuration {
        flex: 0 0 auto;
        padding: var(--lumo-space-l);
        background: var(--lumo-shade-5pct);
      }

      .configuration .bean-code {
        width: 100%;
        height: 300px;
      }

      .output {
        flex: 1 1 0;
      }

      .output,
      .output vaadin-tabsheet {
        height: 100%;
      }

      .output vaadin-scroller {
        width: 100%;
      }

      /* Set the track and thumb color */

      input[type="range"] {
        -webkit-appearance: none;
        width: 100%;
        height: 8px;
        border-radius: 4px;
        outline: none;
      }

      /* Style the track */

      input[type="range"]::-webkit-slider-runnable-track {
        width: 100%;
        height: 8px;
        background-color: var(--lumo-primary-color);
        border-radius: 4px;
      }

      input[type="range"]::-moz-range-track {
        width: 100%;
        height: 8px;
        background-color: var(--lumo-primary-color);
        border-radius: 4px;
      }

      /* Style the thumb */

      input[type="range"]::-webkit-slider-thumb {
        -webkit-appearance: none;
        width: 16px;
        height: 16px;
        background-color: var(--lumo-base-color);
        border-radius: 50%;
        border: 2px solid var(--lumo-contrast);
        margin-top: -4px;
      }

      input[type="range"]::-moz-range-thumb {
        width: 16px;
        height: 16px;
        background-color: var(--lumo-base-color);
        border-radius: 50%;
        border: 2px solid var(--lumo-contrast);
        margin-top: -4px;
      }

      /* Style the thumb on hover */

      input[type="range"]::-webkit-slider-thumb:hover {
        background-color: var(--lumo-base-color);
        cursor: pointer;
      }

      input[type="range"]::-moz-range-thumb:hover {
        background-color: var(--lumo-base-color);
        cursor: pointer;
      }
    `;
  }

  @state()
  private exampleBeans: ExampleBean[] = [];
  @state()
  private formConfig: FormGeneratorConfig | null = null;
  @state()
  private formModel: FormModel | null = null;

  private uploadI18n: UploadI18n = {
    ...new Upload().i18n,
    addFiles: {
      one: "Upload .java file",
      many: "",
    },
  };

  private selectableModels = [
    {
      label: "GPT 3.5 turbo ⚡",
      value: "gpt-3.5-turbo",
    },
    {
      label: "GPT 4 🐢",
      value: "gpt-4",
    },
  ];

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

  render() {
    if (!this.formConfig) {
      return null;
    }

    return html`
      <div class="layout">
        <div class="configuration">
          <h1>Vaadin Form Generator</h1>
          <h2>Generator Options</h2>
          <vaadin-select
            label="Load demo bean"
            .items="${this.exampleBeanOptions}"
            @change="${this.handleSelectExampleBean}"
          ></vaadin-select>
          or
          <vaadin-upload
            nodrop
            max-files="1"
            accept=".java"
            .i18n="${this.uploadI18n}"
            @upload-before="${this.handleUpload}"
          ></vaadin-upload>
          <br />
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
          <vaadin-select
            label="Model"
            .items="${this.selectableModels}"
            .value="${this.formConfig.model}"
            @change="${(event: SelectChangeEvent) =>
              (this.formConfig = {
                ...this.formConfig!,
                model: event.target.value,
              })}"
          ></vaadin-select>
          <br />
          <vaadin-custom-field label="Turn up the heat (Temperature)">
            <input
              type="range"
              min="0"
              max="1"
              step="0.1"
              .value="${this.formConfig.temperature.toString()}"
              @change="${(event: InputEvent) => {
                this.formConfig = {
                  ...this.formConfig!,
                  temperature: parseFloat(
                    (event.target as HTMLInputElement).value
                  ),
                };
              }}"
            />
          </vaadin-custom-field>
          <h2>Rendering Options</h2>
          <vaadin-checkbox
            label="Show group header"
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
          ${this.formModel
            ? html` <form-generator-output
                .formConfig="${this.formConfig}"
                .formModel="${this.formModel}"
              ></form-generator-output>`
            : html` <form-generator-empty></form-generator-empty>`}
        </div>
      </div>
    `;
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

  async handleUpload(event: UploadBeforeEvent) {
    event.preventDefault();

    const file = event.detail.file as any;
    const fileReader = new FileReader();
    fileReader.addEventListener("load", () => {
      const content = fileReader.result;
      this.formConfig = {
        ...this.formConfig!,
        beanSourceCode: content as string,
      };
    });
    fileReader.readAsText(file);
    (event.target as Upload).files = [];
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
