import { Component, Input, Output, EventEmitter, ChangeDetectionStrategy, HostListener, ChangeDetectorRef, OnChanges } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-modal',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './modal.component.html',
  styleUrls: ['./modal.component.css'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ModalComponent implements OnChanges {
  @Input() title: string = '';
  @Input() isOpen: boolean = false;
  @Output() close = new EventEmitter<void>();

  constructor(private cdr: ChangeDetectorRef) {}

  ngOnChanges(): void {
    this.cdr.markForCheck();
  }

  @HostListener('keydown.escape')
  onEscape() {
    if (this.isOpen) {
      this.closeModal();
    }
  }

  closeModal() {
    this.close.emit();
  }
}

