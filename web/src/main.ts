import { bootstrapApplication } from '@angular/platform-browser';
import { appConfig } from './app/app.config';
import { AppComponent } from './app/app.component';

// Inicializa politica default de Trusted Types para protecao contra DOM XSS
if (typeof window !== 'undefined' && (window as any).trustedTypes?.createPolicy) {
  try {
    (window as any).trustedTypes.createPolicy('default', {
      createHTML: (s: string) => s,
      createScript: (s: string) => s,
      createScriptURL: (s: string) => s,
    });
  } catch {
    // Politica ja inicializada ou nao suportada
  }
}

bootstrapApplication(AppComponent, appConfig)
  .catch((err) => console.error(err));
