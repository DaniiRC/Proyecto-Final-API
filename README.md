<div align="center">

<img src="https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white"/>
<img src="https://img.shields.io/badge/Java_21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white"/>
<img src="https://img.shields.io/badge/MySQL_8-4479A1?style=for-the-badge&logo=mysql&logoColor=white"/>
<img src="https://img.shields.io/badge/JWT-000000?style=for-the-badge&logo=jsonwebtokens&logoColor=white"/>
<img src="https://img.shields.io/badge/Clever_Cloud-F26522?style=for-the-badge&logo=icloud&logoColor=white"/>
<img src="https://img.shields.io/badge/Maven-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white"/>

<br/><br/>

# 🖥 Agenda Académica — API REST

**Backend completo para la app Agenda Académica.**  
API RESTful construida con Spring Boot 4 y Spring Security 6. Gestiona usuarios, tareas, grupos académicos y autenticación JWT. Desplegada en producción sobre Clever Cloud + MySQL.

<br/>

[![Status](https://img.shields.io/badge/Estado-Terminado-brightgreen?style=for-the-badge)](https://proyecto-final-api.cleverapps.io)
[![License](https://img.shields.io/badge/License-MIT-green?style=for-the-badge)](LICENSE)
[![Java](https://img.shields.io/badge/Java-21-orange?style=for-the-badge)](https://jdk.java.net/21/)

</div>

---

## 🗂 Endpoints de la API

### 🔓 Rutas públicas (sin autenticación)

| Método | Ruta | Descripción |
|---|---|---|
| `POST` | `/api/usuarios/login` | Autenticación con email + contraseña → devuelve JWT |
| `POST` | `/api/usuarios/registro` | Crear nueva cuenta |
| `POST` | `/api/usuarios/google-login` | Login con token de Google Firebase |
| `POST` | `/api/usuarios/enviar-codigo` | Envía código de recuperación al email |
| `POST` | `/api/usuarios/verificar-y-cambiar` | Verifica código y actualiza contraseña |
| `GET`  | `/uploads/**` | Servir imágenes de perfil y grupos |

### 🔐 Rutas protegidas (requieren `Authorization: Bearer <token>`)

#### Usuarios
| Método | Ruta | Descripción |
|---|---|---|
| `GET` | `/api/usuarios/{id}` | Obtener datos del usuario |
| `PUT` | `/api/usuarios/{id}` | Actualizar perfil |
| `POST` | `/api/usuarios/{id}/foto` | Subir foto de perfil (multipart) |
| `GET` | `/api/usuarios/{id}/grupos` | Grupos del usuario |

#### Eventos / Tareas
| Método | Ruta | Descripción |
|---|---|---|
| `GET` | `/api/eventos/usuario/{id}` | Todos los eventos del usuario (personales + grupos) |
| `GET` | `/api/eventos/{id}` | Detalle de un evento |
| `GET` | `/api/eventos/grupo/{grupoId}` | Eventos de un grupo |
| `POST` | `/api/eventos/crear` | Crear nuevo evento |
| `PUT` | `/api/eventos/{id}` | Editar evento |
| `DELETE` | `/api/eventos/{id}` | Eliminar evento |
| `POST` | `/api/eventos/{id}/completar` | Marcar como completado |
| `POST` | `/api/eventos/{id}/tiempo-invertido` | Guardar tiempo de foco (Pomodoro) |
| `POST` | `/api/eventos/{id}/nota` | Registrar nota obtenida |

#### Grupos
| Método | Ruta | Descripción |
|---|---|---|
| `GET` | `/api/grupos` | Todos los grupos |
| `GET` | `/api/grupos/{id}` | Detalle de un grupo |
| `GET` | `/api/grupos/codigo/{codigo}` | Buscar grupo por código |
| `GET` | `/api/grupos/usuario/{id}` | Grupos del usuario |
| `POST` | `/api/grupos/crear` | Crear grupo (requiere rol ADMIN/PROFESOR) |
| `POST` | `/api/grupos/{codigo}/unirse` | Unirse a un grupo |
| `POST` | `/api/grupos/{id}/salir` | Abandonar un grupo |
| `POST` | `/api/grupos/{id}/foto` | Subir foto del grupo |

#### Asignaturas
| Método | Ruta | Descripción |
|---|---|---|
| `GET` | `/api/asignaturas/usuario/{id}` | Asignaturas del usuario |
| `POST` | `/api/asignaturas` | Crear asignatura |
| `DELETE` | `/api/asignaturas/{id}` | Eliminar asignatura |

#### Subtareas
| Método | Ruta | Descripción |
|---|---|---|
| `POST` | `/api/subtareas` | Crear subtarea |
| `PUT` | `/api/subtareas/{id}/completar` | Marcar subtarea como completada |
| `DELETE` | `/api/subtareas/{id}` | Eliminar subtarea |

---

## 🏗 Estructura del proyecto

```
src/main/java/com/agenda/backend_academico/
│
├── controller/          # Capa REST (entrada de peticiones HTTP)
│   ├── UsuarioController.java
│   ├── EventoController.java
│   ├── GrupoController.java
│   ├── AsignaturaController.java
│   └── SubtareaController.java
│
├── service/             # Lógica de negocio
│   ├── EventoService.java
│   ├── GrupoService.java
│   ├── CodigoVerificacionService.java
│   └── FileStorageService.java
│
├── repository/          # Spring Data JPA (acceso a BD)
│   ├── EventoRepository.java
│   ├── UsuarioRepository.java
│   ├── GrupoRepository.java
│   ├── AsignaturaRepository.java
│   ├── SubtareaRepository.java
│   └── CodigoVerificacionRepository.java
│
├── model/               # Entidades JPA (mapeo a tablas MySQL)
│   ├── Usuario.java
│   ├── Evento.java
│   ├── Grupo.java
│   ├── Asignatura.java
│   ├── Subtarea.java
│   └── CodigoVerificacion.java
│
├── dto/                 # Objetos de transferencia de datos
│   ├── request/         # Cuerpo de las peticiones entrantes
│   └── response/        # Forma de las respuestas salientes
│
├── security/            # Spring Security + JWT
│   ├── JwtUtils.java    # Generación y validación de tokens
│   └── JwtAuthFilter.java # Filtro HTTP para autenticar peticiones
│
├── config/
│   ├── SecurityConfig.java  # Configuración de Spring Security
│   └── WebConfig.java       # CORS
│
└── component/
    └── LimpiezaCodigosComponent.java  # @Scheduled: limpia códigos expirados
```

---

## ⚙️ Configuración

### Variables de entorno requeridas

```properties
# Base de datos
DB_URL=jdbc:mysql://host:3306/database
DB_USUARIO=username
DB_CONTRASENA=password

# Mail (Gmail App Password)
MAIL_USUARIO=tu@gmail.com
MAIL_PASSWORD=tu_app_password

# JWT
JWT_SECRET=tu_clave_secreta_base64_minimo_256bits
JWT_EXPIRATION=86400000
```

### application.properties (estructura)

```properties
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USUARIO}
spring.datasource.password=${DB_CONTRASENA}

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false

spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=${MAIL_USUARIO}
spring.mail.password=${MAIL_PASSWORD}
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

---

## 🚀 Instalación y ejecución local

```bash
# 1. Clonar el repositorio
git clone https://github.com/DaniiRC/Proyecto-Final-API.git
cd Proyecto-Final-API

# 2. Crear base de datos MySQL
mysql -u root -p
> CREATE DATABASE agenda_academica;
> exit

# 3. Importar el esquema
mysql -u root -p agenda_academica < schema.sql

# 4. Configurar variables de entorno
cp .env.example .env
# Editar .env con tus credenciales

# 5. Compilar y ejecutar
mvn spring-boot:run

# La API estará disponible en:
# http://localhost:8080
```

### Verificar que funciona

```bash
curl -X POST http://localhost:8080/api/usuarios/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@test.com","password":"1234"}'

# Respuesta esperada:
# { "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6...", "id": 1, "nombre": "Admin" }
```

---

## 🔐 Seguridad

### Flujo de autenticación JWT

```
Cliente                    Servidor
  │                           │
  │── POST /login ──────────▶│
  │   {email, password}       │── BCrypt.matches() ──▶ BD
  │                           │◀── usuario válido ─────│
  │◀── { token } ────────────│
  │                           │
  │── GET /api/eventos ──────▶│
  │   Authorization: Bearer   │── JwtAuthFilter ──────▶ valida firma
  │                           │── SecurityContext ──────▶ usuario autenticado
  │◀── [ eventos ] ──────────│
```

- Contraseñas: **BCrypt** (factor 10)
- Tokens: **JJWT** 0.11.5, firmados con HMAC-SHA256, expiran en 24h
- Sesión: **Stateless** (no hay HttpSession)
- CSRF: Desactivado (API REST pura)

---

## 🌐 Despliegue en producción

El backend está desplegado en **Clever Cloud**:
- Instancia: Java (Maven)
- Base de datos: MySQL Add-on
- Variables de entorno configuradas en el panel de Clever Cloud

```bash
# Deploy automático via git push
git remote add clever git+ssh://git@push.clever-cloud.com/app_xxxx.git
git push clever main
```
---


<div align="center">
  Desarrollado por <strong>Daniel Ruiz Cocera</strong> · IES Las Fuentezuelas · 2ºDAM · 2026<br/>
  <a href="https://github.com/DaniiRC/Agenda-Academica">📱 Repositorio de la App Android</a>
</div>
