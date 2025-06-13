import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import App from "./components/App.tsx";
import {Provider} from "react-redux";
import store from "./storage/AppStorage.ts";
// eslint-disable-next-line @typescript-eslint/ban-ts-comment
// @ts-expect-error
import '@fontsource/saira';

createRoot(document.getElementById('root')!).render(
  <StrictMode>
      <Provider store={store}>
      <App/>
      </Provider>
  </StrictMode>,
);