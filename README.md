# Aplicación Accesible

App Android para facilitar la comunicación de personas sordas y personas con
dificultades del habla. Tiene tres secciones, accesibles desde la barra inferior:

- **Sonidos**: botonera organizada en plantillas. Cada botón dice un texto con
  texto a voz (TTS) o reproduce un efecto de sonido. Las plantillas y botones se
  pueden crear, editar, renombrar y borrar desde el modo edición.
- **Comunicación**: «Texto a Voz» (escribes y la app lo dice en voz alta, con
  frases rápidas guardadas) y «Voz a Texto» (la otra persona habla y la app lo
  escribe).
- **Señas**: palabras de Lengua de Señas Argentina (LSA) con su imagen.

Incluye tema claro, oscuro y para daltonismo, contraste WCAG AA en los botones,
áreas táctiles de al menos 48 dp, etiquetas para TalkBack, y ajustes de voz
(idioma, voz, tono y velocidad) que se guardan entre sesiones.

## Capturas

<!-- TODO: agregar capturas -->

| Sonidos | Comunicación | Señas | Configuración |
|---|---|---|---|
| _(captura)_ | _(captura)_ | _(captura)_ | _(captura)_ |

## Requisitos

- **Android Studio** (versión compatible con Android Gradle Plugin 8.13).
- **JDK 21** (el proyecto usa `jvmToolchain(21)`).
- Android SDK con la plataforma **36** (`compileSdk`).
- Dispositivo o emulador con **Android 7.0 (API 24)** o superior.
- Para texto a voz, un motor TTS con voces instaladas (por ejemplo, Servicios
  de voz de Google).

## Compilar y ejecutar

El proyecto Android está en la carpeta `AplicacionAccesible/`.

**Con Android Studio:** abrir la carpeta `AplicacionAccesible/`, esperar la
sincronización de Gradle y ejecutar la configuración `app`.

**Desde la terminal:**

```sh
cd AplicacionAccesible
./gradlew assembleDebug      # genera app/build/outputs/apk/debug/app-debug.apk
./gradlew installDebug       # instala en el dispositivo conectado
```

## Tests

```sh
cd AplicacionAccesible
./gradlew testDebugUnitTest          # tests unitarios (JVM)
./gradlew connectedDebugAndroidTest  # tests instrumentados (requiere dispositivo o emulador)
./gradlew lint                       # análisis estático
```

- **Unitarios** (`app/src/test`): contraste WCAG de la paleta, detección de
  frases duplicadas y lectura de ajustes guardados.
- **Instrumentados** (`app/src/androidTest`): DAOs de Room y `SoundboardViewModel`
  con base de datos en memoria, y tamaño mínimo de 48 dp de los controles.

> **Atención:** `connectedDebugAndroidTest` desinstala la app al terminar, lo
> que borra sus datos (plantillas, frases y ajustes). En un teléfono con datos
> reales conviene instalar y correr los tests sin desinstalar:
>
> ```sh
> ./gradlew installDebug installDebugAndroidTest
> adb shell am instrument -w com.sindicato.aplicacionaccesible.test/androidx.test.runner.AndroidJUnitRunner
> ```

## Estructura del proyecto

```
AplicacionAccesible/
├── app/
│   ├── schemas/                      Esquemas exportados de Room (para migraciones)
│   └── src/
│       ├── main/java/com/sindicato/aplicacionaccesible/
│       │   ├── MainActivity.kt       Navegación inferior, barra superior y ayuda
│       │   ├── data/                 Room: base de datos, DAOs, entidades y repositorios
│       │   │   └── settings/         Ajustes persistentes con DataStore
│       │   ├── ui/
│       │   │   ├── components/       Sonidos (Soundboard) y Configuración (Settings)
│       │   │   ├── comunicacion/     Texto a Voz y Voz a Texto
│       │   │   ├── signlanguage/     Señas
│       │   │   ├── screens/soundgrid/ Modelos de botones, plantillas y efectos
│       │   │   ├── sound/            Motor de texto a voz y efectos de sonido
│       │   │   └── theme/            Colores, temas y contraste
│       │   └── viewmodel/            ViewModels de Sonidos y de audio
│       ├── main/res/raw/             Efectos de sonido
│       ├── test/                     Tests unitarios
│       └── androidTest/              Tests instrumentados
└── CREDITOS_SONIDOS.md               Origen y licencia de los sonidos
```

### Base de datos

La versión 5 del esquema es la línea base. Para cambiar el esquema hay que subir
la versión y escribir una migración; los pasos están documentados en
`data/AppDatabase.kt`. No se debe volver a usar `fallbackToDestructiveMigration()`,
porque borraría los datos de las personas al actualizar la app.

## Docker

<!-- TODO: completar según lo que indique la cátedra. -->

_Pendiente._

## Licencia

Todavía no se eligió una licencia para el código. Hasta que se agregue un archivo
`LICENSE`, todos los derechos quedan reservados por sus autores.

Los efectos de sonido agregados provienen de Wikimedia Commons con licencia CC0 o
de dominio público; ver [`CREDITOS_SONIDOS.md`](AplicacionAccesible/CREDITOS_SONIDOS.md).
