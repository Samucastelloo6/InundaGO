import { Injectable } from "@angular/core";
import { environment } from "../../../environments/environment";
import { HttpClient } from "@angular/common/http";
import { Observable } from "rxjs";
import { RouteDTO, RouteRequestDTO, RouteResponseDTO } from "../models/route.models";






@Injectable({ providedIn: 'root' })
export class MapApiService {
  private api = environment.apiUrl;

  constructor(private http: HttpClient) { }


  //Calcula la ruta en el backend
  calculate(body: RouteRequestDTO): Observable<RouteResponseDTO> {
    return this.http.post<RouteResponseDTO>(`${this.api}/api/v1/routes/calculate`, body);

  }
  //Zonas inundables
  getFloodZones(): Observable<any> {

    return this.http.get<any>(`${this.api}/api/v1/flood-zones`);
  }

  getMyRoutes(): Observable<RouteDTO[]> {
    return this.http.get<RouteDTO[]>(`${this.api}/api/v1/routes/me`);
  }


}
