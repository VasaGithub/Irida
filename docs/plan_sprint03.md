# Sprint 03 – Planning Document

## 1. Sprint Goal
Transformar la aplicación Irida de un prototipo volátil a una herramienta profesional y segura. El objetivo es implementar la persistencia total de datos mediante SQLite (Room) y asegurar la privacidad del usuario a través de Firebase Authentication, garantizando que la información sobreviva al cierre de la app y que cada viajero tenga su propio espacio personal protegido. Todo ello bajo una arquitectura robusta usando Hilt y el Repository Pattern.

---

## 2. Sprint Backlog

| ID | Tarea | Responsable | Estimación (h) | Prioridad |
|----|-------|-------------|----------------|-----------|
| **T0** | **Sprint Planning & Documentation** | **Equipo** | **-** | **Alta** |
| T0.1 | Redactar plan_sprint03.md y actualizar design.md | Iker | 1.5 | Alta |
| **T1** | **Persistencia con Room (SQLite)** | **Equipo** | **-** | **Alta** |
| T1.1 | Configurar Room Database y Entities (Trip/Itinerary) | Iker | 2.0 | Alta |
| T1.2 | Crear DAOs y definir operaciones CRUD | Iker | 2.5 | Alta |
| T1.3 | Migrar ViewModels para usar Repositorios (Hilt) | Iker | 3.0 | Alta |
| **T2** | **Acceso Seguro (Login/Logout)** | **Equipo** | **-** | **Alta** |
| T2.1 | Conexión del proyecto a Firebase Console | Raúl | 1.0 | Alta |
| T2.2 | Diseño e implementación de UI Login | Raúl | 2.0 | Alta |
| T2.3 | Lógica de estado de sesión al inicio de la app | Iker | 1.5 | Alta |
| **T3** | **Gestión de Usuarios (Registro/Recuperación)** | **Equipo** | **-** | **Media** |
| T3.1 | Formulario de registro y verificación por email | Raúl | 2.5 | Media |
| T3.2 | Flujo de recuperación de contraseña | Raúl | 1.5 | Media |
| T3.3 | Implementar Repository Pattern para Auth | Iker | 2.0 | Alta |
| **T4** | **Sincronización y Perfil Local** | **Equipo** | **-** | **Alta** |
| T4.1 | Tabla de usuario local (login, birthdate, phone, etc.) | Raúl | 2.0 | Alta |
| T4.2 | Filtrado de viajes por ID de usuario logueado | Iker | 2.0 | Alta |
| T4.3 | Log de accesos (UserId + Datetime) | Raúl | 1.5 | Media |
| **T5** | **Calidad y Entrega** | **Equipo** | **-** | **Alta** |
| T5.1 | Unit Tests para DAOs y validación de datos | Iker | 3.0 | Alta |
| T5.2 | Seguimiento de errores con Logcat | Raúl | 1.0 | Media |
| T5.3 | Grabación de video demostrativo (v3.x.x) | Raúl | 1.5 | Alta |

---

## 3. Definition of Done (DoD)

- [ ] La base de datos Room gestiona correctamente el ciclo de vida de los viajes e itinerarios de forma persistente.
- [ ] El sistema de Firebase Auth permite el registro, login, logout y recuperación de contraseña funcional.
- [ ] La arquitectura utiliza obligatoriamente Hilt para inyección de dependencias y el patrón Repository.
- [ ] Los viajes son privados: un usuario solo puede ver los datos asociados a su ID.
- [ ] Se han incluido Unit Tests que validan las operaciones de la base de datos.
- [ ] El video de evidencia está guardado en /docs/evidence/v3.x.x mostrando todas las tareas.
- [ ] Release v3.x.x publicada en GitHub con el link correspondiente.

---

## 4. Riesgos identificados

- Conflictos en la Inyección de Dependencias: El uso de Hilt puede generar errores de compilación complejos si no se configuran correctamente los módulos.
- Migraciones de Base de Datos: Al modificar las entidades existentes (como añadir el ID de usuario a los viajes), la app podría fallar.
- Latencia: La dependencia de servicios externos para el login puede causar retrasos o errores en el flujo de usuario si no se implementa una gestión de errores.

---
