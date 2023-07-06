import { html, render } from "lit";
import "./views/main.ts";

render(
  html` <form-generator></form-generator>`,
  document.getElementById("outlet")!
);
