import { Component, CUSTOM_ELEMENTS_SCHEMA, inject, OnInit } from '@angular/core';
import { NavigationEnd, Router, RouterLink } from '@angular/router';
import { AsyncPipe, NgFor, NgIf } from '@angular/common';
import { filter, map } from 'rxjs';
import { routes as appRoutes } from '../../../app.routes';
import { AuthService } from '../../../core/auth/auth.service';
import { UserService } from '../../../core/user/user.service';
import { UserDTO } from '../../../core/models/user.models';

type MenuItem = { path: string; title: string };

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [AsyncPipe, RouterLink, NgFor, NgIf],
  templateUrl: './navbar.component.html',
  schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class NavbarComponent implements OnInit {

  private router = inject(Router);
  private authService = inject(AuthService);
  private userService = inject(UserService);

  menuOpen = false;
  userName: string = 'Usuario';
  isDarkMode: boolean = false;

  routes: MenuItem[] = appRoutes
    .filter((r: any) => !!r.path && r.path !== '**')
    .filter((r: any) => !String(r.path).startsWith('auth'))
    .filter((r: any) => !!r.title)
    .map((r: any) => ({
      path: r.path as string,
      title: r.title as string,
    }));

  pageTitle$ = this.router.events.pipe(
    filter(event => event instanceof NavigationEnd),
    map(event => (event as NavigationEnd).urlAfterRedirects),
    map(url => {
      const found: any = appRoutes.find(r => `/${r.path}` === url);
      return found?.title ?? 'Mapas';
    })
  );

  ngOnInit(): void {
    this.loadTheme();
    this.loadUser();

    this.router.events
      .pipe(filter(event => event instanceof NavigationEnd))
      .subscribe(()=> this.loadUser());
  }

  //  Cargar usuario
  private loadUser(): void {
    const raw = localStorage.getItem('user');

    if (raw) {
      try {
        const user = JSON.parse(raw);
        this.userName = user?.nombre ?? user?.email ?? 'Usuario';
        return;
      } catch {
        localStorage.removeItem('user');
      }
    }

    // Fallback si no existe user
    this.userService.me().subscribe({
      next: (user: UserDTO) => {
        this.userName = user?.nombre ?? user?.email ?? 'Usuario';
        localStorage.setItem('user', JSON.stringify(user));
      },
      error: () => {
        this.userName = 'Usuario';
      }
    });
  }

  // 🌙 Dark Mode
  private loadTheme(): void {
    const savedTheme = localStorage.getItem('theme');
    this.isDarkMode = savedTheme === 'dark';
    this.applyTheme();
  }

  toggleTheme(): void {
    this.isDarkMode = !this.isDarkMode;
    localStorage.setItem('theme', this.isDarkMode ? 'dark' : 'light');
    this.applyTheme();
  }

  private applyTheme(): void {
    document.documentElement.classList.toggle('dark', this.isDarkMode);
  }

  // Logout
  logout(): void {
    this.menuOpen = false;
    this.authService.logout();
    localStorage.removeItem('user');
    this.router.navigateByUrl('/');
  }
  routeIcons: Record<string, string> = {
    'fullscreen': 'mynaui:navigation-one-solid',
    'profile': 'mynaui:user-circle',
    'history': 'mynaui:map',
  }
}

