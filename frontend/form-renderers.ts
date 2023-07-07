import { html, TemplateResult } from "lit";
import { html as staticHtml, literal, StaticValue } from "lit/static-html.js";
import FieldGroup from "Frontend/generated/de/sissbruecker/formgenerator/model/FieldGroup";
import FormModel from "Frontend/generated/de/sissbruecker/formgenerator/model/FormModel";
import FormField from "Frontend/generated/de/sissbruecker/formgenerator/model/FormField";
import FieldType from "Frontend/generated/de/sissbruecker/formgenerator/model/FieldType";

export interface RenderOptions {
  addGroupHeader: boolean;
}

abstract class FormRenderer<TItemPresentation> {
  abstract renderForm(
    formModel: FormModel,
    options: RenderOptions
  ): TItemPresentation;

  public renderFormItems(
    formModel: FormModel,
    options: RenderOptions
  ): TItemPresentation[] {
    const formItems: TItemPresentation[] = [];
    let lastField: FormField | null = null;

    formModel.groups.forEach((group) => {
      if (options.addGroupHeader) {
        formItems.push(this.renderGroupHeader(group));
      }

      let spanTotal = 0;
      group.properties.forEach((property) => {
        const field = formModel!.fields.find(
          (field) => field.beanProperty.name === property
        );
        if (!field) {
          console.warn(`Could not find field for property ${property}`);
          return;
        }
        lastField = field;
        if (field.colSpan === 2) {
          spanTotal = 0;
        } else {
          spanTotal += field!.colSpan;
        }
        formItems.push(this.renderField(field!));
      });

      // add placeholder to fill row
      if (
        !options.addGroupHeader &&
        spanTotal % 2 !== 0 &&
        lastField &&
        lastField.colSpan === 1
      ) {
        formItems.push(this.renderPlaceholder());
      }
    });

    return formItems;
  }

  abstract renderGroupHeader(group: FieldGroup): TItemPresentation;

  abstract renderField(field: FormField): TItemPresentation;

  abstract renderPlaceholder(): TItemPresentation;
}

export class LitTemplateFormRenderer extends FormRenderer<TemplateResult> {
  renderForm(formModel: FormModel, options: RenderOptions): TemplateResult {
    const formItems = this.renderFormItems(formModel, options);
    return html` <vaadin-form-layout>${formItems}</vaadin-form-layout> `;
  }

  renderField(field: FormField): TemplateResult {
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
        colspan="${field.colSpan}"
        chars="50"
      >
       <label slot="label" chars="${field.suggestedCharacters}">${field.displayName}</label>
      </${fieldTagName}>`;
  }

  renderGroupHeader(group: FieldGroup): TemplateResult {
    return html` <h3 colspan="2">${group.name}</h3> `;
  }

  renderPlaceholder(): TemplateResult {
    return html` <div></div>`;
  }
}

export class FlowFormRenderer extends FormRenderer<string> {
  renderForm(formModel: FormModel, options: RenderOptions): string {
    const formItems = this.renderFormItems(formModel, options);

    return `FormLayout formLayout = new FormLayout();
formLayout.setResponsiveSteps(
  new FormLayout.ResponsiveStep("0", 1),
  new FormLayout.ResponsiveStep("500px", 2)
);

${formItems.join("\n\n")}
`;
  }

  renderField(field: FormField): string {
    const effectiveFieldType =
      field.fieldType || field.suggestedFieldType || FieldType.TextField;
    let fieldType: string;

    switch (effectiveFieldType) {
      case FieldType.TextArea:
        fieldType = "TextArea";
        break;
      case FieldType.EmailField:
        fieldType = "EmailField";
        break;
      case FieldType.IntegerField:
        fieldType = "IntegerField";
        break;
      case FieldType.NumberField:
        fieldType = "NumberField";
        break;
      case FieldType.PasswordField:
        fieldType = "PasswordField";
        break;
      case FieldType.DatePicker:
        fieldType = "DatePicker";
        break;
      case FieldType.TimePicker:
        fieldType = "TimePicker";
        break;
      case FieldType.DateTimePicker:
        fieldType = "DateTimePicker";
        break;
      case FieldType.Checkbox:
        fieldType = "Checkbox";
        break;
      case FieldType.Select:
        fieldType = "Select";
        break;
      case FieldType.ComboBox:
        fieldType = "ComboBox";
        break;
      default:
        fieldType = "TextField";
    }

    return `${fieldType} ${field.beanProperty.name} = new ${fieldType}("${field.displayName}");
formLayout.add(${field.beanProperty.name}, ${field.colSpan});`;
  }

  renderGroupHeader(group: FieldGroup): string {
    return `formLayout.add(new H3("${group.name}"), 2);`;
  }

  renderPlaceholder(): string {
    return "formLayout.add(new Div()); // placeholder to fill row";
  }
}

export class HillaLitFormRenderer extends FormRenderer<string> {
  renderForm(formModel: FormModel, options: RenderOptions): string {
    const formItems = this.renderFormItems(formModel, options);

    return `<vaadin-form-layout>
${formItems.join("\n\n")}
</vaadin-form-layout>`;
  }

  renderField(field: FormField): string {
    let elementName: string;

    switch (field.fieldType) {
      case FieldType.TextArea:
        elementName = "vaadin-text-area";
        break;
      case FieldType.EmailField:
        elementName = "vaadin-email-field";
        break;
      case FieldType.IntegerField:
        elementName = "vaadin-integer-field";
        break;
      case FieldType.NumberField:
        elementName = "vaadin-number-field";
        break;
      case FieldType.PasswordField:
        elementName = "vaadin-password-field";
        break;
      case FieldType.DatePicker:
        elementName = "vaadin-date-picker";
        break;
      case FieldType.TimePicker:
        elementName = "vaadin-time-picker";
        break;
      case FieldType.DateTimePicker:
        elementName = "vaadin-date-time-picker";
        break;
      case FieldType.Checkbox:
        elementName = "vaadin-checkbox";
        break;
      case FieldType.Select:
        elementName = "vaadin-select";
        break;
      case FieldType.ComboBox:
        elementName = "vaadin-combo-box";
        break;
      default:
        elementName = "vaadin-text-field";
    }

    return `  <${elementName} label="${field.displayName}" colspan="${field.colSpan}"></${elementName}>`
  }

  renderGroupHeader(group: FieldGroup): string {
    return `  <h3 colspan="2">${group.name}</h3>`
  }

  renderPlaceholder(): string {
    return `  <div></div> <!-- placeholder to fill row -->`;
  }
}
