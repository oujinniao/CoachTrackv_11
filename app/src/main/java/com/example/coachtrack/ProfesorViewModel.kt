package com.example.coachtrack

import android.app.Application
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProfesorViewModel(application: Application) : AndroidViewModel(application) {

    // ----------------------------------------------------
    // 🔹 Service Locator (correcto)
    // ----------------------------------------------------
    private val container = (application as CoachTrackApplication).container
    private val profesorRepository = container.profesorRepository
    private val alumnoRepository = container.alumnoRepositoryHibrido

    // ----------------------------------------------------
    // 🔹 UI STATE (NECESARIO PARA GestionProfesoresScreen)
    // ----------------------------------------------------
    val mostrarDialogoAgregar = mutableStateOf(false)
    val profesorEnEdicion = mutableStateOf<ProfesorEntity?>(null)

    fun abrirDialogoAgregar(profesor: ProfesorEntity? = null) {
        profesorEnEdicion.value = profesor
        mostrarDialogoAgregar.value = true
    }

    fun cerrarDialogoAgregar() {
        profesorEnEdicion.value = null
        mostrarDialogoAgregar.value = false
    }

    // ----------------------------------------------------
    // 🔹 FLOWS (Room = fuente de verdad)
    // ----------------------------------------------------
    val profesores: StateFlow<List<ProfesorEntity>> =
        profesorRepository.getProfesores()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )

    val alumnos: StateFlow<List<AlumnoEntity>> =
        alumnoRepository.obtenerAlumnosDelProfesor()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )

    val alumnosDisponibles: StateFlow<List<AlumnoEntity>> =
        alumnos
            .map { lista -> lista.filter { it.profesorInstructor == null } }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )

    // ----------------------------------------------------
    // 🔹 OPERACIONES CRUD
    // ----------------------------------------------------
    fun agregarOActualizarProfesor(profesor: ProfesorEntity) {
        viewModelScope.launch {
            profesorRepository.guardarProfesor(profesor)
            Log.d("ProfesorVM", "Profesor guardado/actualizado")
        }
    }

    fun agregarOActualizarProfesorConAsignacion(
        profesor: ProfesorEntity,
        alumnoLocalIdSeleccionado: Long?
    ) {
        viewModelScope.launch {
            val savedProfesor = profesorRepository.guardarProfesor(profesor)
            val profesorId = savedProfesor.id

            // 1️⃣ Desasignar alumnos actuales
            alumnos.value
                .filter { it.profesorInstructor == profesorId }
                .forEach {
                    alumnoRepository.guardarAlumno(
                        it.copy(profesorInstructor = null)
                    )
                }

            // 2️⃣ Asignar nuevo alumno
            if (alumnoLocalIdSeleccionado != null) {
                val alumno = alumnos.value.find { it.localId == alumnoLocalIdSeleccionado }
                alumno?.let {
                    alumnoRepository.guardarAlumno(
                        it.copy(profesorInstructor = profesorId)
                    )
                }
            }
        }
    }

    fun eliminarProfesor(profesor: ProfesorEntity) {
        viewModelScope.launch {
            profesorRepository.deleteProfesor(profesor)
        }
    }

    fun countAlumnosAsignados(profesorId: Long): Int =
        alumnos.value.count { it.profesorInstructor == profesorId }
}
