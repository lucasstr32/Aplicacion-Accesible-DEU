# Vista Sonidos
- Los botones que todos conserven una apariencia similar
- Faltan sonidos (Alarma, Bocina, etc)
- En editar la vista tiene que ser más organizada, el carten "Mánten presionado..." molesta y el boton de eliminar no se mezcla bien con los demás botnes.
  - Ademas de que se puede crear otra plantilla mientras se esta editando una.
  - El cartel deberia estar en una sección que no moleste y que pueda guiar al usuario.
  - No es posible cambiar el nombre de una plantilla.
- En ajustes, al cambiar de tema, la configuración de la voz se resetea.
- La sección de señas debe tener la misma estetica que los demas botones de las demas. Mantener coherencia.

# Prompt para Claude Code — Mejoras de diseño y accesibilidad (AplicacionAccesible)

> Pegá todo lo que está debajo de la línea en una sesión de Claude Code abierta en la raíz del repositorio.
> Cambiá `[...]` si querés ajustar algo. Se recomienda trabajar en una rama nueva (`git checkout -b mejoras-diseno`).

---

## Contexto

Trabajás sobre **AplicacionAccesible**, una app Android (Kotlin 2.0.21, Jetpack Compose + Material 3, Room 2.6.1 con KSP, minSdk 24, targetSdk 35) para personas sordas y personas con dificultades del habla. Tiene tres pantallas (barra inferior):

- **Sonidos**: botonera por plantillas. Cada botón reproduce un texto con TTS o un sonido pregrabado. Grilla de 20 celdas, modo edición.
- **Comunicación**: «Texto a Voz» (escribir, escuchar, guardar frases rápidas) y «Voz a Texto» (SpeechRecognizer, botón «Usar texto»).
- **Señas**: 14 imágenes de señas de LSA.

Código en `AplicacionAccesible/app/src/main/java/com/sindicato/aplicacionaccesible/`. Archivos clave: `MainActivity.kt`, `ui/components/Soundboard.kt` (~780 líneas), `ui/components/Settings.kt`, `ui/comunicacion/*`, `ui/signlanguage/*`, `ui/sound/TTSManager.kt`, `ui/theme/Color.kt` y `Theme.kt`, `viewmodel/*`, `data/*`.

Las mejoras salen de un test de usabilidad con 6 personas y de una revisión del código. **Pronto la app se va a usar en entrevistas con personas sordas y con dificultades del habla**, así que lo más importante es que **no se rompa nada que hoy funciona** y que cada cambio sea verificable.

## Reglas de trabajo

1. **Primero leé, después planificá, después editá.** En la Fase 0 no modifiques archivos: leé el código relevante y devolveme un plan (archivos a tocar, riesgos, preguntas). Esperá mi OK antes de la Fase 1.
2. **No inventes APIs ni nombres.** Si no estás seguro de que un ícono de `material-icons-extended`, una API de Room o una versión de una librería exista en las versiones del proyecto, verificalo compilando o buscalo, y decímelo.
3. **Cambios chicos y revisables.** Un commit por tarea (mensaje en español, imperativo: «Corrige la regla de contraste del texto de los botones»). No hagas `push`.
4. **No cambies el comportamiento de lo que no está en la tarea**, ni reformatees archivos enteros, ni migres todos los textos a `strings.xml`. Los textos nuevos de la UI van en `res/values/strings.xml`; los existentes se dejan como están salvo que la tarea lo pida.
5. **Textos de la UI en español con tuteo** (como hoy: «Escribe», «Puedes»). Lenguaje simple y corto.
6. **Compilá y probá al terminar cada fase:** `./gradlew assembleDebug`, `./gradlew testDebugUnitTest` y `./gradlew lint`. Si el entorno no tiene el SDK de Android y no podés compilar, decilo explícitamente y listá qué **no** pudiste verificar. No afirmes que algo funciona si no lo ejecutaste.
7. **No agregues dependencias** salvo las indicadas (DataStore). Pedime confirmación antes de agregar cualquier otra.
8. **Al terminar cada fase** entregame: (a) lista de archivos modificados y por qué, (b) resultado de build/tests/lint, (c) checklist de prueba manual en un dispositivo (incluyendo TalkBack), (d) cualquier decisión que tomaste y que yo deba revisar.

## Fases y tareas

