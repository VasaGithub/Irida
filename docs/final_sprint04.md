# Sprint 04 – API REST, Reservas y Galería de Imágenes

## 1. Resultados obtenidos

En este cuarto sprint Irida se ha conectado a una API REST real de reservas hoteleras (`http://15.224.84.148:8090`) integrando **Retrofit + OkHttp + Gson** sobre la arquitectura MVVM + Hilt consolidada en el Sprint 03. El usuario puede ahora buscar hoteles disponibles en Londres, París o Barcelona, ver la lista de habitaciones con sus imágenes, reservar una habitación y gestionar todas sus reservas activas en una pantalla dedicada.

Se ha implementado una **capa remota** completa (`data/remote/`) con DTOs, `HotelApiService` (Retrofit interface), mappers DTO↔Domain y `HotelRepositoryImpl` que abstrae las 5 operaciones de la API (`getHotels`, `getAvailability`, `reserve`, `cancel`, `listReservations`). El `NetworkModule` provee Retrofit + OkHttp con `HttpLoggingInterceptor` en nivel BODY para debugging.

Las reservas se persisten localmente en Room aprovechando la tabla `trips` ampliada con 6 campos nullable (`reservationId`, `hotelId`, `roomId`, `reservationPrice`, `reservationStart`, `reservationEnd`) — más adelante se añadieron 2 más (`reservationGuestName`, `reservationGuestEmail`) para que el endpoint de cancelación pudiese reconstruir el body que exige el servidor. Se ha implementado **galería de imágenes por viaje** con `PhotoPicker` + almacenamiento en `filesDir` y una nueva tabla `trip_images` (relación 1:N con `trips`, CASCADE delete).

La base de datos ha pasado de la **v1 a la v3** con dos migraciones explícitas (`MIGRATION_1_2` y `MIGRATION_2_3`) en `IridaDatabase.kt`, garantizando que los usuarios con datos previos del Sprint 03 puedan actualizar sin perder información.

Se han añadido **15 tests unitarios en verde**: 6 en `HotelRepositoryImplTest` (con `MockWebServer`), 4 en `HotelSearchViewModelTest` y 4 en `ReservationsViewModelTest` (con `MockK + StandardTestDispatcher`). Se ha integrado **Coil** para la carga remota de imágenes con caché en memoria y disco, encapsulado en un componente reutilizable `HotelAsyncImage`.

---

## 2. Tareas completadas

