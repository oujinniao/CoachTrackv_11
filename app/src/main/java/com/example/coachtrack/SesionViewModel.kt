package com.example.coachtrack

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SesionViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = SesionRepository(application)

    val sesiones: StateFlow<List<SesionEntity>> = repository
        .getSesiones()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun agregarSesion(sesion: SesionEntity) {
        viewModelScope.launch {
            repository.addSesion(sesion)
        }
    }

    fun getSesionesPorAlumno(alumnoId: Int): StateFlow<List<SesionEntity>> =
        repository.getSesionesPorAlumno(alumnoId)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )
}