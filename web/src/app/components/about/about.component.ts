import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ContentSobre } from '../../models/content.model';

@Component({
  selector: 'app-about',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './about.component.html',
  styleUrl: './about.component.css'
})
export class AboutComponent {
  @Input() sobre?: ContentSobre | null;

  get validList() {
    return (this.sobre?.lista || []).filter(item => 
      (item.nome && item.nome.trim()) || (item.descricao && item.descricao.trim())
    );
  }

  get hasValidItems(): boolean {
    return this.validList.length > 0;
  }
}