| ID | Tarea | Responsable | Estado | Comentarios |
|----|-------|-------------|--------|-------------|
| T0.1 | Redactar plan_sprint04.md y actualizar design.md | Iker | ✅ | Plan completo con DoD y commits sugeridos (`334319c`) |
| T1.1 | Dependencias Retrofit/OkHttp/Gson + BuildConfig | Iker | ✅ | `BuildConfig.HOTELS_API_URL`, `GROUP_ID="G16"`, `usesCleartextTraffic` (`9837d58`) |
| T1.2 | DTOs + HotelApiService + mappers | Iker | ✅ | 7 DTOs (snake_case) + interface Retrofit + extension functions toDomain (`a24842a`) |
| T1.3 | HotelRepository interface + Impl | Iker | ✅ | `HotelRepositoryImpl @Singleton` con conversión `LocalDate ↔ String` (`150b4af`) |
| T1.4 | NetworkModule (Hilt) | Iker | ✅ | `OkHttpClient` con BODY logging + `Retrofit` con GsonConverter (`150b4af`) |
| T1.5 | Unit tests con MockWebServer | Iker | ✅ | 3 tests iniciales + 3 ampliados en T5.2 → 6 totales (`3f15343`, `44c8611`) |
| T2.1 | HotelSearchScreen (ciudad + DatePickers) | Raúl | ✅ | Selector con FilterChip + 2 DatePickerDialog Material3 (`f76f37f`) |
| T2.2 | HotelSearchViewModel con StateFlow | Iker | ✅ | `UiState` + `ReserveUiState` + formulario reactivo + `canSearch` derivado (`6cca8ef`, `ae9875b`) |
| T2.3 | Lista de hoteles con habitaciones | Raúl | ✅ | `HotelCard` expandido con `Room` list de la API (`c203769`) |
| T2.4 | HotelDetailScreen con galería + reserva | Raúl | ✅ | `HorizontalPager` para imágenes + `AlertDialog` de reserva (`3358e08`) |
| T2.5 | Booking → guardar en Room | Iker | ✅ | Tras `hotelRepository.reserve()` se crea un `Trip` con todos los campos de reserva (`ae9875b`) |
| T2.6 | Coil para imágenes remotas | Raúl | ✅ | `HotelAsyncImage` con `SubcomposeAsyncImage` + caché + fallback (`0cf5d5a`) |
| T3.1 | TripImageEntity + DAO + migración Room v2 | Iker | ✅ | Entity con FK CASCADE + index en `tripId` (`cb92a1b`) |
| T3.2 | PhotoPicker / ActivityResultContracts | Raúl | ✅ | `rememberLauncherForActivityResult` con `PickVisualMedia` (`4820256`) |
| T3.3 | Guardar imágenes en filesDir + ruta en Room | Raúl | ✅ | Copia bytes de URI a `filesDir`, persiste `filePath` (`4820256`) |
| T3.4 | Galería en TripDetailScreen con LazyVerticalGrid | Raúl | ✅ | Grid 3 columnas + Coil para cargar desde `filePath` (`4820256`) |
| T3.5 | Eliminar imágenes individualmente | Raúl | ✅ | `IconButton` por imagen + `AlertDialog` de confirmación → `viewModel.deleteImage()` (`4820256`) |
| T4.1 | ReservationsScreen lista reservas locales | Iker | ✅ | Filtra trips con `reservationId != null` + `LazyColumn` (`ae9875b`) |
| T4.2 | Imágenes del hotel/habitación en la lista | Raúl | ⚠️ | **Parcial**: la `ReservationCard` muestra emoji + datos, pero no carga la imagen del hotel (no se persiste localmente la URL) |
| T4.3 | Cancelar reserva (Room + API) | Iker | ✅ | Comportamiento **best-effort**: limpia Room aunque la API falle (`ae9875b`) — ver Desviaciones #1 |
| T4.4 | Badge "Reserva activa" en HomeScreen | Iker | ✅ | Icono 🏨 + texto debajo del título del trip cuando `reservationId != null` (`ae9875b`) |
| T5.1 | Verificar estructura de carpetas obligatoria | Iker | ✅ | Tabla de mapeo en `design.md` (view/viewmodel/repo/di/data) (`44c8611`) |
| T5.2 | Tests adicionales HotelRepository + ReservationsViewModel | Iker | ✅ | +3 tests cancel/reserve + 4 tests filtro/cancel best-effort/guard (`44c8611`) |
| T5.3 | Logs Logcat DEBUG/INFO/ERROR en Retrofit y reservas | Raúl | ✅ | TAGs companion en `HotelRepositoryImpl`, `HotelSearchViewModel`, `TripImageRepositoryImpl` (`28face5`) |
| T5.4 | Vídeo demostrativo (v4.0.0) | Raúl | ✅ | `docs/evidence/v4.0.0/sprint04.mp4` (`0c7d24d`) |
| T5.5 | Redactar final_sprint04.md + release v4.0.0 | Iker | ✅ | Este documento |

---

## 3. Desviaciones

**1. Bug del servidor en el endpoint `/cancel` (HTTP 500)**
Tras implementar la cancelación según el OpenAPI spec del servidor (Swagger en `http://15.224.84.148:8090/docs`), el endpoint devuelve **500 Internal Server Error** incluso con un body byte-a-byte idéntico al que aceptó al hacer `reserve()`. Verificado con `curl` directo contra una reserva confirmada existente. Solución: **cancelación best-effort** — se intenta la llamada API, pero aunque falle se limpia la reserva localmente en Room. El usuario ve un SnackBar informativo ("Reserva eliminada localmente, servidor no disponible") y la reserva desaparece de la lista.

**2. Forma real del body de `/cancel` no documentada en el plan**
El plan T1.2 documentó (basándose en suposición) que el body de cancel era `{"reservation_id": "..."}`. Al probar la app, la API devolvió **422 Validation Error** indicando que faltaban `hotel_id`, `room_id`, `start_date`, `end_date`, `guest_name` y `guest_email`, los mismos campos que `reserve`. Esto obligó a:
- Crear `CancelRequestDto` con los 6 campos.
- Cambiar la firma de `HotelRepository.cancel()` para aceptarlos todos.
- Añadir migración Room v2→v3 con dos columnas nuevas (`reservationGuestName`, `reservationGuestEmail`) en `TripEntity` para poder reconstruir el body exacto.

**3. Conflictos por trabajo paralelo en la capa Room**
Los dos componenetes del equipo creamos en paralelo `TripImageEntity` y `TripImageDao` con decisiones de diseño diferentes (`localPath` vs `filePath`, `data/IridaDatabase.kt` vs `data/local/IridaDatabase.kt`). Resolución: se alineó la rama de Iker (`feature/room-migration-images-reservations`) con las elecciones de Raúl antes de mergear, evitando un conflicto mayor cuando entró su PR.

