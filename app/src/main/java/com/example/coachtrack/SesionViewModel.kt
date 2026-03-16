package com.example.coachtrack

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.coachtrack.data.repository.AlumnoRepositoryHibrido
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SesionViewModel(
    application: Application,
    private val repository: SesionRepository
) : AndroidViewModel(application) {

    private val alumnoRepository: AlumnoRepositoryHibrido =
        (application as CoachTrackApplication).container.alumnoRepositoryHibrido

    private val _alumnoId = MutableStateFlow(0L)

    val sesionesDelAlumno: StateFlow<List<SesionEntity>> = _alumnoId
        .flatMapLatest { id ->
            if (id == 0L) flowOf(emptyList())
            else repository.getSesionesPorAlumno(id)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun cargarSesiones(alumnoLocalId: Long) {
        _alumnoId.value = alumnoLocalId
    }

    val sesionesGenerales: StateFlow<List<SesionEntity>> = repository
        .getSesiones()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _sesionActual = mutableStateListOf<Plantilla>()
    val sesionActual: List<Plantilla> get() = _sesionActual

    fun agregarPlantilla(plantilla: Plantilla) {
        if (!_sesionActual.contains(plantilla)) _sesionActual.add(plantilla)
    }

    fun quitarPlantilla(plantilla: Plantilla) {
        _sesionActual.remove(plantilla)
    }

    fun limpiarSesionActual() {
        _sesionActual.clear()
    }

    fun agregarSesion(sesion: SesionEntity) {
        viewModelScope.launch {
            repository.addSesion(sesion)
        }
    }

    fun guardarNuevaSesion(sesion: SesionEntity) {
        viewModelScope.launch {
            repository.addSesion(sesion)
            alumnoRepository.incrementarClasesCursadas(sesion.alumnoId)
        }
    }
    // Agregar dentro de SesionViewModel
    suspend fun generarNuevoSessionId(): Long {
        val sesionesActuales = sesionesGenerales.first()
        return (sesionesActuales.maxOfOrNull { it.id } ?: 0L) + 1L
    }


}