### Fase 0 — Exploración y plan (sin editar)
Leé los archivos clave y confirmá o corregí estas afirmaciones sobre el código actual (decime las que no se cumplan):
- `getContrastColor` (en `Color.kt`) elige texto negro si `luminance() > 0.5` y blanco si no.
- En `SoundGrid`, en modo edición el toque corto no hace nada; sólo la pulsación larga abre la edición.
- Los botones se muestran al 50 % de opacidad en modo edición.
- El botón «Eliminar» de cada frase rápida mide 24 dp; los selectores de tono en `Settings.kt` miden 40 dp, no tienen texto ni descripción.
- `ComunicacionViewModel(this)` se instancia dentro de `setContent` sin fábrica, y hay una instancia sin uso.
- `AppDatabase` usa `fallbackToDestructiveMigration()` con `version = 5` y `exportSchema = false`.
- Tema, idioma, tono y velocidad no se persisten; el tema inicial es siempre `LIGHT`.
- `SoundEffectManager` se inicializa pero no se usa para reproducir (se usa `MediaPlayer`).
- La barra inferior usa `PlayArrow`, `Email` y `Menu` como íconos.
- El consejo de «Texto a Voz» habla del ícono «+», pero el botón dice «Guardar».
- El FAB de ayuda se superpone al contenido en las esquinas inferiores.

Devolveme el plan por fase y las preguntas que tengas.

### Fase 1 — Accesibilidad rápida (esfuerzo bajo, impacto alto)

**T1. Contraste del texto de los botones.**
- Reemplazá la regla del umbral 0,5 por una que calcule la relación de contraste WCAG con blanco y con negro y elija el color con **mayor** contraste. Hacela una función **pura** (no `@Composable`) para poder testearla.
- Valores esperados con los colores de `SafeColors` (relación de contraste del color elegido): Bermellón `#D55E00` → negro 5,43 · Azul `#0072B2` → blanco 5,19 · Amarillo `#F0E442` → negro 15,88 · Verde azulado `#009E73` → negro 6,14 · Naranja `#E69F00` → negro 9,32 · Púrpura rojizo `#CC79A7` → negro 6,86. Todos deben ser ≥ 4,5:1.
- Test unitario que verifique esos valores (tolerancia ±0,05) y que ningún color de `SafeColors` quede por debajo de 4,5.
- El tema daltónico dibuja un borde con el color de contenido; verificá que siga siendo coherente.

**T2. Áreas táctiles de al menos 48 dp.**
- Botón «Eliminar» de las frases rápidas (`QuickPhraseCard` en `ComunicacionScreen.kt`): área táctil de 48 dp aunque el ícono siga siendo pequeño (usá `minimumInteractiveComponentSize()` o un contenedor de 48 dp). Cuidá que no tape el texto de la frase.
- Selectores de tono en `Settings.kt`: que cada uno mida ≥ 48 dp y tenga **etiqueta accesible** («Tono muy grave», «Tono grave», «Tono normal», «Tono agudo», «Tono muy agudo») con `Role.RadioButton` y estado seleccionado (`selected` o `stateDescription`). Probá que quepan en pantallas angostas.
- Revisá los botones «+» de las celdas vacías y los íconos de la barra superior de plantillas, y corregí los que no lleguen a 48 dp.
- Agregá tests de UI de Compose que verifiquen `assertWidthIsAtLeast(48.dp)` / `assertHeightIsAtLeast(48.dp)` en los elementos tocados.

**T3. Íconos de navegación y botón de ayuda.**
- Cambiá los íconos de la barra inferior por otros que coincidan con la función: Sonidos (p. ej. volumen o grilla), Comunicación (p. ej. chat o voz), Señas (p. ej. mano). Verificá qué íconos existen en la versión de `material-icons-extended` del proyecto.
- Quitá el FAB de ayuda que tapa contenido y pasalo a una acción de la barra superior con `contentDescription` «Ayuda». Mantené el diálogo de ayuda actual.
- En el consejo de «Texto a Voz», corregí el texto para que coincida con la UI («Escribe tu mensaje y toca “Guardar” para tenerlo a mano en Frases rápidas.»).

### Fase 2 — Modo edición y claridad TTS/STT

**T4. Modo edición explícito y descubrible.**
- En modo edición: el toque corto sobre un botón **abre la edición** (la pulsación larga sigue funcionando).
- Reemplazá la opacidad 0,5 (que baja el contraste) por una señal distinta: opacidad 100 %, borde discontinuo o resaltado, y un ícono de lápiz pequeño dentro del botón como indicador (decorativo, sin descripción propia; el botón completo expone «Editar [nombre]»).
- Mostrá un cartel **fijo** en la parte superior de la grilla mientras dure el modo edición: «Modo edición. Toca un botón para modificarlo.» y mantené el botón «Aceptar» visible. Mantené el cartel flotante actual sólo si no se superpone con el nuevo.
- Que el cambio de modo se anuncie con TalkBack (`liveRegion` o anuncio al activar/desactivar).

