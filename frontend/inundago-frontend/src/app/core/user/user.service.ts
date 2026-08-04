import { Injectable } from "@angular/core";
import { environment } from "../../../environments/environment";
import { HttpClient } from "@angular/common/http";
import { Observable } from "rxjs";
import { RolDTO, UserDTO, UserRequestDTO, UserResponseDTO } from "../models/user.models";





@Injectable({
  providedIn: 'root',
})


export class UserService {

  private readonly baseUrl = environment.apiUrl;

  constructor(private http: HttpClient){}

  me(): Observable<UserDTO>{

    return this.http.get<UserDTO>(`${this.baseUrl}/api/v1/user/me`);
  }

  updateUser(payload: UserRequestDTO): Observable<UserResponseDTO>{
    return this.http.put<UserResponseDTO>(`${this.baseUrl}/api/v1/user`,payload);
  }



}
