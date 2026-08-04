import { CommonModule } from '@angular/common';
import { Component, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { Router, RouterLink } from '@angular/router';
import { RegisterRequestDTO } from '../../models/registerRequestDTO.models';
import { AuthService } from '../auth.service';

@Component({
  selector: 'app-register-page',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './register-page.html',
  schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class RegisterPageComponent {

  nombre: string = '';
  apellidos: string = '';
  email: string = '';
  password: string = '';

  errorMessage: string = '';

  constructor(private authService: AuthService, private router: Router) { }

  isFormValid(): boolean {
    return (
      this.nombre.trim() !== '' &&
      this.apellidos.trim() !== '' &&
      this.email.trim() !== '' &&
      this.password.trim() !== ''
    );

  }

  onRegister(): void {

    this.errorMessage = '';
    if (!this.isFormValid()) {
      this.errorMessage = 'Rellena todos los campos';
      return;
    }

    const payload: RegisterRequestDTO = {
      nombre: this.nombre.trim(),
      apellidos: this.apellidos.trim(),
      email: this.email.trim(),
      password: this.password,
    };
    this.authService.register(payload).subscribe({
      next: () => {

        this.nombre = '';
        this.apellidos = '';
        this.email = '';
        this.password = '';
        this.router.navigateByUrl('/fullscreen');

      }, error: (err) => {
        this.errorMessage = err?.error?.message ?? 'Error inesperado al registrar';

      },
    });

  }

  clearError(): void {
    this.errorMessage = '';
  }

}
