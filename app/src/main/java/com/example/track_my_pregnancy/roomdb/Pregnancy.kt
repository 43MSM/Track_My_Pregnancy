package com.example.track_my_pregnancy.roomdb

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class Pregnancy(
    val sysBP : String,
    val diaBP : String,
    val weight: String,
    val babyKicks: String,


    @PrimaryKey(autoGenerate = true)
    val id :Int = 0,

    val timestamp: Long = System.currentTimeMillis()
)
