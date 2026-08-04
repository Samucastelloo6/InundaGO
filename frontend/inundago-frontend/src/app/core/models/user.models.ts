export interface RolDTO {
  idRol: number;
  nombre: string;

}

export interface UserDTO {
  idUsuario: number;
   nombre:string;
  apellidos: string;
  fechaLogin: string;
  email:string;
  rolDTO: RolDTO;

}
export interface UserRequestDTO {
  idUsuario: number;
  nombre: string;
  apellidos: string;
  email: string;
}

export interface UserResponseDTO{
  idUsuario:number;
  nombre: string;
  apellidos: string;
  email: string;
  rolNombre: string;
}

