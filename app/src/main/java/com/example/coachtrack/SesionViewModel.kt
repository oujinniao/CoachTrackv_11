package com.example.coachtrack

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel que maneja tanto las sesiones guardadas en Room
 * como la sesión temporal que se está planificando.
 */
class SesionViewModel(application: Application) : AndroidViewModel(application) {

    // Repositorio de acceso a Room
    private val repository = SesionRepository(application)

    // 🔹 Sesiones almacenadas en Room (historial general)
    val sesiones: StateFlow<List<SesionEntity>> = repository
        .getSesiones()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // 🔹 Sesión temporal (las plantillas agregadas con “+” en Planificación)
    private val _sesionActual = mutableStateListOf<Plantilla>()
    val sesionActual: List<Plantilla> get() = _sesionActual

    /**
     * Agrega una plantilla a la sesión actual si aún no está incluida.
     */
    fun agregarPlantilla(plantilla: Plantilla) {
        if (!_sesionActual.contains(plantilla)) {
            _sesionActual.add(plantilla)
        }
    }

    /**
     * Quita una plantilla de la sesión temporal.
     */
    fun quitarPlantilla(plantilla: Plantilla) {
        _sesionActual.remove(plantilla)
    }

    /**
     * Limpia la sesión actual después de guardarla o cancelar.
     */
    fun limpiarSesionActual() {
        _sesionActual.clear()
    }

    /**
     * Guarda una sesión definitiva en la base de datos Room.
     */
    fun agregarSesion(sesion: SesionEntity) {
        viewModelScope.launch {
            repository.addSesion(sesion)
        }
    }

    /**
     * Obtiene las sesiones asociadas a un alumno específico.
     */
    fun getSesionesPorAlumno(alumnoId: Int): StateFlow<List<SesionEntity>> =
        repository.getSesionesPorAlumno(alumnoId)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )
}
