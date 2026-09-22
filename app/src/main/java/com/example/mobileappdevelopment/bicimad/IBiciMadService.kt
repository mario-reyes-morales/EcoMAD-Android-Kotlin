package com.example.mobileappdevelopment.bicimad

import retrofit2.http.GET
import retrofit2.http.Header

interface IBiciMadService {

    // 1. Corregido el nombre del header a "passKey"
    @GET("mobilitylabs/user/login/")
    suspend fun login(
        @Header("X-ClientId") clientId: String,
        @Header("passKey") passKey: String
    ): LoginResponse

    // 2. Añadido el Accept (la EMT es muy estricta con esto a veces)
    @GET("transport/bicimad/stations/")
    suspend fun getStations(
        @Header("accessToken") token: String,
        @Header("Accept") accept: String = "application/json"
    ): BiciMadResponse
}