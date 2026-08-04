//Peticion que espera el backend
export interface RouteRequestDTO{
  direccionOrigen?: string | null;
  direccionDestino?: string |null;
  latOrigen?: number | null;
  lngOrigen?: number | null;
  latDestino?: number | null;
  lngDestino?: number | null;
}

//Alerta que espera el backend
export interface AlertDTO{
  nivel: string;
  color: string;
  mmLluvia: number;
  descripcion: string;
}

// Ruta guardada
export interface RouteDTO {
  idRuta: number;
  direccionOrigen: string;
  direccionDestino: string;
  distancia: number;
  duracion: number;
  fechaBusqueda: string;
  riesgo: boolean;
  idUsuario: number;
  coordenadasRuta: CoordinateDTO[];
}


//Respuesta del backend tras calcular ruta
export interface RouteResponseDTO{
  idRuta: number;
  distancia: number;
  duracion: number;
  riesgo: boolean;
  mensaje: string;
  coordenadasRuta: CoordinateDTO[];
  alertaClima: AlertDTO | null;
  rutaAlternativa: RouteDTO | null;
}

//Coordenada simple
export interface CoordinateDTO{ lat: number; lng: number;}
