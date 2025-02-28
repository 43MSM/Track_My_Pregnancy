package com.example.track_my_pregnancy.roomdb

import androidx.room.Database
import androidx.room.RoomDatabase


@Database (
    entities = [Pregnancy::class],
    version = 1
)
abstract class PregnancyDatabase:RoomDatabase() {
    abstract val dao:RoomDao
}