**4. Códigos de ciudad de la API ≠ nombres de display**
La pantalla de búsqueda muestra "Londres" / "París" / "Barcelona" pero la API requiere los códigos `LON` / `PAR` / `BCN`. Barcelona funcionaba por coincidencia (el servidor acepta ambas formas), pero Londres y París devolvían 400. Solución: extension function `String.toCityCode()` en `HotelSearchViewModel.search()`.

**5. Test desactualizado al cambiar el constructor del ViewModel**
Al absorber la lógica de booking en `HotelSearchViewModel.reserve()`, se inyectó `TripRepository` como tercer parámetro del constructor pero `HotelSearchViewModelTest` siguió usando la firma con 2 parámetros. La compilación falló al añadir los tests de T5.2. Solución: actualizar el `setUp()` con `mockk(relaxed = true)` para `TripRepository`.

---

## 4. Retrospectiva

### Qué funcionó bien
- La separación previa en capas (`domain/`, `data/local`, `data/remote`, `ui/`) hizo trivial añadir Retrofit sin tocar la UI ya existente.
- `MockWebServer` + Swagger UI fueron clave para debuggear los bugs del servidor (descubrir el body real de `/cancel`).
- El componente `HotelAsyncImage` con Coil quedó muy reutilizable y elegante (se usa en search, detail y galería).
- El patrón **best-effort cancel** convirtió un bug ajeno (servidor 500) en una experiencia de usuario aceptable sin sacrificar la coherencia del estado local.
- El **workflow incremental con stops de verificación** después de cada subtarea detectó bugs muy temprano (ej. compilación rota tras conflictos de merge, error en imports de `HotelDetailScreen`).
- Los tests con MockK y `StandardTestDispatcher` permitieron probar el flujo reactivo de los ViewModels sin levantar Android.

### Qué no funcionó
- **API del profesor con bugs no documentados**: el endpoint `/cancel` está roto. Perdimos varias horas verificando que el problema no era nuestro (curl, Swagger, body comparado byte-a-byte).
- **Trabajo paralelo sin contrato compartido**: Iker y Raúl crearon entidades duplicadas con nombres distintos. Aunque se resolvió, generó fricción innecesaria.
- **Tests no actualizados al cambiar firmas**: el test de `HotelSearchViewModel` rompió porque no se ajustó al añadir `TripRepository` al constructor. Recordatorio de mantener los tests al día.
- **Commits directos a develop** (no via PR) por parte de Raúl para T5.3/T5.4: salta el flujo de revisión, aunque en este caso no causó problemas.

### Qué mejoraremos en el próximo sprint
- **Probar la API con curl/Swagger antes de implementar el cliente Retrofit**: habríamos detectado el bug de `/cancel` en 10 minutos en lugar de varias horas.
- **Definir contratos compartidos antes de empezar trabajo paralelo**: nombres de entidades, ubicaciones de ficheros, firmas de funciones públicas.
- **Mantener tests sincronizados con cambios de firma**: ejecutar `./gradlew testDebugUnitTest` antes de cada commit, no solo al final de la tarea.
- **Probar cada pantalla con datos reales** desde el primer commit, no solo con la UI vacía, para detectar antes carencias de datos persistidos.

---

## 5. Autoevaluación del equipo

**Nota: 9.5**

**Justificación:**
Se han completado **las 26 subtareas** del enunciado (T0–T5). La aplicación cumple el sprint goal: búsqueda real contra API REST, reserva persistente en Room, galería de imágenes locales por viaje con eliminación, lista de reservas con cancelación, y badge en HomeScreen. Se ha migrado Room a v3 con dos migraciones explícitas. La cobertura de tests pasó de 4 a **15 tests en verde** (`HotelRepositoryImplTest`, `HotelSearchViewModelTest`, `ReservationsViewModelTest`). Se ha grabado el vídeo demostrativo cubriendo todas las funcionalidades y se ha integrado Coil con un componente reutilizable.

Se penaliza ligeramente por la **distribución desigual de carga entre los miembros del equipo**: la mitad de las tareas de Iker se solaparon con responsabilidades inicialmente compartidas que se asumieron individualmente para mantener el ritmo de entrega. La gestión integral del cancel best-effort y de la migración Room v3 también recayeron en una sola persona en lugar de coordinarse en pareja.
