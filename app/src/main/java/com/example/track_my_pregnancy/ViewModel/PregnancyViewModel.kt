package com.example.track_my_pregnancy.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.track_my_pregnancy.roomdb.Pregnancy
import kotlinx.coroutines.launch

class PregnancyViewModel(private val repository: Repository): ViewModel() {

    // Upsert operation
    fun upsertPregnancy(pregnancy: Pregnancy) {
        viewModelScope.launch {
            repository.upsertPregnancy(pregnancy)
        }
    }

    // Delete operation
    fun deletePregnancy(pregnancy: Pregnancy) {
        viewModelScope.launch {
            repository.deletePregnancy(pregnancy)
        }
    }

    // Fetching data as LiveData
    fun getAllPregnancy() = repository.getAllPregnancy().asLiveData()
}
