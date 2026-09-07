import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-icon',
  standalone: true,
  imports: [CommonModule],
  template: `
    <img 
      [src]="'/assets/icons/' + name + '.svg'" 
      [alt]="alt || name" 
      [style.width.px]="size || null" 
      [style.height.px]="size || null" 
      [class]="className"
      loading="lazy" />
  `,
  styles: [`
    :host {
      display: inline-flex;
      align-items: center;
      justify-content: center;
      line-height: 0;
    }
    img {
      display: inline-block;
      max-width: 100%;
      height: auto;
    }
  `]
})
export class AppIconComponent {
  @Input({ required: true }) name!: string;
  @Input() size?: number;
  @Input() alt?: string;
  @Input() className: string = 'app-icon';
}
