//Lo que Angular envia al backend
export interface LoginRequestDTO{
  email: string;
  password: string;
}

//Lo que el backend devuelve
export interface AuthResponseDTO{
  accesoToken: string;
}

