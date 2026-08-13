import { Component } from '@angular/core';

@Component({
  selector: 'app-login-cms',
  imports: [],
  templateUrl: './login-cms.html',
  styleUrl: './login-cms.css',
})
export class LoginCms {
  showPassword = false;

  togglePassword() {
    this.showPassword = !this.showPassword;
  }
}
