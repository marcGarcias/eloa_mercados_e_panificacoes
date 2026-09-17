import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ContentBanner } from '../../models/content.model';
import { smoothScrollToSection } from '../../core/utils/navigation.util';

@Component({
  selector: 'app-hero',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './hero.component.html',
  styleUrl: './hero.component.css'
})
export class HeroComponent {
  @Input() banner?: ContentBanner | null;

  onNavClick(event: Event, targetId: string): void {
    smoothScrollToSection(event, targetId);
  }
}

