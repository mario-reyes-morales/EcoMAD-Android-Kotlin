package com.example.mobileappdevelopment.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface FavoriteDao {
    @Query("SELECT * FROM favorites")
    suspend fun getAllFavorites(): List<StationFavorite>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(station: StationFavorite)

    @Delete
    suspend fun deleteFavorite(station: StationFavorite)

    @Query("SELECT EXISTS(SELECT * FROM favorites WHERE id = :id)")
    suspend fun isFavorite(id: Int): Boolean
}