import { customElement, property, query } from "lit/decorators.js";
import { css, html, LitElement, PropertyValues } from "lit";
import hljs from "highlight.js/lib/core";
import javaLanguage from "highlight.js/lib/languages/java";
import htmlLanguage from "highlight.js/lib/languages/xml";
// @ts-ignore
import highlightStyles from "highlight.js/styles/github.css?inline";

hljs.registerLanguage("java", javaLanguage);
hljs.registerLanguage("html", htmlLanguage);
hljs.configure({ languages: ["java", "html"] });

@customElement("form-code-view")
export class CodeView extends LitElement {
  static get styles() {
    return [
      highlightStyles,
      css`
        :host {
          display: block;
          font-size: 13px;
        }

        #code {
          white-space: pre;
          font-family: monospace;
          background: none !important;
        }
      `,
    ];
  }

  @property({})
  public code: string = "";
  @property({})
  public language: string = "";
  @query("#code")
  private codeElement?: HTMLDivElement;

  protected update(changedProperties: PropertyValues) {
    super.update(changedProperties);

    if ((changedProperties.has("code") || changedProperties.has("language")) && this.codeElement) {
      this.codeElement.textContent = this.code;
      this.codeElement.className = this.language;
      hljs.highlightElement(this.codeElement);
    }
  }

  render() {
    return html` <div id="code"></div> `;
  }
}
