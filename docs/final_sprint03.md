# Sprint 03 – Persistencia, Autenticación e Inyección de Dependencias

## 1. Resultados obtenidos

En este tercer sprint se ha transformado Irida de un prototipo con datos volátiles en una aplicación con persistencia real y autenticación segura. El almacenamiento en memoria (`FakeTripDataSource`) ha sido sustituido por una base de datos **Room (SQLite)** que gestiona viajes, actividades, usuarios y log de accesos.

Se ha integrado **Firebase Authentication** con email/contraseña, incluyendo verificación de correo electrónico, recuperación de contraseña y mensajes de error traducidos al español. Toda la app se ha migrado a una arquitectura con **Hilt** como inyector de dependencias y **Repository Pattern** para separar la capa de datos del dominio.

Se ha añadido una tabla `users` en local que persiste el perfil del usuario tras el registro y una tabla `access_logs` que registra cada login y logout con su `userId` y datetime. Los viajes se filtran por `userId` para garantizar que cada usuario solo ve los suyos.

Finalmente, se han implementado **14 tests instrumentados** sobre los DAOs usando `Room.inMemoryDatabaseBuilder` y se ha actualizado la documentación de arquitectura (`design.md`) con el nuevo esquema de base de datos en Mermaid y los flujos de autenticación.

---

## 2. Tareas completadas

| ID | Tarea | Responsable | Estado | Comentarios |
|----|-------|-------------|--------|-------------|
| T0.1 | Redactar plan_sprint03.md y actualizar design.md | Iker | ✅ | Plan completo con decisiones de arquitectura y commits ordenados |
| T1.1 | Configurar Room Database y Entities (Trip/Activity) | Iker | ✅ | `IridaDatabase` + 4 entities con TypeConverters para LocalDate/LocalTime |
| T1.2 | Crear DAOs y definir operaciones CRUD | Iker | ✅ | `TripDao`, `ActivityDao`, `UserDao`, `AccessLogDao` con Flow + suspend |
| T1.3 | Migrar ViewModels para usar Repositorios (Hilt) | Iker | ✅ | `@HiltViewModel` + `@Inject` en TripList, TripDetail, Settings y Auth |
| T2.1 | Conexión del proyecto a Firebase Console | Equipo | ✅ | `google-services.json` integrado y plugin `google-services` activado (no aparece en commits, está en .gitignore) |
| T2.2 | Diseño e implementación de UI Login | Iker + Raúl | ✅ | Iker: LoginScreen inicial (`8cdf4fe`). Raúl: mejoras de validación y UX (`77f1c23`) |
| T2.3 | Lógica de estado de sesión al inicio de la app | Iker | ✅ | SplashScreen consulta `authViewModel.isLoggedIn` y redirige a Home/Login (`1d0bd49`) |
| T3.1 | Formulario de registro y verificación por email | Iker + Raúl | ✅ | Iker: RegisterScreen inicial + persistencia en UserRepository (`703655c`, `b4efb38`). Raúl: EmailVerificationScreen con reenvío y cooldown (`8632e26`) |
| T3.2 | Flujo de recuperación de contraseña | Iker + Raúl | ✅ | Iker: ForgotPasswordScreen inicial (`9614750`). Raúl: mejoras del flujo (`8632e26`) |
| T3.3 | Implementar Repository Pattern para Auth | Iker | ✅ | `AuthRepository` interface + `AuthRepositoryImpl` con `Tasks.await()` (`faa8c67`) |
| T4.1 | Tabla de usuario local (login, birthdate, phone, etc.) | Iker + Raúl | ✅ | Iker: UserEntity + UserRepositoryImpl + persistencia tras registro (`a3b7332`, `bad7f9a`, `b4efb38`). Raúl: UserProfileScreen (`9d6d899`) |
| T4.2 | Filtrado de viajes por ID de usuario logueado | Iker | ✅ | `getTripsStream(userId)` filtra por SQL + reactividad ante cambios de auth (`36f2ece`) |
| T4.3 | Log de accesos (UserId + Datetime) | Iker + Raúl | ✅ | Iker: AccessLogEntity + AccessLogDao + logging en AuthViewModel (`a3b7332`, `8cdf4fe`). Raúl: AccessLogScreen (`9d6d899`) |
| T5.1 | Unit Tests para DAOs y validación de datos | Iker | ✅ | 14 tests instrumentados (TripDao, ActivityDao, UserDao) en verde (`af2a549`) |
| T5.2 | Seguimiento de errores con Logcat | Iker | ✅ | `Log.i("AuthVM", ...)` en login, logout, register y verificación (`8cdf4fe`) |
| T5.3 | Grabación de video demostrativo (v3.0.0) | Iker | ✅ | Vídeo dividido en 3 partes en `docs/evidence/v3.0.0/` (`0936f47`, `1611c29`, `a55280a`) |

