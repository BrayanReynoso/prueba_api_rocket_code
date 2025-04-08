# API de Gestión de Biblioteca

## Descripción

Esta API REST permite gestionar los préstamos de libros en una biblioteca. Está construida con **Spring Boot** (Java 8) y se conecta a una base de datos **Oracle**. La API ofrece operaciones CRUD para administrar usuarios, libros y préstamos. Además, al registrar un préstamo a un estudiante, la API envía automáticamente una notificación por correo electrónico con los detalles del préstamo.

## Arquitectura

La API sigue una arquitectura **cliente-servidor**, donde el servidor proporciona los recursos y servicios que el cliente (frontend) consume a través de peticiones HTTP. La API está diseñada bajo los principios de **REST**.

## Dependencias

El proyecto usa las siguientes dependencias:

- **Spring Boot**: Framework principal utilizado para el desarrollo del backend.
  - `spring-boot-starter-web` - Para crear la API REST.
  - `springdoc-openapi-ui` - Para generar la documentación de la API.
  - `spring-boot-starter-mail` - Para el envío de correos electrónicos de notificación.
  - `spring-boot-starter-thymeleaf` - Para plantillas de correo HTML.
  
- **MyBatis**: Framework ORM para interactuar con la base de datos.
  - `mybatis-spring-boot-starter` - Configuración inicial de MyBatis con Spring Boot.

- **Oracle JDBC Driver**: Para la conexión con la base de datos **Oracle**.
  - `ojdbc8` - El driver JDBC para Oracle.

- **Lombok**: Librería para reducir el código repetitivo, como los getters, setters, constructores, etc.
  - `lombok` - Para facilitar el desarrollo.

## Endpoints

### **BookController**

- **GET** `/all` - Obtener todos los libros
- **GET** `/available` - Obtener solo libros disponibles
- **GET** `/search/title` - Buscar libros por título
- **GET** `/search/author` - Buscar libros por autor
- **GET** `/{id}` - Obtener un libro por ID
- **POST** `/` - Crear un nuevo libro
- **PUT** `/{id}` - Actualizar un libro
- **PUT** `/{id}/availability` - Cambiar la disponibilidad de un libro
- **DELETE** `/{id}` - Marcar un libro como no disponible (soft delete)
- **PUT** `/{id}/stock` - Actualizar el stock de un libro

### **LoanController**

- **GET** `/all` - Obtener todos los préstamos
- **GET** `/{id}` - Obtener préstamo por ID
- **GET** `/usuario/{nombre}` - Obtener préstamos por nombre de usuario
- **POST** `/register` - Registrar un nuevo préstamo
- **PUT** `/{id}` - Actualizar un préstamo
- **PATCH** `/{id}/devolver` - Registrar la devolución de un libro
- **PATCH** `/{id}/cancelar` - Cancelar un préstamo
- **DELETE** `/{id}` - Eliminar préstamo

### **StudentController**

- **GET** `/all` - Obtener todos los estudiantes
- **GET** `/get-by-matricula/{matricula}` - Obtener estudiante por matrícula
- **GET** `/get-by-email/{email}` - Obtener estudiante por correo electrónico
- **POST** `/register` - Registrar un estudiante
- **PUT** `/update/{id}` - Actualizar un estudiante
- **DELETE** `/delete/{id}` - Eliminar un estudiante

