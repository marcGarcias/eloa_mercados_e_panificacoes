import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { User } from '../../models/user.model';

@Component({
  selector: 'app-modal-usuario',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './modal-usuario.component.html',
  styleUrls: ['./modal-usuario.component.css']
})
export class ModalUsuarioComponent implements OnInit {
  @Input() user: User | null = null;
  @Output() saved = new EventEmitter<Partial<User>>();
  @Output() closed = new EventEmitter<void>();

  userForm: FormGroup;
  showPassword = false;

  constructor(private fb: FormBuilder) {
    this.userForm = this.fb.group({
      firstName: ['', Validators.required],
      lastName: ['', Validators.required],
      role: ['editor', Validators.required],
      password: ['', [Validators.required, Validators.minLength(8)]]
    });
  }

  ngOnInit() {
    if (this.user) {
      const parts = this.user.name.split(' ');
      const firstName = parts[0] || '';
      const lastName = parts.slice(1).join(' ') || '';
      this.userForm.patchValue({
        firstName,
        lastName,
        role: this.user.role,
        password: this.user.password || ''
      });
    }
  }

  close() {
    this.closed.emit();
  }

  save() {
    if (this.userForm.invalid) return;
    const v = this.userForm.value;
    const name = `${v.firstName} ${v.lastName}`.trim();
    this.saved.emit({
      name,
      role: v.role,
      password: v.password
    });
  }
}
