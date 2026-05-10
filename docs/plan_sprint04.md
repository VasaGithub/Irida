# Sprint 04 – Planning Document

## 1. Sprint Goal
Convertir Irida en una aplicación conectada al mundo real integrando un servicio remoto de reservas hoteleras mediante Retrofit. El objetivo es permitir al usuario buscar hoteles en Londres, París o Barcelona, reservar habitaciones, gestionar sus reservas (listar y cancelar) y enriquecer cada viaje con una galería de imágenes locales. Todo ello respetando la arquitectura MVVM + Repository ya consolidada en el Sprint 03, manteniendo Hilt como inyector de dependencias y Room como capa de persistencia local.

---

## 2. Sprint Backlog

| ID | Tarea | Responsable | Estimación (h) | Prioridad |
|----|-------|-------------|----------------|-----------|
| **T0** | **Sprint Planning & Documentation** | **Equipo** | **-** | **Alta** |
| T0.1 | Redactar plan_sprint04.md y actualizar design.md | Iker | 1.5 | Alta |
| **T1** | **Retrofit Configuration (3 pts)** | **Equipo** | **-** | **Alta** |
| T1.1 | Añadir dependencias Retrofit/OkHttp/Gson y configurar el HTTP client (BaseUrl http://15.224.84.148:8090) | Iker | 1.0 | Alta |
| T1.2 | Crear data models (Hotel, Room, Availability, Reservation) e interfaces de API (HotelApiService) bajo MVVM | Iker | 2.0 | Alta |
| T1.3 | Crear capa Repository (HotelRepository) que abstrae el acceso a la API y expone resultados a los ViewModels | Iker | 2.0 | Alta |
| T1.4 | Configurar módulo Hilt (NetworkModule) que provee Retrofit, OkHttp y el ApiService | Iker | 1.0 | Alta |
| T1.5 | Unit tests mockeando la conexión remota (MockWebServer / Mockito) | Iker | 2.5 | Alta |
| **T2** | **Search and Booking Screens (5 pts)** | **Equipo** | **-** | **Alta** |
| T2.1 | Crear HotelSearchScreen con selector de ciudad (Londres/París/Barcelona) y DatePickers de fechas inicio/fin | Raúl | 2.5 | Alta |
| T2.2 | Implementar HotelSearchViewModel que consume el endpoint de availability y expone StateFlow | Iker | 2.0 | Alta |
| T2.3 | Mostrar la lista de hoteles y sus habitaciones (≈3 por hotel) con datos devueltos por la API | Raúl | 2.0 | Alta |
| T2.4 | Crear HotelDetailScreen con galería de imágenes del hotel y habitaciones, y botón "Reservar" | Raúl | 2.5 | Alta |
| T2.5 | Implementar lógica de booking: guardar reserva (id, room, hotel, precio, fechas) en Room creando un nuevo Trip vinculado | Iker | 2.5 | Alta |
| T2.6 | Integrar carga remota de imágenes con Coil en search y booking | Raúl | 1.5 | Alta |
| **T3** | **Image Gallery per Trip (4 pts)** | **Equipo** | **-** | **Alta** |
| T3.1 | Crear entidad TripImage y DAO en Room (relación 1:N con Trip) | Iker | 1.5 | Alta |
| T3.2 | Implementar selector de imágenes con PhotoPicker / ActivityResultContracts | Raúl | 2.0 | Alta |
| T3.3 | Guardar imágenes localmente (filesDir + ruta en Room) | Raúl | 2.0 | Alta |
| T3.4 | Mostrar galería específica del viaje en TripDetailScreen con LazyVerticalGrid | Raúl | 2.0 | Alta |
| T3.5 | Permitir eliminar imágenes individualmente de la galería | Iker | 1.0 | Media |
| **T4** | **List and Cancel Reservations (3 pts)** | **Equipo** | **-** | **Alta** |
| T4.1 | Crear ReservationsScreen que lista todas las reservas locales indicando el viaje asociado | Iker | 2.0 | Alta |
| T4.2 | Mostrar imágenes del hotel y habitación en cada item de la lista | Raúl | 1.5 | Alta |
| T4.3 | Implementar cancelación de reserva: borrado local en Room y llamada a la API si procede | Iker | 2.0 | Alta |
| T4.4 | Actualizar HomeScreen ("My Trips") para indicar si un viaje incluye reserva y mostrar sus detalles | Iker | 1.5 | Alta |
| **T5** | **Calidad y Entrega** | **Equipo** | **-** | **Alta** |
| T5.1 | Verificar estructura de carpetas obligatoria (view, viewmodel, repo, di, data) | Iker | 0.5 | Alta |
| T5.2 | Tests unitarios adicionales para HotelRepository y ReservationViewModel | Iker | 2.0 | Alta |
| T5.3 | Logs Logcat DEBUG/INFO/ERROR en llamadas Retrofit y operaciones de reserva | Raúl | 1.0 | Media |
| T5.4 | Grabación de video demostrativo (v4.x.x) en docs/evidence/v4.x.x | Raúl | 1.5 | Alta |
| T5.5 | Redactar final_sprint04.md con resultados y release v4.0.0 en GitHub | Iker | 1.0 | Alta |

---

## 3. Definition of Done (DoD)

- [ ] Retrofit configurado correctamente y conectado al endpoint http://15.224.84.148:8090 mediante Hilt.
- [ ] La búsqueda de hoteles funciona para Londres, París y Barcelona usando DatePickers para las fechas.
- [ ] La pantalla de detalle muestra todas las imágenes del hotel y de las habitaciones devueltas por la API.
- [ ] El usuario puede reservar una habitación y la reserva se guarda localmente en Room creando un nuevo Trip.
- [ ] Existe una galería de imágenes locales por viaje, persistente entre sesiones.
- [ ] La pantalla de reservas lista todas las reservas locales con sus imágenes y permite cancelarlas.
- [ ] La pantalla "My Trips" indica visualmente qué viajes incluyen una reserva hotelera.
- [ ] Se respeta la arquitectura View – ViewModel – Repository – DB y la estructura de carpetas obligatoria (view, viewmodel, repo, di, data).
- [ ] Hilt es el único mecanismo de DI y Room la única capa de persistencia local.
- [ ] Tests unitarios mockeando la API cubren las operaciones principales del repositorio.
- [ ] Video demostrativo guardado en /docs/evidence/v4.x.x mostrando todas las tareas.
- [ ] Release v4.0.0 publicada en GitHub con el tag correspondiente y nombre de proyecto correcto en Android Studio.

---

## 4. Riesgos identificados

- Disponibilidad de la API remota: el servidor http://15.224.84.148:8090 es externo y puede sufrir caídas o latencia, bloqueando pruebas y demos. Mitigación: usar MockWebServer en tests y manejar timeouts y errores de red en la UI.
- Gestión de imágenes locales: guardar y cargar múltiples imágenes desde almacenamiento interno puede provocar errores de permisos, rutas inválidas u OutOfMemory si no se redimensionan. Mitigación: usar Coil con caché y filesDir como ruta segura sin permisos extra.
- Sincronización local-remoto: la cancelación de reservas debe coordinarse entre la API y Room sin dejar estados inconsistentes (p. ej. reserva borrada localmente pero viva en el servidor). Mitigación: realizar primero la llamada remota y, solo si tiene éxito, eliminar el registro local.
- Mapeo de modelos API ↔ Room: divergencias entre los DTOs de la API (Hotel, Room, Reservation) y las entidades locales (Trip, ReservationEntity) pueden generar bugs sutiles. Mitigación: capa de mappers explícita y tests unitarios sobre las conversiones.

---
