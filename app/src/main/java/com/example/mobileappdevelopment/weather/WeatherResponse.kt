package com.example.mobileappdevelopment.weather


    data class WeatherResponse(
        val main: MainData,
        val weather: List<WeatherDescription>,
        val clouds: Clouds,
        val name: String
    )

    data class MainData(
        val temp: Double,
        val humidity: Int
    )

    data class WeatherDescription(
        val description: String,
        val icon: String
    )

    data class Clouds(
        val all: Int
    )
