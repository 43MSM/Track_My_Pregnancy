package com.example.track_my_pregnancy.roomdb

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow


@Dao
interface RoomDao {
    @Upsert
    suspend fun upsertPregnancy( pregnancy: Pregnancy)

    @Delete
    suspend fun deletePregnancy( pregnancy: Pregnancy)

    @Query("SELECT * FROM pregnancy")
    fun getAllPregnancy(): Flow<List<Pregnancy>>
}