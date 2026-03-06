# FarmaPresence — Sistema de Control de Asistencia Biométrico

Sistema web para la gestión de asistencia de empleados en una droguería, desarrollado con Angular en el frontend y Spring Boot en el backend. Incluye registro por huella dactilar mediante un lector biométrico conectado por puerto serial (Arduino), así como registro manual y generación de reportes en PDF y Excel.

---

## ¿Qué hace esta aplicación?

La aplicación permite a los administradores de una farmacia llevar un control detallado de la asistencia de sus empleados. Las funciones principales son:

- **Registro de asistencia** de forma manual o automática por lectura de huella dactilar
- **Gestión de empleados**: registro, edición y desactivación
- **Asignación de horarios** con soporte para doble turno (mañana y tarde)
- **Programación de turnos** por fecha para cada empleado
- **Evaluación automática de puntualidad**: el sistema compara la hora de entrada con el horario asignado y determina si el empleado llegó a tiempo o tarde
- **Reportes de asistencia** filtrables por nombre, fecha, mes, año y estado (Presente, Tarde, Ausente)
- **Exportación de reportes** en formato PDF y Excel
- **Gráficas de asistencia**: distribución por estado, asistencias por mes y comparativa por empleado
- **Gestión de usuarios** del sistema con autenticación, roles (ADMIN / USER) y recuperación de contraseña por correo

---

## Tecnologías utilizadas

### Frontend
- **Angular 18** — framework principal
- **TypeScript**
- **Chart.js** — gráficas de asistencia
- **jsPDF + jspdf-autotable** — exportación PDF desde el frontend
- **SheetJS (xlsx)** — exportación a Excel
- **Bootstrap 5** + Bootstrap Icons

### Backend
- **Java 17 + Spring Boot**
- **Spring Data JPA / Hibernate**
- **Spring Security** (con BCrypt para contraseñas)
- **iTextPDF** — generación de reportes PDF en el servidor
- **JFreeChart** — generación de gráficas embebidas en PDF
- **JavaMailSender** — envío de correos para recuperación de contraseña
- **jSerialComm** — comunicación serial con el lector biométrico (Arduino)

### Base de datos
- **MySQL** (instancia en Clever Cloud)

### Despliegue
- **Frontend:** Vercel
- **Backend:** Render

---

## Arquitectura general

```
Frontend (Angular)  <-->  REST API (Spring Boot)  <-->  MySQL
                                  |
                          Lector biométrico
                          (Arduino por COM)
```

El lector biométrico envía el ID de la huella por puerto serial al servicio `SerialReaderService`, que llama internamente al endpoint `/asistencia/huella/entrada/{huella}` para registrar la asistencia de forma automática.

---

## Estructura del proyecto

```
/
├── frontend/         # Aplicación Angular
│   └── src/app/
│       ├── components/
│       │   ├── PRINCIPAL/       # Login e inicio
│       │   ├── header/
│       │   ├── registro-asistencia/
│       │   ├── registros-Empleados/
│       │   ├── reportes/
│       │   ├── turno-programado/
│       │   └── usuario/
│       └── services/            # Servicios HTTP
│
└── backend/          # API Spring Boot
    └── src/main/java/Package/PHARMACY_PROJECT/
        ├── Controllers/
        ├── Models/
        ├── Repository/
        └── Services/
```

---

## Endpoints principales

| Método | Ruta | Descripción |
|--------|------|-------------|
| POST | `/usuario/login` | Autenticación |
| POST | `/auth/forgot-password` | Envío de token de recuperación |
| POST | `/auth/reset-password` | Restablecimiento de contraseña |
| GET | `/empleado/all` | Listar empleados |
| POST | `/empleado/registrar/{huella}` | Completar registro de empleado |
| GET | `/horarios/all` | Listar horarios disponibles |
| POST | `/asistencia/manual/registrarIngreso` | Registrar asistencia manual |
| POST | `/asistencia/huella/entrada/{huella}` | Registrar asistencia por huella |
| GET | `/asistencia/todas` | Obtener todas las asistencias |
| GET | `/informe-asistencia/reporteMensual/pdf/{mes}/{ano}` | Descargar PDF mensual |
| GET | `/informe-asistencia/reporteEmpleado/pdf/{empleadoId}/{mes}/{ano}` | PDF por empleado |
| POST | `/turnoProgramado/asignar` | Asignar turno a empleado |

---

## Cómo correr el proyecto localmente

### Requisitos
- Node.js 18+
- Java 17+
- Maven
- MySQL (o conexión a una instancia remota)

### Frontend

```bash
cd frontend
npm install
ng serve
```

Disponible en `http://localhost:4200`

### Backend

```bash
cd backend
mvn spring-boot:run
```

Disponible en `http://localhost:8080`

> **Nota:** Las credenciales de la base de datos están en `application.properties`. Para correrlo localmente necesitas configurar tu propia instancia de MySQL o usar las credenciales del entorno de producción (no incluidas en este repositorio por seguridad).

---

## Funcionalidades destacadas

### Lógica de puntualidad
Cada empleado tiene un horario con hasta dos turnos diarios (`ENTRADA_1`, `ENTRADA_2`). Al registrar asistencia, el sistema calcula la diferencia entre la hora real de entrada y la hora de inicio del turno correspondiente. Si la diferencia supera 5 minutos, el estado se registra como **Tarde**.

### Recuperación de contraseña
El flujo usa tokens UUID enviados al correo del usuario. El backend genera el token, lo guarda en base de datos y envía un enlace HTML al correo. Al hacer clic, el usuario puede cambiar su contraseña desde la ruta `/reset-password`.

### Generación de reportes PDF
Los reportes se generan en el backend con iTextPDF e incluyen tablas con estadísticas por empleado: total de tardanzas, puntualidades y diferencia de tiempo acumulada.

---

## Estado del proyecto

Proyecto funcional desarrollado como sistema real para una droguería. Actualmente desplegado en producción. Algunas mejoras pendientes:

- [ ] Implementar JWT para autenticación stateless
- [ ] Agregar paginación en listados
- [ ] Mejorar manejo de errores en el frontend
- [ ] Tests unitarios en el frontend

---

## Autor
Auder Gonzalez Martinez
Desarrollado como proyecto académico/profesional.  
Si tienes preguntas o quieres ver una demo, puedes contactarme.