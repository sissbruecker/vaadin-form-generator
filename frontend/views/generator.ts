import { css, html, LitElement, TemplateResult } from "lit";
import { customElement, state } from "lit/decorators.js";
import { html as staticHtml, literal, StaticValue } from "lit/static-html.js";
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
import { SelectChangeEvent } from "@vaadin/select";
import ExampleBean from "Frontend/generated/de/sissbruecker/formbuilder/model/ExampleBean";
import {
  ExampleBeanEndpoint,
  FormGeneratorEndpoint,
} from "Frontend/generated/endpoints";
import FormModel from "Frontend/generated/de/sissbruecker/formbuilder/model/FormModel";
import FieldType from "Frontend/generated/de/sissbruecker/formbuilder/model/FieldType";
import FormField from "Frontend/generated/de/sissbruecker/formbuilder/model/FormField";
import { TextAreaChangeEvent } from "@vaadin/text-area";
import { TextFieldChangeEvent } from "@vaadin/text-field";
import FormGeneratorConfig from "Frontend/generated/de/sissbruecker/formbuilder/model/FormGeneratorConfig";
import { CheckboxCheckedChangedEvent } from "@vaadin/checkbox";

interface ExampleBeanOption {
  label: string;
  value: string;
}

@customElement("form-generator")
export class GeneratorView extends LitElement {
  static get styles() {
    return css`
      :host {
        display: flex;
        max-width: 1280px;
        height: 100%;
        margin-left: auto;
        margin-right: auto;
        background: white;
      }

      h2 {
        margin: 0;
      }

      .configuration,
      .output {
        flex: 0 0 50%;
        padding: 10px;
      }

      .configuration .bean-code {
        width: 100%;
        height: 300px;
      }
    `;
  }

  @state()
  private exampleBeans: ExampleBean[] = [];
  @state()
  private beanSourceCode: string = "";
  @state()
  private addGroupHeader: boolean = true;
  @state()
  private formLanguage: string = "English";
  @state()
  private formModel: FormModel | null = null;

  get exampleBeanOptions(): ExampleBeanOption[] {
    return this.exampleBeans.map((bean) => ({
      label: bean.filename,
      value: bean.filename,
    }));
  }

  protected async firstUpdated() {
    this.exampleBeans = await ExampleBeanEndpoint.loadExampleBeans();
  }

  render() {
    return html`
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
          .value="${this.beanSourceCode}"
          @change="${(event: TextAreaChangeEvent) =>
            (this.beanSourceCode = event.target.value)}"
        ></vaadin-text-area>
        <vaadin-text-field
          label="Form language"
          .value="${this.formLanguage}"
          @change="${(event: TextFieldChangeEvent) =>
            (this.formLanguage = event.target.value)}"
        ></vaadin-text-field>
        <br />
        <vaadin-checkbox
          label="Add group header"
          ?checked="${this.addGroupHeader}"
          @checked-changed="${(e: CheckboxCheckedChangedEvent) =>
            (this.addGroupHeader = e.detail.value)}"
        >
        </vaadin-checkbox>
        <br />
        <br />
        <vaadin-button
          theme="primary"
          ?disabled="${!this.beanSourceCode}"
          @click="${this.generateForm}"
          >Generate form
        </vaadin-button>
      </div>
      <div class="panel output">
        <h2>Output</h2>
        ${this.renderFormWithGroups()}
      </div>
    `;
  }

  private renderFormWithOrderedFields() {
    if (!this.formModel) {
      return null;
    }

    const orderedFields = this.formModel.fields
      .slice()
      .sort((left, right) => left.order - right.order);
    const fieldItems: TemplateResult[] = [];
    orderedFields.forEach((field) => {
      fieldItems.push(this.renderField(field));
    });

    return html` <vaadin-form-layout>${fieldItems}</vaadin-form-layout> `;
  }

  private renderFormWithGroups() {
    if (!this.formModel) {
      return null;
    }

    const formItems: TemplateResult[] = [];

    this.formModel.groups.forEach((group) => {
      if (this.addGroupHeader) {
        formItems.push(html` <h3 colspan="2">${group.name}</h3> `);
      }

      let spanTotal = 0;
      group.properties.forEach((property) => {
        const field = this.formModel!.fields.find(
          (field) => field.beanProperty.name === property
        );
        if (!field) {
          console.warn(`Could not find field for property ${property}`);
          return;
        }
        spanTotal += field!.colSpan;
        formItems.push(this.renderField(field!));
      });

      if (spanTotal % 2 !== 0) {
        formItems.push(html` <div></div>`); // add empty item to fill row
      }
    });

    return html` <vaadin-form-layout>${formItems}</vaadin-form-layout> `;
  }

  private renderField(field: FormField) {
    const effectiveFieldType =
      field.fieldType || field.suggestedFieldType || FieldType.TextField;
    let fieldTagName: StaticValue;

    switch (effectiveFieldType) {
      case FieldType.TextArea:
        fieldTagName = literal`vaadin-text-area`;
        break;
      case FieldType.EmailField:
        fieldTagName = literal`vaadin-email-field`;
        break;
      case FieldType.IntegerField:
        fieldTagName = literal`vaadin-integer-field`;
        break;
      case FieldType.NumberField:
        fieldTagName = literal`vaadin-number-field`;
        break;
      case FieldType.PasswordField:
        fieldTagName = literal`vaadin-password-field`;
        break;
      case FieldType.DatePicker:
        fieldTagName = literal`vaadin-date-picker`;
        break;
      case FieldType.TimePicker:
        fieldTagName = literal`vaadin-time-picker`;
        break;
      case FieldType.DateTimePicker:
        fieldTagName = literal`vaadin-date-time-picker`;
        break;
      case FieldType.Checkbox:
        fieldTagName = literal`vaadin-checkbox`;
        break;
      case FieldType.Select:
        fieldTagName = literal`vaadin-select`;
        break;
      case FieldType.ComboBox:
        fieldTagName = literal`vaadin-combo-box`;
        break;

      default:
        fieldTagName = literal`vaadin-text-field`;
    }

    return staticHtml` <${fieldTagName}
        label="${field.displayName}"
        colspan="${field.colSpan}"
      ></${fieldTagName}>`;
  }

  async handleSelectExampleBean(event: SelectChangeEvent) {
    const selectedBean = this.exampleBeans.find(
      (bean) => bean.filename === event.target.value
    );

    if (!selectedBean) {
      return;
    }

    this.beanSourceCode = selectedBean.source;
  }

  async generateForm() {
    const config: FormGeneratorConfig = {
      language: this.formLanguage,
    };
    const formModel = await FormGeneratorEndpoint.generateForm(
      this.beanSourceCode,
      config
    );
    this.formModel = formModel;
  }
}
