import { CommonModule } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators, FormGroup } from '@angular/forms';
import { RouterModule, Router } from '@angular/router';
import { AuthResponseDTO } from '../../models/auth.models';
import { UserDTO } from '../../models/user.models';
import { UserService } from '../../user/user.service';
import { AuthService } from '../auth.service';
import { Loading } from '../../../shared/components/loading/loading';



@Component({
  selector: 'app-login-page',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule, Loading],
  templateUrl: './login-page.html',
  schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class LoginPageComponent {

  form!: FormGroup;
  errorMessage: string | null = null;
  isLoading: boolean = false;



  constructor(private fb: FormBuilder, private authService: AuthService, private userService: UserService,
    private router: Router
  ) {

    //Creo un formulario con dos campos email y pass
    this.form = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required]],
    });
  }

  onSubmit(): void {
    //Si el formulario esta mal se para
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }


    //Construir el objeto que le envio al backend
    const request = {
      email: this.form.value.email!,
      password: this.form.value.password!,
    };

    this.isLoading = true;
    this.authService.login(request).subscribe({

      next: (res: AuthResponseDTO) => {

        //Pido al backend que usuario es
        this.userService.me().subscribe({
          next: (user: UserDTO) => {
            console.log(`Hola, ${user.nombre}`);
            localStorage.setItem('user', JSON.stringify(user));

            //Redirijo al mapa
            this.router.navigateByUrl('/fullscreen')
          },
          error: (err: HttpErrorResponse) => {
            this.isLoading = false;
            this.errorMessage = err.error?.message ?? 'Error al cargar usuario';
          }
        });
      },
      error: (err: HttpErrorResponse) => {
        this.isLoading = false;
        this.errorMessage = err.error?.error ?? 'Error al iniciar sesion';
      }
    });
  }

}
