import { Routes } from '@angular/router';
import { FullscreenMapPageComponent } from './pages/fullscreen-map-page.component/fullscreen-map-page.component';
import { ProfilePageComponent } from './features/profile/profile-page/profile-page';
import { RouteHistoryPageComponent } from './pages/route-history-page.ts/route-history-page';
import { LoginPageComponent } from './core/auth/login-page/login-page';
import { authGuard } from './core/auth/auth.guard';


export const routes: Routes = [

   { path: '', component: LoginPageComponent, title: 'Login' },

{
    path: 'fullscreen',
    component: FullscreenMapPageComponent,
    title: 'Mapa',
    canActivate: [authGuard],
  },
  {
  path: 'auth/register',
  loadComponent: () =>
    import('./core/auth/register-page/register-page').then(m => m.RegisterPageComponent),
    },
  {
    path: 'profile',
    component: ProfilePageComponent,
    title: 'Perfil',
    canActivate: [authGuard],
  },
  {
    path: 'history',
    component: RouteHistoryPageComponent,
    title: 'Mis rutas',
    canActivate: [authGuard],
  },
  {
  path: '',
  redirectTo: 'login',
  pathMatch: 'full'
},


];
