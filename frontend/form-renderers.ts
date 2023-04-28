import { html, TemplateResult } from "lit";
import {
  html as staticHtml,
  literal,
  StaticValue,
} from "lit/static-html.js";
import FieldGroup from "Frontend/generated/de/sissbruecker/formbuilder/model/FieldGroup";
import FormModel from "Frontend/generated/de/sissbruecker/formbuilder/model/FormModel";
import FormField from "Frontend/generated/de/sissbruecker/formbuilder/model/FormField";
import FieldType from "Frontend/generated/de/sissbruecker/formbuilder/model/FieldType";

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

      if (spanTotal % 2 !== 0 && lastField && lastField.colSpan === 1) {
        // add placeholder to fill row
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
        label="${field.displayName}"
        colspan="${field.colSpan}"
      ></${fieldTagName}>`;
  }

  renderGroupHeader(group: FieldGroup): TemplateResult {
    return html` <h3 colspan="2">${group.name}</h3> `;
  }

  renderPlaceholder(): TemplateResult {
    return html` <div></div>`;
  }
}
