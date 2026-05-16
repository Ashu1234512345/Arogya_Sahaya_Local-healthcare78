package com.example.arogyasahayalocal.viewmodels


import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.arogyasahayalocal.data.repository.VitalRepository
import kotlinx.coroutines.launch
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
//import com.example.arogyasahayalocal.data.repository.VitalRepository
import com.example.arogyasahayalocal.data.entity.VitalLog
import kotlinx.coroutines.launch

class VitalViewModel(private val repository: VitalRepository) : ViewModel() {

    val latestVitals = repository.latestVitals.asLiveData()

    fun addVitalEntry(systolic: Int, diastolic: Int, sugarLevel: Int) {
        viewModelScope.launch {
             repository.saveVital(systolic, diastolic, sugarLevel)
        }
    }
}