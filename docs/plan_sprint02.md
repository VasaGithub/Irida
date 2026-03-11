# 📋 Plan Sprint 02 – Irida Travel Planner
**Release objetivo:** v2.0.0  
**Equipo:** Iker Vázquez (IV) · Raul Rubio (RR)

---

## 🎯 Objetivo del Sprint

Implementar la lógica principal de la aplicación Travel Planner: operaciones CRUD en memoria para viajes y actividades, validación de datos, persistencia de preferencias con SharedPreferences, soporte multiidioma y tests unitarios. Todo siguiendo la arquitectura MVVM con la cadena UI → ViewModel → Repository → DataSource.

---

## 🏗️ Arquitectura objetivo

```
UI (Screens)
    ↓
ViewModel (TripListViewModel, TripDetailViewModel)
    ↓
TripRepository (interface en domain/)
    ↓
TripRepositoryImpl (en data/repository/)
    ↓
FakeTripDataSource (en data/fakeDB/)
```

---

## 📁 Estructura de carpetas objetivo

```
app/src/main/java/com/travelplanner/irida/
├── data/
│   ├── fakeDB/
│   │   └── FakeTripDataSource.kt        ← almacenamiento inMemory
│   └── repository/
│       └── TripRepositoryImpl.kt        ← implementación del repositorio
├── domain/
│   ├── Trip.kt                          ← modelo actualizado (LocalDate)
│   ├── Activity.kt                      ← modelo actividad (LocalDate, LocalTime)
│   ├── TripRepository.kt                ← interfaz del repositorio
│   └── GalleryImage.kt                  ← sin cambios
├── ui/
│   ├── screens/
│   │   ├── HomeScreen.kt                ← lista de viajes dinámica
│   │   ├── AddTripScreen.kt             ← nuevo: formulario añadir viaje
│   │   ├── EditTripScreen.kt            ← nuevo: formulario editar viaje
│   │   ├── TripDetailScreen.kt          ← refactorizado: lista actividades
│   │   ├── AddActivityScreen.kt         ← nuevo: formulario añadir actividad
│   │   ├── EditActivityScreen.kt        ← nuevo: formulario editar actividad
│   │   ├── PreferencesScreen.kt         ← con SharedPreferences real
│   │   ├── SplashScreen.kt              ← sin cambios
│   │   ├── AboutPageScreen.kt           ← sin cambios
│   │   └── TermsAndConditionsScreen.kt  ← sin cambios
│   ├── viewmodels/
│   │   ├── TripListViewModel.kt         ← nuevo
│   │   └── TripDetailViewModel.kt       ← nuevo
│   ├── theme/
│   │   ├── Color.kt
│   │   ├── Theme.kt
│   │   └── Type.kt
│   └── AppNavigation.kt                 ← rutas nuevas
docs/
├── plan_sprint02.md                     ← este archivo
├── final_sprint02.md                    ← al finalizar
├── evidence/
│   └── v2.0.0/                          ← vídeo demo aquí
└── ...
```

---

## ✅ Tareas del Sprint

### T1 – Lógica de gestión de viajes (4 pts)

| ID | Tarea | Responsable |
|----|-------|-------------|
| T1.1 | Refactorizar modelo `Trip.kt` con `startDate: LocalDate`, `endDate: LocalDate`, `description: String` | IV |
| T1.2 | Crear modelo `Activity.kt` con `title`, `description`, `date: LocalDate`, `time: LocalTime` | IV |
| T1.3 | Crear interfaz `TripRepository.kt` en domain/ con métodos CRUD para trips y activities | IV |
| T1.4 | Implementar `FakeTripDataSource.kt` con lista mutable y dataset de ejemplo | IV |
| T1.5 | Implementar `TripRepositoryImpl.kt` delegando en FakeTripDataSource | IV |
| T1.6 | Implementar `TripListViewModel.kt` con addTrip, editTrip, deleteTrip y StateFlow | IV |
| T1.7 | Implementar `TripDetailViewModel.kt` con addActivity, updateActivity, deleteActivity | IV |
| T1.8 | Implementar validación en ViewModels (fechas futuras, startDate < endDate, actividad dentro del rango) | IV |
| T1.9 | Implementar SharedPreferences en PreferencesScreen (username, fecha nacimiento, dark mode, idioma) | IV |
| T1.10 | Implementar multiidioma ES / EN / CA con strings.xml por locale | RR |

