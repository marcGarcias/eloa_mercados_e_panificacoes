import { Component, ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-catalog',
  standalone: true,
  imports: [CommonModule],
  template: '<h2>Catalog</h2>',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class CatalogComponent {}
