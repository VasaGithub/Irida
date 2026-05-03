# Sprint 03 – Asignación de Tareas

Este documento lista la asignación de tareas del Sprint 03 entre los miembros del equipo, basada en los commits realizados en el repositorio.

## Equipo

- **Iker Vázquez**
- **Raúl Rubio**

---

## Tareas por miembro

### Iker Vázquez

| Tarea | Commits relacionados |
|-------|----------------------|
| T0.1 — Redactar plan_sprint03.md | `5cfedd0` Add Sprint 03 planning document |
| T1.1 / T1.2 — Configuración inicial Hilt + Firebase + Room (Database, Entities, DAOs, Modules) | `a3b7332`, `f0c1684` Implementar la configuración inicial para la integración de Hilt y Firebase con nuevos módulos y entidades |
| T3.3 — AuthRepositoryImpl con Firebase Auth | `faa8c67` Implementar AuthRepositoryImpl para la gestión de autenticación con Firebase |
| T4.1 (backend) — UserRepositoryImpl + bindings de Auth/User en RepositoryModule | `bad7f9a` Implementar UserRepositoryImpl y configurar los binds |
| T1.3 — Migrar SettingsViewModel a Hilt | `42c4741` Implementar Hilt en SettingsViewModel y eliminar SettingsViewModelFactory |
| T2.2 / T2.3 — AuthViewModel + LoginScreen + log de accesos | `8cdf4fe` Implementar AuthViewModel y LoginScreen |
| T3.1 — RegisterScreen inicial | `703655c` Implementar la pantalla de registro de usuarios (RegisterScreen) |
| T3.2 — ForgotPasswordScreen | `9614750` Implementar la pantalla de recuperación de contraseña |
| T1.3 (UI) — hiltViewModel en PreferencesScreen | `3ba94b0` Implementar hiltViewModel en PreferencesScreen |
| T2.3 / T2.4 — Flujo completo de auth + integración hiltViewModel + logout | `1d0bd49` Implementar flujo de autenticación, integración con hiltViewModel y funcionalidad de cierre de sesión |
| T4.1 — Persistencia de username en registro vía UserRepository | `b4efb38` Actualizar el proceso de registro para incluir el nombre de usuario |
| T5.1 — Tests instrumentados de TripDao, UserDao y ActivityDao | `af2a549` Implementar pruebas instrumentadas para TripDao, UserDao y ActivityDao |
| T4.3 / T5.4 — Actualizar design.md con esquema de DB y flujos de auth | `647ae10` Actualizar documento de diseño arquitectónico |
| Setup — Añadir .claude/ a .gitignore | `26beb4f` Add .claude/ directories to .gitignore |
| Integración — Merge de PR #16 (feature/setup-hilt-firebase) | `5ef746e` Merge pull request #16 |
| Integración — Merge de feature/auth-ui-improvements | `f41b83d` |
| Integración — Merge de feature/user-profile-access-log | `58a6554` |
| Mejora — Manejo de errores de auth en español | `c500df6` Mejorar manejo de errores de autenticación con mensajes en español |
| Fix — Resuscripción del Flow al cambiar de usuario | `36f2ece` fix(viewmodel): resubscribe trips stream on Firebase auth state change |
| T5.3 — Carpeta de evidencias y subida del vídeo | `0936f47`, `1611c29`, `a55280a`, `9cb0258` |
| Versionado — Actualizar versión del SplashScreen a v3.0.0 | `583f3b8` feat: update splash version to v3.0.0 · Sprint 03 in all language resources |
| Documentación — Redactar final_sprint03.md y sprint03.md | (este sprint) |

### Raúl Rubio

| Tarea | Commits relacionados |
|-------|----------------------|
| T2.2 / T3.1 — Mejorar pantallas de autenticación con validaciones y UX | `77f1c23` feat: mejorar pantallas de autenticación con validaciones y UX |
| UX — Mostrar nombre e iniciales del usuario autenticado en HomeScreen | `92aab92` feat: mostrar nombre e iniciales del usuario autenticado en HomeScreen |
| T3.1 / T3.2 — Flujo completo de verificación de email y recuperación de contraseña (EmailVerificationScreen, mejoras en ForgotPassword) | `8632e26` feat: flujo completo de verificación de email y recuperación de contraseña |
| T4.1 / T4.3 — UserProfileScreen y AccessLogScreen | `9d6d899` feat: perfil de usuario local y log de accesos |

---

## Tareas conjuntas / fuera del repositorio

| Tarea | Descripción |
|-------|-------------|
| T2.1 — Conexión a Firebase Console | Creación del proyecto, descarga de google-services.json y habilitación de Email/Password (no aparece en commits porque google-services.json está en .gitignore) |
| Personalización de plantillas de email en Firebase Console | Configuración del nombre del remitente "Irida Travel Planner" y traducción al español del email de recuperación de contraseña |
| Testing manual end-to-end | Pruebas en emulador del flujo registro → verificación → login → CRUD viajes → logout |
| Release v3.0.0 | Tag y publicación en GitHub |

---

## Resumen de contribución

- **Iker**: Arquitectura completa (Hilt + Room + Firebase Auth), todos los repositorios y ViewModels, primera versión de las pantallas de auth (Login, Register, ForgotPassword), tests instrumentados, documentación de arquitectura, integración de ramas y fixes críticos.
- **Raúl**: Mejoras de UX y validaciones sobre las pantallas de autenticación, EmailVerificationScreen completa, mejoras en HomeScreen para mostrar el usuario autenticado, y las pantallas adicionales de UserProfile y AccessLog.
