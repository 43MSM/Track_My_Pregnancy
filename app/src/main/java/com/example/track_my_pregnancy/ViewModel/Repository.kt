package com.example.track_my_pregnancy.ViewModel

import com.example.track_my_pregnancy.roomdb.Pregnancy
import com.example.track_my_pregnancy.roomdb.PregnancyDatabase

class Repository(private val db : PregnancyDatabase) {
    suspend fun upsertPregnancy(pregnancy: Pregnancy){
        db.dao.upsertPregnancy(pregnancy)
    }

    suspend fun deletePregnancy(pregnancy: Pregnancy){
        db.dao.deletePregnancy(pregnancy)
    }

    fun getAllPregnancy() = db.dao.getAllPregnancy()
}