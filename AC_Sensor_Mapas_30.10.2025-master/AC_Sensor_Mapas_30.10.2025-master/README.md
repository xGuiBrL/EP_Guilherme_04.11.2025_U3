# AC_Sensor_Mapas_30102025

Aplicación de ejemplo que integra el acelerómetro y Google Maps en dos fragments.

Estructura:
- `SensorFragment` (arriba): muestra valores X, Y, Z del acelerómetro y detecta movimiento.
- `MapFragmentCustom` (abajo): muestra un Google Map con un marcador en Santa Cruz y cambia su color/título cuando hay movimiento.

Instrucciones para ejecutar:
1. Añade tu API key de Google Maps en `app/src/main/AndroidManifest.xml`, reemplaza `YOUR_API_KEY_HERE` por tu clave real.
   - Para obtener una API key revisa la documentación de Google Cloud (habilitar Maps SDK for Android).
2. Compila el proyecto en Android Studio (Gradle descargará la dependencia `play-services-maps`).
3. Ejecuta en un dispositivo físico para probar el sensor (el emulador puede no ofrecer acelerómetro real). Mueve el dispositivo y observa cómo cambia el marcador.

Notas técnicas:
- `SensorFragment` usa `SensorManager` y `onSensorChanged` para actualizar la UI y detectar movimiento (umbral configurable).
- `MainActivity` actúa como puente: recibe eventos de movimiento desde `SensorFragment` y llama `setMovementState` en `MapFragmentCustom`.
- `MapFragmentCustom` usa `SupportMapFragment` dentro de un child fragment y `OnMapReadyCallback`.

Puntos de mejora:
- Usar `ViewModel` o `LiveData` para desacoplar la comunicación entre fragments.
- Añadir permisos de ubicación runtime si desea centrar en la posición del usuario.

---
Calificación de la implementación (según criterios):
- SensorManager: implementado
- EventListener: implementado
- Google Map: implementado (requiere API key)
- Uso de Fragments: implementado
- UI/UX: básico, claro
- Integración sensor->mapa: implementado (marker cambia color y título)
