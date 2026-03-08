# Sprint 01 – Execution & Review

## 1. Resultados obtenidos

Comparación con Sprint Goal:

El objetivo del Sprint 01 era establecer las bases del proyecto e implementar
las pantallas principales de la aplicación con datos mock y navegación funcional.
El sprint se ha completado satisfactoriamente, entregando las 7 pantallas
planificadas, la navegación entre ellas y la estructura de carpetas correcta.

---

## 2. Tareas completadas

| ID | Tarea | Completada | Comentarios |
|----|-------|------------|-------------|
| T1.1 | Definición del producto y nombre | ✅ Sí | Nombre "Irida" justificado en README.md |
| T1.5 | Inicialización del proyecto Android Studio | ✅ Sí | Estructura de paquetes correcta |
| T2.2 | Repositorio Git con ramas y CONTRIBUTING.md | ✅ Sí | Feature branching implementado |
| T2.10 | Release v1.0.0 en GitHub | ✅ Sí | Publicada con todo el Sprint 01 |
| T3.1 | Pantalla Home con lista de viajes | ✅ Sí | Con mock data y navegación inferior |
| T3.2 | Pantalla Trip Detail con itinerario | ✅ Sí | Tabs: Itinerario, Galería, Presupuesto, Notas |
| T3.3 | Pantalla Trip Gallery | ✅ Sí | Grid de imágenes con filtros y confirmación de borrado |
| T3.4 | Clases del modelo de datos con @TODO | ✅ Sí | Trip, ItineraryItem, GalleryImage en domain/ |
| T4.1 | Splash Screen con logo | ✅ Sí | Animaciones de entrada y barra de carga |
| T4.2 | About Page | ✅ Sí | Equipo, versión e información técnica |
| T4.3 | Términos y Condiciones | ✅ Sí | Mostrado solo la primera vez, botones Aceptar/Rechazar |
| T4.4 | Pantalla de Preferencias (UI mock) | ✅ Sí | Idioma, moneda, tema, notificaciones |
| T5.1 | Diagrama del modelo de datos | ✅ Sí | Generado en Mermaid y exportado como PNG |
| T5.2 | Documentación design.md | ✅ Sí | Arquitectura MVVM y decisiones técnicas |
| T5.3 | Documentación color-palette.md | ✅ Sí | Paleta basada en el logo de la app |
| T5.4 | Documentación final_sprint01.md | ✅ Sí | Retrospectiva y autoevaluación |
| T5.5 | Logo generado e integrado | ✅ Sí | Visible en SplashScreen y AboutScreen |
| T5.6 | Paleta de colores personalizada | ✅ Sí | Azules extraídos del gradiente del logo |
| T5.7 | Navegación completa con NavController | ✅ Sí | 7 pantallas conectadas con NavHost |
| T5.8 | Galería integrada en TripDetail | ✅ Sí | Tab Galería con grid y borrado confirmado |
| T5.9 | Mock data refactorizada a carpeta data/ | ✅ Sí | MockTrips, MockItinerary, MockGallery separados |

---

## 3. Desviaciones

- **Estructura Git inicial incorrecta**: El proyecto Android fue inicializado
  dentro de la carpeta `app/` en lugar de la raíz del repositorio, lo que
  causaba duplicación de carpetas al crear ramas. Se resolvió eliminando la
  carpeta incorrecta y reinicializando el proyecto correctamente.

- **Mock data acoplada a las pantallas**: Inicialmente los datos mock estaban
  definidos dentro de los archivos de pantalla, lo que causó problemas de
  referencias cruzadas entre ramas. Se refactorizó creando la carpeta `data/`
  con archivos independientes.

- **Conflictos de merge**: Al trabajar en múltiples ramas simultáneamente se
  produjeron conflictos en `HomeScreen.kt` y `TripDetailScreen.kt` que se
  resolvieron manualmente.

---

## 4. Retrospectiva

### Qué funcionó bien
- El uso de Feature Branching mantuvo el código organizado y permitió trabajar
  en cada pantalla de forma independiente.
- Jetpack Compose aceleró el desarrollo de la UI considerablemente.
- La separación en capas (ui, domain, data) desde el inicio facilitará
  la integración con datos reales en sprints futuros.

### Qué no funcionó
- La gestión de ramas al principio fue confusa, con merges accidentales a
  `main` en lugar de `develop`.
- No se planificó desde el inicio la separación de mock data, lo que obligó
  a refactorizar a mitad del sprint.

### Qué mejoraremos en el próximo sprint
- Revisar siempre la rama activa antes de hacer commit.
- Planificar la estructura de datos antes de empezar a codificar las pantallas.
- Añadir más commits intermedios para evitar perder trabajo.

---

## 5. Autoevaluación del equipo (0-10)

Nota: 8

Justificación: Se han completado todas las tareas planificadas para el Sprint 01
y la app funciona correctamente con navegación entre las 7 pantallas. Se penaliza
ligeramente por los problemas iniciales con la estructura Git y la necesidad de
refactorizar la mock data a mitad del sprint.