**T5. Claridad entre «Texto a Voz» y «Voz a Texto».**
- En el selector de modo, además del texto, mostrá una **línea de ayuda del modo activo**: «Tú escribes → la app lo dice en voz alta» / «La otra persona habla → la app lo escribe».
- Que el botón principal de cada modo diga la acción de forma inequívoca («Decir en voz alta», «Empezar a escuchar») y mantené los estados actuales («Hablando…», «Detener»).
- Evitá que se guarden frases **duplicadas** (comparar sin distinguir mayúsculas y sin espacios en los extremos); si ya existe, mostrá un aviso breve («Ya la tienes guardada»).
- Obtené `ComunicacionViewModel` con `viewModel()` y una fábrica (como `SoundboardViewModel`) en lugar de instanciarlo en `setContent`, y eliminá la instancia sin uso. Descomentá/implementá `onCleared()` para destruir el `SpeechRecognizer`.
- Revisá que al volver de «Voz a Texto» a «Texto a Voz» y al rotar la pantalla no se pierdan el texto escrito ni el reconocido.

### Fase 3 — Datos, ajustes y voz

**T6. Persistencia de ajustes.**
- Agregá `androidx.datastore:datastore-preferences` (elegí una versión compatible con el proyecto y decime cuál).
- Persistí: tema, idioma, tono, velocidad, cantidad de columnas y la voz elegida (T8).
- El tema inicial debe seguir el **tema del sistema** (claro/oscuro) mientras la persona no haya elegido otro.
- Al iniciar, aplicá los valores guardados a `TTSManager` (tono, velocidad, idioma, voz) cuando el motor esté listo.

**T7. Migraciones de Room.**
- Poné `exportSchema = true` y configurá el directorio de esquemas en KSP.
- Sacá `fallbackToDestructiveMigration()`. La versión actual (5) es la línea base: usá `fallbackToDestructiveMigrationFrom(1, 2, 3, 4)` sólo para versiones de desarrollo anteriores, y escribí migraciones reales para cualquier cambio de esquema que hagas de ahora en adelante. Si no hace falta cambiar el esquema, no lo cambies.
- Agregá un test de migración si ya cambió el esquema; si no, dejá documentado en un comentario cómo agregar la próxima.

**T8. Selector de voz y variante rioplatense.**
- En Configuración, listá las voces del motor TTS para el idioma elegido (`TextToSpeech.getVoices()` filtradas por idioma, con nombre legible; evitá mostrar identificadores técnicos crudos) y permití elegir una y **escucharla** con una frase de prueba.
- Si el dispositivo tiene voces `es-AR`, ofrecé «Español (Argentina)» además de «Español (España)» (`AppLanguage`). Verificá la disponibilidad con `isLanguageAvailable` y ocultá la opción si no existe.
- Manejá el caso sin voces o con datos de voz faltantes con un mensaje claro y un botón para abrir la configuración de TTS del sistema.

### Fase 4 — Calidad y repositorio

**T9. Limpieza técnica.**
- Eliminá código comentado y campos sin uso (`tts`, `speechRecognizer` y `mediaPlayer` en `SoundManagerViewModel` si no se usan), y la dependencia `navigation-compose` si no se usa. Decidí entre usar `SoundPool` (`SoundEffectManager`) para los efectos o eliminarlo; explicame la opción elegida y por qué (latencia al tocar).
- Revisá `@RequiresApi(Build.VERSION_CODES.O)` frente a `minSdk 24` y decime si hay riesgo en Android 7.x.
- Agregá tests unitarios de: DAOs (con base en memoria), `SoundboardViewModel` (crear, editar y borrar botón/plantilla), `ComunicacionViewModel` (frases duplicadas) y la función de contraste.

**T10. Higiene del repositorio.**
- Actualizá `.gitignore` y quitá del índice `.idea/` y `.kotlin/errors/` (sin borrar archivos locales).
- Creá un `README.md` con: descripción, capturas (dejá marcadores), requisitos (Android Studio, JDK 21), cómo compilar y ejecutar, estructura del proyecto y cómo correr tests. Dejá una sección «Docker» vacía con un `TODO` (se completará según lo que responda la cátedra; **no** crees un Dockerfile por ahora).
- Agregá un archivo `LICENSE` sólo si yo te confirmo cuál; si no, dejá una nota en el README.

## Fuera de alcance (no lo hagas)
- Chat para dos personas, versión web, ni ampliar el vocabulario de señas (requiere validación con la comunidad sorda).
- Cambiar los colores de `SafeColors`, el esquema de color de los temas ni la estructura de navegación (tres destinos).
- Cambiar el modelo de datos sin migración.

## Definición de terminado
- Compila (`assembleDebug`) y pasan los tests y lint sin errores nuevos.
- Todos los colores de `SafeColors` cumplen ≥ 4,5:1 con su texto.
- Ningún objetivo táctil tocado mide menos de 48 dp.
- Con TalkBack, se puede: reproducir un botón, crear y editar un botón, cambiar de modo en «Comunicación», elegir el tono y guardar una frase, sabiendo en todo momento qué elemento está enfocado y en qué modo se está.
- Los ajustes sobreviven al cierre de la app y las plantillas sobreviven a una actualización.
- Un resumen final con: lo hecho, lo no verificado, decisiones a revisar y próximos pasos.

Empezá por la **Fase 0**.