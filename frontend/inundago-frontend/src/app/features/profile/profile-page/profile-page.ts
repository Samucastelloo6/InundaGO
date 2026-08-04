import { Component, OnDestroy, OnInit, inject } from '@angular/core';
import { UserDTO, UserRequestDTO } from '../../../core/models/user.models';
import { UserService } from '../../../core/user/user.service';
import { Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { AuthService } from '../../../core/auth/auth.service';

@Component({
  selector: 'app-profile-page',
  standalone: true,
  imports: [CommonModule, RouterModule, ReactiveFormsModule],
  templateUrl: './profile-page.html',
})
export class ProfilePageComponent implements OnInit, OnDestroy {
  user?: UserDTO;
  loading = true;
  saving = false;

  error?: string;
  success?: string;

  editMode = false;

  private fb = inject(FormBuilder);
  private logoutTimeout?: ReturnType<typeof setTimeout>;
  private authService = inject(AuthService);


  form = this.fb.group({
    nombre: ['', [Validators.required, Validators.minLength(2)]],
    apellidos: ['', [Validators.required, Validators.minLength(2)]],
    email: ['', [Validators.required, Validators.email]],
  });

  constructor(
    private userService: UserService,
    private router: Router
  ) { }


  ngOnDestroy(): void {
    clearTimeout(this.logoutTimeout);
  }


  ngOnInit(): void {
    this.loadMe();


  }
  loadMe(): void {
    this.loading = true;
    this.error = undefined;


    this.userService.me().subscribe({
      next: (u) => {
        this.user = u;
        this.loading = false;
      },
      error: (e) => {
        console.error(e);
        this.error = 'No se pudo cargar el perfil';
        this.loading = false;
      }
    });

  }

  startEdit(): void {
    this.editMode = true;
    this.error = undefined;
    this.success = undefined;

    if (this.user) {
      this.form.patchValue({
        nombre: this.user.nombre,
        apellidos: this.user.apellidos,
        email: this.user.email,
      })
    }
  }

  cancelEdit(): void {
    this.editMode = false;
    this.error = undefined;
    this.success = undefined;

    // volver a valores originales
    if (this.user) {
      this.form.patchValue({
        nombre: this.user.nombre,
        apellidos: this.user.apellidos,
        email: this.user.email,
      });
    }
  }
  roleLabel(role?: string | null): string {
    switch (role) {
      case 'ROLE_USER': return 'Usuario';
      case 'ROLE_ADMIN': return 'Administrador';
      default: return role ?? '—';
    }
  }

  save(): void {
    if (!this.user) return;

    this.form.markAllAsTouched();
    if (this.form.invalid) return;

    const payload: UserRequestDTO = {
      idUsuario: this.user.idUsuario,
      nombre: this.form.value.nombre!,
      apellidos: this.form.value.apellidos!,
      email: this.form.value.email!,
    };

    this.saving = true;
    this.error = undefined;
    this.success = undefined;

    this.userService.updateUser(payload).subscribe({
      next: () => {
        if (this.user) {
          this.user = {
            ...this.user,
            nombre: payload.nombre,
            apellidos: payload.apellidos,
          };
          localStorage.setItem('user', JSON.stringify(this.user));
        }

        this.success = 'Perfil actualizado correctamente';
        this.editMode = false;
        this.saving = false;
      },
      error: (e) => {
        console.error(e);
        this.error = 'No se pudo actualizar el perfil';
        this.saving = false;
      },
    });
  }
  logout(): void {
    this.authService.logout();
    this.router.navigate(['/']);
  }

}
