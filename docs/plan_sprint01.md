# Sprint 01 – Planning Document

## 1. Sprint Goal
Establecer la infraestructura base y el andamiaje (scaffolding) de la aplicación Irida. El objetivo es configurar el entorno de desarrollo, definir una arquitectura de datos expandida, implementar la navegación entre las pantallas principales con datos de prueba (mocked data) y asegurar el cumplimiento de los estándares de control de versiones requeridos.

---

## 2. Sprint Backlog

| ID | Tarea | Responsable | Estimación (h) | Prioridad |
|----|-------|-------------|----------------|-----------|
| T0 | Sprint Planning & Documentation | Equipo | - | Alta |
| T0.1 | Crear y redactar plan_sprint01.md | Iker | 1.0 | Alta |
| T0.1.1 | Definir Sprint Goal, DoD y riesgos | Iker | 0.5 | Alta |
| T0.1.2 | Desglosar tareas y asignar responsables | Iker | 0.5 | Alta |
| T1 | Project Setup | Equipo | - | Alta |
| T1.1 | Crear nombre del producto | Raúl | 0.5 | Alta |
| T1.1.1 | Redactar justificación de originalidad | Raúl | 0.5 | Media |
| T1.2 | Generar logo de la app con IA | Raúl | 1.0 | Alta |
| T1.3 | Definir versión de Android (Min SDK) | Iker | 0.5 | Alta |
| T1.4 | Configurar versión de Kotlin |Iker | 0.5 | Alta |
| T1.5 | Inicializar proyecto en Android Studio | Iker | 1.0 | Alta |
| T1.5.1 | Configurar Package Name con nombre T1.1 | Iker | 0.5 | Alta |
| T2 | Version Control | Equipo | - | Alta |
| T2.1 | Crear repositorio GitHub (PÚBLICO) | Iker | 0.5 | Alta |
| T2.2 | Inicializar repositorio Git local | Iker | 0.5 | Alta |
| T2.3 | Añadir archivo LICENSE en raíz | Iker | 0.5 | Alta |
| T2.4 | Redactar CONTRIBUTING.md (estrategia de ramas) | Iker | 0.5 | Alta |
| T2.5 | Crear README.md (descripción y equipo) | Iker | 0.5 | Alta |
| T2.6 | Crear estructura de directorios (/docs, /app) | Iker | 0.5 | Alta |
| T2.7 | Inicializar design.md | Iker | 0.5 | Alta |
| T2.8 | Configurar ramas Git local y remoto | Iker | 0.5 | Alta |
| T2.9 | Hacer push inicial (v0.1.0) | Iker | 0.5 | Alta |
| T2.10 | Publicar Release v0.1.0 en GitHub | Iker | 0.5 | Alta |
| T3 | Core Implementation | Equipo | - | Alta |
| T3.1 | Desarrollar layouts de pantallas principales | Raul | 2.0 | Alta |
| T3.1.1 | Crear UI Home y Trip | Raul | 1.5 | Alta |
| T3.1.2 | Crear UI Itinerary y User Preference | Iker | 1.5 | Alta |
| T3.1.3 | Integrar mocked data en las vistas | Iker | 1.0 | Alta |
| T3.2 | Implementar sistema de navegación | Iker | 2.0 | Alta |
| T3.3 | Documentar modelo de datos | Iker | 1.0 | Alta |
| T3.3.1 | Expandir diagrama con Mermaid | Raul | 1.5 | Alta |
| T3.4 | Crear clases Kotlin en domain/ | Iker | 2.0 | Alta |
| T3.4.1 | Redactar lógica pendiente con @TODO | Raul | 1.0 | Alta |
| T4 | Customization | Equipo | - | Media |
| T4.1 | Crear Splash Screen con logo | Raul | 1.5 | Media |
| T4.2 | Implementar About Page | Raul | 1.0 | Media |
| T4.3 | Diseñar pantalla Terms & Conditions | Iker | 1.0 | Media |
| T4.4 | Mockear UI Preferences Screen (idiomas) | Iker | 1.5 | Media |

---

## 3. Definition of Done (DoD)

- [ ] Planificación inicial documentada en plan_sprint01.md al arrancar el sprint
- [ ] Estructura del repositorio cumple estrictamente el formato obligatorio (app/, docs/, etc.)
- [ ] Modelo de dominio expandido e implementado en carpeta domain/ con anotaciones @TODO
- [ ] Documentación obligatoria (incluyendo diagrama de Mermaid) completada en Markdow
- [ ] Release final v1.0.0 publicada en GitHub conteniendo todas las tareas

---

## 4. Riesgos identificados

- Falta de experiencia en Git y GitHub: Riesgo de sobreescribir código o generar conflictos de fusión (merge conflicts) al trabajar varios miembros del equipo simultáneamente en la rama develop
- Desconocimiento del entorno Android Studio: Riesgo de bloqueos técnicos durante la configuración inicial del proyecto, gestión del SDK, dependencias de Gradle o uso del emulador por falta de familiaridad con el IDE.
- Curva de aprendizaje con Kotlin: Dificultades técnicas y posibles retrasos al implementar la lógica del dominio y los layouts, debido a la falta de experiencia previa programando en este lenguaje.

---

⚠ Este documento no puede modificarse después del 30% del sprint.
Fecha límite modificación: 22/02/2026