---

## 3. Desviaciones

Durante el sprint surgieron varias incidencias que requirieron ajustes:

**1. Migración de TripRepository a suspend + Flow**
La interfaz original (Sprint 02) era síncrona. Tuvimos que reescribirla a `suspend fun` y `Flow<List<Trip>>` para integrarse con Room, lo que obligó a refactorizar todos los ViewModels y los tests existentes (`TripRepositoryImplTest` se rehizo con `runTest` y un nuevo `FakeTripRepository`).

**2. Bug de suscripción en TripListViewModel**
Se detectó que al registrar un usuario nuevo, los viajes creados no aparecían en HomeScreen. La causa era que `init` se suscribía al Flow con `userId = ""` cuando el usuario aún no estaba autenticado. Se solucionó usando `callbackFlow` sobre `FirebaseAuth.AuthStateListener` y `flatMapLatest` para resuscribir el stream cada vez que cambia la sesión.

**3. Conflicto de merge en AppNavigation.kt**
La integración de las ramas `feature/auth-ui-improvements` y `feature/user-profile-access-log` produjo un conflicto en el objeto `Routes`. Cada rama añadió constantes diferentes (EMAIL_VERIFICATION en una, PROFILE/ACCESS_LOG en otra). Se resolvió manteniendo todas las constantes de ambas ramas.

**4. Mensajes de error de Firebase en inglés**
Por defecto, las excepciones de Firebase Auth devolvían mensajes técnicos en inglés ("The supplied credential is incorrect, malformed or has expired"). Se añadió una función `Throwable.toSpanishMessage()` en `AuthViewModel` que mapea las excepciones tipadas (`FirebaseAuthInvalidCredentialsException`, `FirebaseAuthInvalidUserException`, etc.) a mensajes en español.

**5. Limitación de tamaño en GitHub para el vídeo**
El vídeo demostrativo de 3:07 minutos pesaba 50MB, superando el límite de 25MB de GitHub. Se dividió el vídeo en 3 partes con cortes en 1:33 y 2:20 para que cada fragmento quedase dentro del límite.

**6. Plantillas de email de Firebase no editables**
Se intentó personalizar el cuerpo del email de verificación pero Firebase no permite editarlo (medida antispam). Solo se pudo personalizar el "From name" a "Irida Travel Planner". Para el email de recuperación sí se pudo modificar el cuerpo.

---

## 4. Retrospectiva

### Qué funcionó bien
- La separación previa en capas (`domain/`, `data/`, `ui/`) del Sprint 01 facilitó enormemente la migración a Room + Hilt sin tocar la UI.
- El uso de KSP (en lugar de KAPT) aceleró considerablemente los tiempos de compilación con Hilt y Room.
- El Repository Pattern permitió escribir tests con `FakeTripRepository` sin necesidad de levantar la base de datos en los unit tests.
- Los tests instrumentados con `Room.inMemoryDatabaseBuilder` validaron toda la capa de persistencia de forma rápida y aislada.
- Las plantillas de email personalizadas en Firebase mejoran la experiencia del usuario final.

### Qué no funcionó
- La planificación inicial subestimó el tiempo de configuración de Firebase y Hilt: hubo varios fallos de compilación por orden incorrecto de los plugins en `build.gradle.kts`.
- El bug de la suscripción reactiva al Flow se detectó muy tarde (durante la grabación del vídeo), lo que obligó a un fix urgente en una rama nueva.
- Trabajar en ramas largas (`feature/auth-ui-improvements`, `feature/user-profile-access-log`) generó conflictos al integrarlas en develop.

### Qué mejoraremos en el próximo sprint
- Hacer integraciones más frecuentes (rebases sobre develop) para reducir el tamaño de los conflictos.
- Probar el flujo completo de cada feature en el emulador antes de cerrar la rama, no solo al grabar el vídeo final.
- Documentar las decisiones de arquitectura en el plan ANTES de empezar a codificar (commits ordenados, no improvisados).

---

## 5. Autoevaluación del equipo

**Nota: 9**

**Justificación:**
Se han completado las 5 tareas principales del enunciado (T1-T5) con todas sus subtareas. La aplicación cumple los requisitos de persistencia con Room, autenticación con Firebase, inyección con Hilt, filtrado por usuario, log de accesos y tests instrumentados. Se ha actualizado `design.md` con el nuevo esquema de base de datos y se ha grabado el vídeo demostrativo cubriendo todas las funcionalidades.

Se penaliza ligeramente por:
- El bug de suscripción al Flow detectado tarde (requirió un fix de última hora).
- La gestión de ramas con conflictos al merge (mejorable con integraciones más frecuentes).
- No haber separado más el trabajo entre los miembros del equipo desde el inicio (la mayoría de la capa de persistencia recayó en una sola persona).
