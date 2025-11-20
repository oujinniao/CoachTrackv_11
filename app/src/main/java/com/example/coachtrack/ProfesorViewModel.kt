package com.example.coachtrack

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.collections.filter
import kotlin.collections.find

class ProfesorViewModel(application: Application) : AndroidViewModel(application) {

    private val profesorRepository = ProfesorRepository(application)
    private val alumnoRepository = AlumnoRepository(application)

    val profesores: StateFlow<List<ProfesorEntity>> = profesorRepository
        .getProfesores()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val alumnos: StateFlow<List<AlumnoEntity>> = alumnoRepository
        .getAlumnos()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // UI state: exponemos MutableState para que Compose pueda observarlos desde la UI.
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

    // DB operations (existentes)
    fun agregarOActualizarProfesor(profesor: ProfesorEntity) {
        viewModelScope.launch {
            if (profesor.id != 0) {
                profesorRepository.updateProfesor(profesor)
            } else {
                profesorRepository.addProfesor(profesor)
            }
        }
    }

    /**
     * Inserta o actualiza profesor y, si se pasa alumnoId, asigna al alumno a ese profesor.
     */
    fun agregarOActualizarProfesorConAsignacion(profesor: ProfesorEntity, alumnoIdSeleccionado: Int?) {
        viewModelScope.launch {
            if (profesor.id == 0) {
                // insert + assign (transaccional en repo)
                val newId = profesorRepository.addProfesorAndAssignAlumno(profesor, alumnoIdSeleccionado)
                // StateFlow/Room refrescará automáticamente la lista
            } else {
                // update profesor
                profesorRepository.updateProfesor(profesor)
                // asignar/desasignar alumno si procede
                asignarAlumnoAProfesor(alumnoIdSeleccionado, profesor.id)
            }
        }
    }

    fun eliminarProfesor(profesor: ProfesorEntity) {
        viewModelScope.launch {
            // Desasigna alumnos que apuntan a este profesor
            val alumnosAsignados = alumnos.value.filter { it.profesorInstructor == profesor.id }
            alumnosAsignados.forEach { alumno ->
                alumnoRepository.updateAlumno(alumno.copy(profesorInstructor = null))
            }
            profesorRepository.deleteProfesor(profesor)
        }
    }

    fun countAlumnosAsignados(profesorId: Int): Int {
        return alumnos.value.count { it.profesorInstructor == profesorId }
    }

    /**
     * Asigna (o desasigna si alumnoId == null) un alumno al profesor especificado.
     */
    fun asignarAlumnoAProfesor(alumnoId: Int?, profesorId: Int) {
        viewModelScope.launch {
            val previamenteAsignados = alumnos.value.filter { it.profesorInstructor == profesorId }
            Log.d("ProfesorVM", "previosAsignados=${previamenteAsignados.map { it.id }}")

            previamenteAsignados.forEach { alumno ->
                alumnoRepository.updateAlumno(alumno.copy(profesorInstructor = null))
            }

            if (alumnoId != null) {
                val alumno = alumnos.value.find { it.id == alumnoId }
                if (alumno != null) {
                    alumnoRepository.updateAlumno(alumno.copy(profesorInstructor = profesorId))
                    Log.d("ProfesorVM", "asignado alumno ${alumno.id} -> prof $profesorId")
                } else {
                    Log.d("ProfesorVM", "alumno a asignar no encontrado en memoria: $alumnoId")
                }
            }
            Log.d("ProfesorVM", "asignarAlumnoAProfesor end")
        }
    }
        }
