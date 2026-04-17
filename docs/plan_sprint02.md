# Sprint 02 – Planning Document

## 1. Sprint Goal
Implementar la lógica principal de la aplicación Irida Travel Planner. El objetivo es desarrollar las operaciones CRUD en memoria para viajes y actividades siguiendo la arquitectura MVVM (UI → ViewModel → Repository → DataSource), implementar validación de datos con DatePickers, persistir las preferencias de usuario con SharedPreferences, añadir soporte multiidioma (ES/EN/CA) y garantizar la corrección del sistema mediante tests unitarios y logs en Logcat.

---

## 2. Sprint Backlog

| ID | Tarea | Responsable | Estimación (h) | Prioridad |
|----|-------|-------------|----------------|-----------|
| T0 | Sprint Planning & Documentation | Equipo | - | Alta |
| T0.1 | Crear y redactar plan_sprint02.md | Iker | 1.0 | Alta |
| T0.1.1 | Definir Sprint Goal, DoD y riesgos | Iker | 0.5 | Alta |
| T0.1.2 | Desglosar tareas y asignar responsables | Iker | 0.5 | Alta |
| T1 | Travel Management Logic | Equipo | - | Alta |
| T1.1 | Refactorizar modelo Trip.kt (startDate/endDate: LocalDate, description: String) | Iker | 1.0 | Alta |
| T1.2 | Crear modelo Activity.kt (title, description, date: LocalDate, time: LocalTime) | Iker | 0.5 | Alta |
| T1.3 | Crear interfaz TripRepository.kt en domain/ con métodos CRUD de trips y activities | Iker | 1.0 | Alta |
| T1.4 | Implementar FakeTripDataSource.kt con lista mutable y fake dataset precargado | Iker | 1.0 | Alta |
| T1.5 | Implementar TripRepositoryImpl.kt delegando en FakeTripDataSource | Iker | 1.0 | Alta |
| T1.6 | Implementar TripListViewModel.kt con addTrip, editTrip, deleteTrip y StateFlow | Iker | 2.0 | Alta |
| T1.7 | Implementar TripDetailViewModel.kt con addActivity, updateActivity, deleteActivity | Iker | 2.0 | Alta |
| T1.8 | Implementar validación en ViewModels (fechas futuras, startDate < endDate, actividad en rango) | Iker | 1.5 | Alta |
| T1.9 | Implementar SharedPreferences en PreferencesScreen (username, fecha nacimiento, dark mode, idioma) | Iker | 1.5 | Alta |
| T1.10 | Implementar multiidioma ES / EN / CA con strings.xml por locale | Raúl | 2.0 | Alta |
| T2 | Itinerary Flow | Equipo | - | Alta |
| T2.1 | Crear AddTripScreen.kt con formulario completo y DatePickers | Iker | 2.0 | Alta |
| T2.2 | Crear EditTripScreen.kt prellenado con datos del viaje seleccionado | Iker | 1.5 | Alta |
| T2.3 | Refactorizar HomeScreen.kt para consumir TripListViewModel y mostrar lista dinámica | Iker | 1.5 | Alta |
| T2.4 | Refactorizar TripDetailScreen.kt para mostrar actividades desde TripDetailViewModel | Iker | 1.5 | Alta |
| T2.5 | Crear AddActivityScreen.kt con DatePicker y TimePicker | Iker | 2.0 | Alta |
| T2.6 | Crear EditActivityScreen.kt prellenado con datos de la actividad seleccionada | Iker | 1.5 | Alta |
| T2.7 | Actualizar AppNavigation.kt con rutas nuevas y paso de parámetros (tripId, activityId) | Iker | 1.5 | Alta |
| T2.8 | Verificar que cambios se reflejan dinámicamente en HomeScreen y TripDetailScreen | Iker | 1.0 | Alta |
| T3 | Validación, Tests y Documentación | Equipo | - | Alta |
| T3.1 | Mostrar mensajes de error en UI: campo vacío, fecha incorrecta, actividad fuera de rango | Iker | 1.5 | Alta |
| T3.2 | Escribir tests unitarios para CRUD de trips (TripRepositoryImplTest.kt) | Iker | 2.0 | Alta |
| T3.3 | Escribir tests unitarios para CRUD de actividades | Iker | 2.0 | Alta |
| T3.4 | Añadir logs Logcat DEBUG/INFO/ERROR en todas las operaciones CRUD y validaciones | Iker | 1.0 | Alta |
| T3.5 | Grabar vídeo de demostración y guardar en docs/evidence/v2.0.0/ | Raúl | 1.0 | Alta |
| T3.6 | Redactar final_sprint02.md con resultados de tests y fixes aplicados | Iker | 1.0 | Media |

---

## 3. Definition of Done (DoD)

- [ ] Planificación inicial documentada en plan_sprint02.md al arrancar el sprint
- [ ] Arquitectura MVVM respetada en todas las pantallas: UI → ViewModel → Repository → DataSource
- [ ] CRUD de trips y actividades funcional con datos en memoria (inMemory)
- [ ] Validación implementada en ViewModel y UI con mensajes de error visibles
- [ ] DatePickers obligatorios en todos los campos de fecha (sin texto libre)
- [ ] SharedPreferences persistiendo y cargando preferencias al iniciar la app
- [ ] Soporte multiidioma en ES, EN y CA
- [ ] Tests unitarios cubriendo las operaciones principales de CRUD
- [ ] Logs visibles en Logcat para operaciones CRUD, validaciones y errores
- [ ] Release final v2.0.0 publicada en GitHub con vídeo de demostración en docs/evidence/v2.0.0/

---

## 4. Riesgos identificados

- Migración del modelo de datos: Al cambiar `startDate` y `endDate` de `String` a `LocalDate`, pueden aparecer errores de compilación en cascada en pantallas y mock data que usan los modelos del Sprint 01. Requiere refactorización cuidadosa.
- Complejidad de la navegación con parámetros: Pasar `tripId` y `activityId` entre pantallas con NavController puede generar bugs difíciles de depurar si no se gestiona correctamente el estado del ViewModel compartido.
- Integración de DatePicker en Compose: El componente `DatePickerDialog` de Material3 tiene una API verbose y puede requerir tiempo adicional para implementarlo correctamente en múltiples pantallas.

---
