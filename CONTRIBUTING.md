# Guía de Contribución - Irida Travel Planner

Para evitar conflictos de código y asegurar que todos los miembros del equipo puedan trabajar en paralelo sin sobrescribir el progreso de los demás, utilizaremos la estrategia de ramas **Feature Branching**.

## 1. Estructura de Ramas

* **`main`**: Es la rama principal y sagrada. Solo contiene código estable y las versiones de entrega (Releases como la v0.1.0 o v1.0.0). Nunca se programa directamente aquí.
* **`develop`**: Es la rama de integración. Aquí unimos las tareas terminadas de todos los miembros del equipo antes de pasarlas a `main`.
* **`feature/...`**: Son las ramas de trabajo individual. Cada vez que empieces una tarea del plan de sprint, crearás una de estas.

## 2. Flujo de Trabajo Paso a Paso

Sigue siempre este orden cuando vayas a programar tu parte:

1. **Actualiza tu código local:**
   Antes de empezar, asegúrate de tener lo último de tus compañeros.
   `git checkout develop`
   `git pull origin develop`

2. **Crea tu rama de trabajo:**
   Nombra la rama con la tarea que vas a hacer (ejemplo: `feature/t3-1-home-layout`).
   `git checkout -b feature/nombre-de-tu-tarea`

3. **Programa y haz commits:**
   Trabaja en Android Studio. Ve guardando tu progreso con mensajes claros en español indicando la tarea.
   `git add .`
   `git commit -m "feat: añadir botones de navegación en la pantalla Home"`

4. **Sube tu rama a GitHub:**
   Cuando termines tu tarea, súbela al repositorio remoto.
   `git push origin feature/nombre-de-tu-tarea`

5. **Crea un Pull Request (PR):**
   Ve a GitHub y abre un Pull Request para unir tu rama `feature/...` hacia la rama `develop`. Pide a un compañero que revise que no hay errores antes de aceptar el PR.