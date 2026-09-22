package com.example.mobileappdevelopment.weather

import retrofit2.http.GET
import retrofit2.http.Query

class IWeatherService {
    interface IWeatherService {
        @GET("weather")
        suspend fun getWeather(
            @Query("lat") lat: Double,
            @Query("lon") lon: Double,
            @Query("appid") appid: String,
            @Query("units") units: String = "metric",
            @Query("lang") lang: String = "es"
        ): WeatherResponse
    }
}