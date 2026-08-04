
# InundaGO

InundaGO es una aplicación web multiplataforma para el cálculo de rutas inteligentes teniendo en cuenta zonas inundables y nivel de riesgo asociado.

La aplicación puede ejecutarse en cualquier dispositivo con navegador web moderno (Windows, macOS, Linux, Android o iOS), ofreciendo una interfaz responsive y dinámica.

El sistema integra frontend, backend y base de datos geoespacial, permitiendo generar rutas principales y alternativas, visualizar zonas de riesgo, almacenar historial de consultas y gestionar usuarios autenticados.

---

## Arquitectura del Sistema

El proyecto sigue una arquitectura cliente-servidor.

Usuario  
↓  
Aplicación Web (Angular)  
↓  
API REST (Spring Boot)  
↓  
Capa de servicios  
↓  
Capa de repositorios  
↓  
PostgreSQL + PostGIS  

La comunicación se realiza mediante peticiones HTTP con autenticación basada en JWT.

---

## Tecnologías

### Frontend

- Angular 20.3.9  
- TypeScript  
- Mapbox GL JS  
- Tailwind CSS  
- HttpClient  
- JWT  

### Backend

- Java 17  
- Spring Boot  
- Maven  
- Spring Security  
- JPA / Hibernate  

### Base de datos

- PostgreSQL  
- Extensión PostGIS  

---

## Características principales

- Aplicación web multiplataforma basada en navegador  
- Interfaz responsive adaptable a distintos dispositivos  
- Registro e inicio de sesión de usuarios  
- Autenticación mediante JWT  
- Cálculo de ruta principal  
- Cálculo de ruta alternativa  
- Representación visual de rutas según nivel de riesgo  
- Sistema de alertas asociado al riesgo detectado  
- Visualización opcional de zonas inundables  
- Reverse geocoding  
- Historial de rutas por usuario  
- Actualización dinámica sin recarga de página  

---

## Configuración del Backend

Requisitos:

- Java 17  
- Maven  

Ejecutar:

mvn spring-boot:run

---

## Configuración de la Base de Datos

Es necesario habilitar la extensión PostGIS:

CREATE EXTENSION IF NOT EXISTS postgis;

---

## Configuración del Frontend

Requisitos:
 
- Angular CLI  

Instalar dependencias:

npm install  

Ejecutar:

ng serve  

---

## Seguridad

- Autenticación basada en JWT  
- Protección de endpoints mediante Spring Security  
- Interceptor HTTP en frontend  
- Gestión de variables sensibles por entorno  

---

## Preparación para Despliegue

La aplicación permite configuración por entorno tanto en frontend como en backend.

Las credenciales y configuraciones sensibles no están codificadas directamente en el código fuente.
CREATE EXTENSION IF NOT EXISTS postgis;




