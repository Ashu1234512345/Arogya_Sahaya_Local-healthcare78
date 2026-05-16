package com.example.arogyasahayalocal.viewmodels


import androidx.lifecycle.viewModelScope
import androidx.lifecycle.ViewModel

import com.example.arogyasahayalocal.data.entity.HealthCamp
import com.example.arogyasahayalocal.data.repository.HealthRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HealthCampViewModel(private val repository: HealthRepository) : ViewModel() {

    val campList: StateFlow<List<HealthCamp>> = repository.allCamps
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun saveCamp(camp: HealthCamp) {
        viewModelScope.launch {
            repository.saveCamp(camp)
        }
    }
}