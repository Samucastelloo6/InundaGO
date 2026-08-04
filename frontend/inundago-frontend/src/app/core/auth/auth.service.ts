import { Injectable } from '@angular/core';
import { environment } from '../../../environments/environment';
import { HttpClient } from '@angular/common/http';
import { AuthResponseDTO, LoginRequestDTO } from '../models/auth.models';
import { Observable, tap } from 'rxjs';
import { RegisterRequestDTO } from '../models/registerRequestDTO.models';





@Injectable({
  providedIn: 'root',
})
export class AuthService {

  private static readonly TOKEN_KEY = 'auth_token';


  //Url base del backend
  private readonly baseUrl = environment.apiUrl;


  constructor(private http: HttpClient) { }

  //Llamo al backend: POST /auth/login

  login(payload: LoginRequestDTO): Observable<AuthResponseDTO> {
    return this.http.post<AuthResponseDTO>(
      `${this.baseUrl}/auth/login`,
      payload
    ).pipe(tap(res => this.setToken(res.accesoToken)));
  }

  //llamo al backend: POST /auth/register
  register(payload: RegisterRequestDTO): Observable<AuthResponseDTO> {
    return this.http.post<AuthResponseDTO>(
      `${this.baseUrl}/auth/register`,
      payload
    ).pipe(tap(res => this.setToken(res.accesoToken)));

  }

  setToken(token: string): void {
    localStorage.setItem(AuthService.TOKEN_KEY, token);
  }

  getToken(): string | null {
    return localStorage.getItem(AuthService.TOKEN_KEY);
  }

  isLoggedIn(): boolean {
    return !!this.getToken();
  }

  logout(): void {
    localStorage.removeItem(AuthService.TOKEN_KEY);
    localStorage.removeItem('user');
  }


}
