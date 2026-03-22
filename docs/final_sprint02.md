# Sprint 02 - Lógica de Gestión y Flujo de Itinerarios

## 1. Resultados obtenidos

En este segundo sprint se ha implementado la lógica principal de la aplicación Irida, permitiendo a los usuarios planificar y modificar viajes de forma funcional.

Se ha adoptado una arquitectura **MVVM**, separando claramente la interfaz de usuario (UI), los ViewModels y un repositorio encargado de la gestión de datos en memoria (`FakeTripDataSource`). Esta decisión permite desacoplar la lógica de negocio de la persistencia, facilitando futuras ampliaciones hacia bases de datos reales.

Además, se ha incorporado persistencia de preferencias mediante `SharedPreferences`, garantizando que configuraciones como el modo oscuro o el tamaño de texto se mantengan entre sesiones.

En cuanto a la integridad de los datos, se han implementado validaciones estrictas tanto en la UI como en los ViewModels, utilizando `DatePicker` nativos para evitar entradas inválidas y asegurando coherencia en rangos de fechas.

Finalmente, se ha integrado un sistema de logging (`Log.d`, `Log.i`, `Log.e`) que permite monitorizar el estado interno de la aplicación y facilitar la detección de errores.

---

## 2. Tareas completadas

| ID | Tarea | Estado | Comentarios |
|----|-------|--------|------------|
| T1.1 | CRUD de viajes en memoria | ✅ | Implementado en `FakeTripDataSource` |
| T1.2 | CRUD de actividades | ✅ | Asociadas a cada viaje |
| T1.3 | Validación de datos | ✅ | Control de fechas y campos obligatorios |
| T1.4 | Configuración de usuario | ✅ | Persistencia con `SharedPreferences` |
| T1.5 | Multi-idioma | ✅ | Soporte para es, ca, en |
| T2.1 | Flujo de navegación | ✅ | Menú → Viajes → Detalle → Actividades |
| T2.2 | Formularios dinámicos | ✅ | Creación y edición diferenciadas |
| T2.3 | Actualización reactiva | ✅ | Uso de `StateFlow` |
| T3.1 | Validación de entrada | ✅ | Mensajes de error claros |
| T3.2 | Tests unitarios | ✅ | CRUD del repositorio |
| T3.3 | Simulación y testing manual | ✅ | Casos límite verificados |
| T3.4 | Documentación | ✅ | README actualizado |
| T3.5 | Logging | ✅ | Integrado en ViewModels y repositorio |

---

## 3. Desviaciones

Durante el sprint surgieron dos incidencias relevantes:

**1. Datos mock vs repositorio en memoria**  
Inicialmente coexistían datos estáticos (`mockTrips`) con el nuevo repositorio en memoria, generando inconsistencias. Se resolvió eliminando el código obsoleto y centralizando la inicialización de datos en el DataSource (`seedFakeData`).

**2. Error en la grabación con ADB**  
Se produjo un fallo al grabar la demo debido a un problema con rutas que contenían espacios en el sistema. En lugar de invertir tiempo en depurar una herramienta externa no crítica, se optó por una solución alternativa utilizando la grabación nativa del sistema operativo, garantizando el resultado sin afectar al entorno de desarrollo.

---

## 3. Desviaciones

Durante el desarrollo y cierre de este sprint surgieron varias incidencias relevantes que requirieron nuestra atención:

**1. Integración de datos iniciales**  
Los datos de prueba no estaban correctamente integrados con la arquitectura MVVM, lo que generaba inconsistencias entre distintas partes de la aplicación. Se solucionó centralizando la inicialización en `FakeTripDataSource`, garantizando que toda la app acceda a una única fuente de datos coherente.

**2. Sincronización en Git**  
Se produjeron conflictos al integrar cambios entre miembros del equipo debido a desincronización en el repositorio. Se resolvió mediante `pull` y fusión manual de ramas, estableciendo como mejora futura una integración más frecuente para evitar conflictos grandes.

**3. Fallos en las traducciones (strings.xml)**  
Se detectó que algunos textos estaban definidos directamente en el código, impidiendo el correcto funcionamiento del sistema multi-idioma. Se refactorizó la aplicación para mover todos los textos a archivos `strings.xml`, asegurando la correcta localización en todos los idiomas soportados.---

## 4. Retrospectiva

### Qué funcionó bien
- La arquitectura MVVM junto con `StateFlow` ha permitido una UI reactiva y consistente.
- La separación de responsabilidades ha evitado estados inválidos en los datos.

### Qué no funcionó
- Se acumuló código obsoleto tras refactorizaciones, generando warnings y confusión.

### Qué mejoraremos
- Integración continua más frecuente para evitar conflictos de código.
- Eliminación inmediata de código no utilizado tras cada refactorización.
- Mantener el proyecto libre de warnings de compilación.

---

## 5. Autoevaluación del equipo

**Nota: 9.5**

**Justificación:**  
Se han cumplido todos los requisitos del sprint, incluyendo la implementación completa del CRUD en memoria, validaciones robustas y una arquitectura bien estructurada.

Además, se ha mejorado la experiencia de usuario mediante navegación fluida y actualización reactiva de datos.
