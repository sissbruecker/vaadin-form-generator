import { html, render } from "lit";
import "./views/generator.ts";

render(
  html` <form-generator></form-generator>`,
  document.getElementById("outlet")!
);
