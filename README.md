# EcoMAD

Aplicación móvil Android desarrollada en Kotlin para fomentar la movilidad sostenible en Madrid. EcoMAD combina información meteorológica, localización y disponibilidad de estaciones BiciMAD para ayudar a planificar desplazamientos en bicicleta.

El proyecto fue desarrollado por Alejandro Corona Ballester, Jorge Sanchez Campelo y Mario Reyes Morales como trabajo académico de desarrollo de aplicaciones móviles.

## Funcionalidades

- Consulta del tiempo actual a partir de la ubicación del dispositivo.
- Recomendaciones para desplazarse en bicicleta según la temperatura y las condiciones meteorológicas.
- Visualización de estaciones BiciMAD sobre un mapa interactivo.
- Consulta de bicicletas disponibles y anclajes libres en las estaciones.
- Gestión de estaciones favoritas mediante una base de datos local.
- Navegación entre el panel principal, el mapa, las estaciones BiciMAD y las estaciones favoritas.
- Inicio de sesión mediante correo electrónico o Google.
- Activación y desactivación de la localización desde la aplicación.

## Tecnologías

- Kotlin
- Android SDK
- AndroidX y Material Design
- Retrofit y Gson para el consumo de servicios web
- OpenWeather para la información meteorológica
- API de EMT Madrid para los datos de BiciMAD
- OSMDroid y OpenStreetMap para la visualización cartográfica
- Room para la persistencia local de estaciones favoritas
- Kotlin Coroutines y `lifecycleScope` para operaciones asíncronas
- Firebase Authentication y Firebase UI para la autenticación
- Glide para la carga de iconos meteorológicos
- Gradle Kotlin DSL

## Arquitectura de la aplicación

La aplicación está organizada en varias pantallas y componentes, cada uno orientado a una función concreta:

- `MainActivity`: panel principal, ubicación, información meteorológica y recomendaciones.
- `OpenStreetMapsActivity`: mapa interactivo con la ubicación del usuario y las estaciones.
- `BiciMadActivity`: consulta de estaciones y disponibilidad de bicicletas y anclajes.
- `FavoritesActivity`: gestión y consulta de las estaciones guardadas.
- Capa de servicios Retrofit: comunicación con las APIs meteorológica y de movilidad.
- Room Database: almacenamiento local de las estaciones favoritas.
- Firebase Authentication: gestión del acceso de los usuarios.

La aplicación solicita permisos de ubicación en tiempo de ejecución y utiliza la ubicación del dispositivo para actualizar la información meteorológica y representar la posición del usuario en el mapa.

## Capturas de pantalla

<img width="921" height="2048" alt="Panel principal de EcoMAD" src="https://github.com/user-attachments/assets/25cd294f-29d0-486b-a723-0fa01538c64c" />

<img width="921" height="2048" alt="Mapa de estaciones BiciMAD" src="https://github.com/user-attachments/assets/62932a1c-2e5c-4e19-be2b-48703d5388f0" />

<img width="921" height="2048" alt="Consulta de estaciones BiciMAD" src="https://github.com/user-attachments/assets/e71b293f-0476-4417-ad80-2d43879837b6" />

<img width="921" height="2048" alt="Estaciones favoritas" src="https://github.com/user-attachments/assets/4b95d6dd-6184-491b-ab2e-7775f23b1e6a" />

## Instalación

### Requisitos

- Android Studio.
- JDK 17.
- Android SDK con API 34.
- Un emulador o dispositivo Android con Android 7.0 o superior.
- Una cuenta y una API key de OpenWeather para consultar la información meteorológica.

### Configuración de OpenWeather

La clave de OpenWeather no se incluye en el repositorio. Debe configurarse localmente en un archivo `local.properties` situado en la raíz del proyecto:

```properties
OPENWEATHER_API_KEY=YOUR_OPENWEATHER_API_KEY
```

El archivo `local.properties` está excluido mediante `.gitignore` y no debe publicarse.

### Ejecución

1. Clonar el repositorio:

   ```bash
   git clone https://github.com/mario-reyes-morales/EcoMAD-Android-Kotlin.git
   ```

2. Abrir el proyecto en Android Studio.
3. Configurar la clave de OpenWeather en `local.properties`.
4. Sincronizar el proyecto con Gradle.
5. Ejecutar la aplicación en un emulador o dispositivo Android.
6. Conceder el permiso de ubicación cuando la aplicación lo solicite.

La autenticación de Firebase utiliza la configuración incluida en `app/google-services.json` para el identificador de aplicación del proyecto.

## Estructura principal

```text
app/
  src/main/
    java/com/example/mobileappdevelopment/
      MainActivity.kt
      BiciMadActivity.kt
      FavoritesActivity.kt
      OpenStreetMapsActivity.kt
      weather/
    res/
  google-services.json
  build.gradle.kts
build.gradle.kts
gradle.properties
settings.gradle.kts
```

## Equipo

- Alejandro Corona Ballester
- Jorge Sanchez Campelo
- Mario Reyes Morales

El proyecto se desarrolló de forma colaborativa, compartiendo el trabajo de análisis, diseño, implementación e integración de la aplicación.
