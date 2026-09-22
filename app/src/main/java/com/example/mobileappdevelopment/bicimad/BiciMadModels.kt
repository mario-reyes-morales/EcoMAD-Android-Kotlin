package com.example.mobileappdevelopment.bicimad

data class LoginResponse(
    val code: String,
    val description: String,
    val data: List<LoginData>
)

data class LoginData(
    val accessToken: String
)

data class BiciMadResponse(
    val code: String,
    val data: List<Station>
)

data class Station(
    val id: Int,
    val name: String,
    val address: String,
    val free_bases: Int,
    val dock_bikes: Int,
    val total_bases: Int,
    val geometry: Geometry
)

data class Geometry(
    val coordinates: List<Double>
)