### T2 – Flujo de itinerario (3 pts)

| ID | Tarea | Responsable |
|----|-------|-------------|
| T2.1 | Crear `AddTripScreen.kt` con formulario y DatePickers | IV |
| T2.2 | Crear `EditTripScreen.kt` prellenado con datos del viaje seleccionado | IV |
| T2.3 | Refactorizar `HomeScreen.kt` para consumir ViewModel y mostrar lista dinámica | IV |
| T2.4 | Refactorizar `TripDetailScreen.kt` para mostrar actividades del ViewModel | IV |
| T2.5 | Crear `AddActivityScreen.kt` con DatePicker y TimePicker | IV |
| T2.6 | Crear `EditActivityScreen.kt` prellenado con datos de la actividad | IV |
| T2.7 | Actualizar `AppNavigation.kt` con todas las rutas nuevas y paso de parámetros | IV |
| T2.8 | Asegurar que los cambios se reflejan dinámicamente en HomeScreen y TripDetail | IV |

### T3 – Validación, tests y documentación (3 pts)

| ID | Tarea | Responsable |
|----|-------|-------------|
| T3.1 | Mostrar mensajes de error claros en UI cuando: campo vacío, fecha incorrecta, actividad fuera de rango | IV |
| T3.2 | Escribir tests unitarios para CRUD de trips en `TripRepositoryImplTest.kt` | IV |
| T3.3 | Escribir tests unitarios para CRUD de actividades | IV |
| T3.4 | Añadir logs Logcat con niveles DEBUG/INFO/ERROR en todas las operaciones CRUD | IV |
| T3.5 | Grabar vídeo de demostración y guardar en `docs/evidence/v2.0.0/` | RR |
| T3.6 | Redactar `final_sprint02.md` con resultados de tests y fixes aplicados | IV |

---

## 🔄 Cambios respecto al Sprint 01

| Elemento | Sprint 01 | Sprint 02 |
|----------|-----------|-----------|
| `Trip.startDate` | `String` ("Mar 10") | `LocalDate` |
| `Trip.endDate` | `String` ("Mar 18") | `LocalDate` |
| `ItineraryItem` | time: String, sin date | `Activity` con `LocalDate` + `LocalTime` |
| Datos | Mock estáticos | InMemory mutable con ViewModel |
| Preferencias | Solo UI (mock) | SharedPreferences persistente |
| Idioma | Solo ES | ES + EN + CA |
| Tests | Solo ExampleUnitTest | Tests reales de CRUD |
| Arquitectura | UI → Mock data | UI → ViewModel → Repository → DataSource |

---

## ⚠️ Puntos críticos a no olvidar

1. **DatePicker obligatorio** – No se puede escribir fechas a mano en ningún campo de fecha.
2. **Validación en ViewModel Y en UI** – Doble capa: el ViewModel valida y expone errores, la UI los muestra.
3. **SharedPreferences** – Las preferencias deben cargarse al iniciar la app, no solo al abrir la pantalla.
4. **ViewModel sobrevive rotaciones** – Usar `viewModel()` de Compose, no instanciar a mano.
5. **Actividades dentro del rango del viaje** – Validar al añadir/editar actividad.
6. **Logs en todas las operaciones** – Usar `Log.d`, `Log.i`, `Log.e` según corresponda.

---

## 🗓️ Orden de implementación recomendado

1. Modelos de dominio (`Trip`, `Activity`, `TripRepository`)
2. DataSource + RepositoryImpl
3. ViewModels con lógica y validación
4. Pantallas de viajes (HomeScreen refactorizado, AddTrip, EditTrip)
5. Pantallas de actividades (TripDetail refactorizado, AddActivity, EditActivity)
6. SharedPreferences en PreferencesScreen
7. Multiidioma (strings.xml EN/CA/ES)
8. Tests unitarios
9. Logs y comentarios
10. Vídeo + documentación final
