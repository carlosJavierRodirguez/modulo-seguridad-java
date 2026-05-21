# modulo-seguridad-java

Módulo de seguridad reutilizable en Java basado en Spring Boot, diseñado para ser consumido por aplicaciones web y móviles mediante autenticación JWT y control de acceso basado en roles.

---

## 🚀 Creación del proyecto (Sprint Initializr)

Este proyecto fue generado utilizando **Spring Initializr**, con una configuración base pensada para un módulo de seguridad desacoplado y reutilizable.

### 🔧 Configuración utilizada

- **Project:** Maven
- **Language:** Java
- **Spring Boot:** 4.0.3
- **Packaging:** Jar
- **Java:** 17
- **Group:** `com.carlos.security`
- **Artifact:** `security-module`
- **Package name:** `com.carlos.security.core`
- **Configuration format:** YAML

### 📦 Dependencias incluidas

- Spring Web
- Spring Security
- Spring Data JPA
- OAuth2 Resource Server
- OAuth2 Authorization Server
- PostgreSQL Driver
- Validation
- Lombok

---

## 📥 ¿Cómo descargar el proyecto base?

1. Ingresa al siguiente enlace de **Spring Initializr**, el cual ya contiene toda la configuración del proyecto:

   👉 https://start.spring.io/#!type=maven-project&language=java&platformVersion=4.0.3&packaging=jar&configurationFileFormat=yaml&jvmVersion=17&groupId=com.carlos.security&artifactId=security-module&name=security-module&description=Reusable%20security%20module%20with%20JWT%20authentication%20and%20role-based%20authorization&packageName=com.carlos.security.core&dependencies=lombok,postgresql,data-jpa,security,web,oauth2-resource-server,oauth2-authorization-server,validation

2. Presiona el botón **Generate**.
3. Se descargará un archivo `.zip` con el proyecto base.
4. Descomprime el archivo y ábrelo en tu IDE de preferencia (IntelliJ IDEA recomendado).
5. A partir de este punto, el proyecto queda listo para comenzar el desarrollo del módulo de seguridad.

## 📦 Estructura del Proyecto

```text
com.carlos.security.core
│
├── config/                          # Configuraciones
│   ├── SecurityConfig.java
│   ├── JwtConfig.java
│   ├── CorsConfig.java
│   └── OpenApiConfig.java
│
├── domain/                          # Capa de Dominio (Entidades y Lógica de Negocio)
│   ├── model/
│   │   ├── User.java
│   │   ├── Role.java
│   │   ├── Permission.java
│   │   └── RefreshToken.java
│   │
│   ├── repository/                  # Interfaces de repositorio
│   │   ├── UserRepository.java
│   │   ├── RoleRepository.java
│   │   └── RefreshTokenRepository.java
│   │
│   ├── exception/                   # Excepciones de dominio
│   │   ├── UserNotFoundException.java
│   │   ├── InvalidCredentialsException.java
│   │   └── TokenExpiredException.java
│   │
│   └── valueobject/                 # Value Objects
│       ├── Email.java
│       └── Password.java
│
├── application/                     # Capa de Aplicación (Casos de Uso)
│   ├── service/
│   │   ├── AuthenticationService.java
│   │   ├── UserService.java
│   │   ├── RoleService.java
│   │   └── TokenService.java
│   │
│   ├── dto/
│   │   ├── request/
│   │   │   ├── LoginRequest.java
│   │   │   ├── RegisterRequest.java
│   │   │   └── RefreshTokenRequest.java
│   │   │
│   │   └── response/
│   │       ├── AuthResponse.java
│   │       └── UserResponse.java
│   │
│   └── mapper/
│       ├── UserMapper.java
│       └── RoleMapper.java
│
├── infrastructure/                  # Capa de Infraestructura
│   ├── persistence/
│   │   ├── entity/
│   │   │   ├── UserEntity.java
│   │   │   ├── RoleEntity.java
│   │   │   └── PermissionEntity.java
│   │   │
│   │   ├── repository/
│   │   │   └── JpaUserRepository.java
│   │   │
│   │   └── adapter/
│   │       └── UserRepositoryAdapter.java
│   │
│   ├── security/
│   │   ├── jwt/
│   │   │   ├── JwtTokenProvider.java
│   │   │   ├── JwtAuthenticationFilter.java
│   │   │   └── JwtAuthenticationEntryPoint.java
│   │   │
│   │   ├── UserDetailsServiceImpl.java
│   │   └── PasswordEncoderConfig.java
│   │
│   └── external/                    # Servicios externos
│       └── EmailService.java
│
├── presentation/                    # Capa de Presentación (API REST)
│   ├── controller/
│   │   ├── AuthController.java
│   │   ├── UserController.java
│   │   └── RoleController.java
│   │
│   ├── advice/
│   │   └── GlobalExceptionHandler.java
│   │
│   └── validation/
│       └── CustomValidators.java
│
└── shared/                          # Utilidades compartidas
    ├── constants/
    │   └── SecurityConstants.java
    │
    └── util/
        └── ValidationUtils.java
```
