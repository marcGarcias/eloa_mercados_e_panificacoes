import { Component } from '@angular/core';
import { ButtonComponent } from '../../shared/button/button';
import { environment } from '../../../environments/environment';

@Component({
  selector: 'app-contact',
  standalone: true,
  imports: [ButtonComponent],
  templateUrl: './contact.html',
  styleUrl: './contact.css'
})
export class ContactComponent {
  whatsappUrl = environment.whatsappUrl;
}
