import { environment } from "../environments/environment";
module.exports = {
  rootTranslationsPath: "src/assets/i18n/",
  langs: ["en", "fr"],
  keysManager: {},
  flatten: {
    aot: environment.production,
  },
};
