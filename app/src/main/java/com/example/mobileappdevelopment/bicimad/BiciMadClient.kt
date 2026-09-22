package com.example.mobileappdevelopment.bicimad

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object BiciMadClient {
    private const val BASE_URL = "https://openapi.emtmadrid.es/v1/"

    val service: IBiciMadService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(IBiciMadService::class.java)
    }
}