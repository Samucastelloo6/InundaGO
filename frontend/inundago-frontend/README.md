# InundaGO – Frontend

Aplicación multiplataforma desarrollada en Angular para el cálculo de rutas inteligentes teniendo en cuenta zonas inundables, nivel de riesgo y sistema de alertas.

El frontend gestiona la interacción del usuario, la visualización geoespacial mediante Mapbox y la comunicación segura con el backend a través de una API REST.

---

## Tecnologías

- Angular 20.3.9
- TypeScript
- Angular CLI
- Mapbox GL JS
- Tailwind CSS
- HttpClient (API REST)
- JWT para autenticación

---

## Arquitectura

El proyecto sigue una estructura modular basada en componentes y servicios.

### Estructura principal

src/app

- core  
  - auth (login, registro, interceptor JWT)  
- models (modelos y DTOs)  
- routes (servicios de comunicación con backend)  
- user (gestión de usuario)  
- features  
  - profile (perfil de usuario)  
- pages  
  - fullscreen-map-page (mapa y lógica principal)  
  - route-history-page (historial de rutas)  
- shared  
  - components/navbar  

La comunicación con el backend está centralizada en servicios y protegida mediante interceptor JWT.

---

## Funcionalidades

- Registro e inicio de sesión de usuarios  
- Autenticación mediante JWT  
- Selección manual de origen y destino en el mapa  
- Geolocalización automática  
- Cálculo de ruta principal  
- Cálculo de ruta alternativa  
- Representación visual de rutas con coloreado dinámico según nivel de riesgo  
- Sistema de alertas asociado al riesgo detectado  
- Activación y desactivación de zonas inundables  
- Reverse geocoding  
- Historial de rutas consultadas por el usuario  
- Actualización dinámica sin recarga de página  
- Interfaz responsive desarrollada con Tailwind CSS  

---

## Requisitos

- Node.js  
- Angular CLI  
- Token de Mapbox  
- Backend en ejecución  

---

## Instalación

Instalar dependencias:

```bash
npm install

