import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ContentDiferenciais } from '../../models/content.model';

@Component({
  selector: 'app-features',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './features.component.html',
  styleUrl: './features.component.css'
})
export class FeaturesComponent {
  @Input() diferenciais?: ContentDiferenciais | null;
}
