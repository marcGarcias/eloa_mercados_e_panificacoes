import { Component } from '@angular/core';
import { HeaderComponent } from './components/header/header';
import { HeroComponent } from './components/hero/hero';
import { CatalogComponent } from './components/catalog/catalog';
import { AboutComponent } from './components/about/about';
import { ContactComponent } from './components/contact/contact';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    HeaderComponent,
    HeroComponent,
    CatalogComponent,
    AboutComponent,
    ContactComponent
  ],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {}
