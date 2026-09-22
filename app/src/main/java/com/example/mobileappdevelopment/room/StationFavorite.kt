package com.example.mobileappdevelopment.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")
data class StationFavorite(
    @PrimaryKey val id: Int,
    val name: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val dockBikes: Int,
    val freeBases: Int
)