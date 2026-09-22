# MAD Project

This is a mobile application developed in **Kotlin** for the *Mobile App Development* course at **ETSISI (UPM)**. The project focuses on promoting sustainable mobility in Madrid by facilitating the use of the **BiciMad** electric bike service.

## Team Members
* **Alejandro Corona Ballester**
* **Mario Reyes Morales**

# EcoMAD (BiciMAD & Weather Companion)

A native Android application designed to promote sustainable mobility in Madrid. The app combines real-time weather data with the availability of BiciMAD stations, offering personalized recommendations to help reduce the user's carbon footprint.

## Main Features

* **Eco Dashboard:** A home screen that displays current weather using geolocation and offers smart recommendations (e.g., rain/ice warnings, heat alerts, or encouraging messages to ride a bike on nice days).
* **Interactive Map:** Integration with OpenStreetMap (OSMDroid) to visualize your real-time location and explore all BiciMAD stations across the city.
* **Real-Time Availability:** A complete list of stations connected to the EMT API, showing available bikes and free docks.
* **Favorite Stations (Offline):** A system to save your most-used stations using a local database. Just long-press a station to save it and quickly check it from your personal favorites dashboard.
* **Authentication:** Secure login integrated with Firebase Authentication (Google & Email).

## Technologies & Architecture

This project was developed in **Kotlin**, following modern Android development best practices:

* **Network Architecture:** `Retrofit2` + `Gson` for consuming the OpenWeather and EMT Madrid APIs.
* **Asynchrony:** `Coroutines` (`lifecycleScope`) to handle network and database operations without blocking the main UI thread.
* **Local Database:** `Room Database` implementing the DAO pattern for local data persistence (favorites).
* **Geolocation:** Native `LocationManager` with runtime permission handling.
* **Maps:** `OSMDroid` for rendering the map and markers without relying on Google Play Services.
* **UI/UX:** Native `RecyclerView`s, `BottomNavigationView` with Material Design icons, and asynchronous image loading with `Glide`.
* **Backend as a Service:** `Firebase Auth` and `Firebase UI`.

## Screenshots

<img width="921" height="2048" alt="WhatsApp Image 2026-04-18 at 17 33 40" src="https://github.com/user-attachments/assets/25cd294f-29d0-486b-a723-0fa01538c64c" />
<img width="921" height="2048" alt="WhatsApp Image 2026-04-18 at 17 33 40(1)" src="https://github.com/user-attachments/assets/62932a1c-2e5c-4e19-be2b-48703d5388f0" />
<img width="921" height="2048" alt="WhatsApp Image 2026-04-18 at 17 33 40(3)" src="https://github.com/user-attachments/assets/e71b293f-0476-4417-ad80-2d43879837b6" />
<img width="921" height="2048" alt="WhatsApp Image 2026-04-18 at 17 33 40(2)" src="https://github.com/user-attachments/assets/4b95d6dd-6184-491b-ab2e-7775f23b1e6a" />



https://github.com/user-attachments/assets/e1b3911f-eea1-4a6b-84e2-84cc00990a44



## Installation & Testing

To compile and test this project in your local environment:

1. Clone this repository: `https://github.com/CJandoB/MobileAppDevelopment.git`
2. Open the project in **Android Studio**.
3. Build and run on an emulator or physical device (Android 8.0+ recommended).

---
**Developed by Alejandro Corona & Mario Reyes**
