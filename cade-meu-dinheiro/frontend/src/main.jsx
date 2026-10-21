import React from "react";
import { createRoot } from "react-dom/client";
import App from "./App.jsx";
import "../../../../../Downloads/cade-meu-dinheiro-react-java/cade-meu-dinheiro/frontend/src/styles/tokens.css";
import "../../../../../Downloads/cade-meu-dinheiro-react-java/cade-meu-dinheiro/frontend/src/styles/global.css";

createRoot(document.getElementById("root")).render(
  <React.StrictMode>
    <App />
  </React.StrictMode>
);
