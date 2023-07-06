import { css, html, LitElement } from "lit";
import { customElement } from "lit/decorators.js";

@customElement("form-generator-empty")
export class Empty extends LitElement {
  static styles = css`
    :host {
      width: 100%;
      height: 100%;
      display: flex;
      justify-content: center;
      align-items: center;
    }

    .message {
      max-width: 300px;
    }
  `;

  render() {
    return html` <div class="message">
      To get started, <em>Load a demo bean</em>, or
      <em>Upload the source code of a custom bean</em>, and then click
      <em>Generate form</em>.
    </div>`;
  }
}
