package com.example.coachtrack

import android.app.Application
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProfesorViewModel(application: Application) : AndroidViewModel(application) {

    private val profesorRepository = ProfesorRepository(application)
    private val alumnoRepository = AlumnoRepository(application)

    // --- FLOWS ---
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

    // NUEVO: Flow para alumnos disponibles (sin profesor asignado)
    val alumnosDisponibles: StateFlow<List<AlumnoEntity>> = alumnos
        .map { listaAlumnos ->
            listaAlumnos.filter { it.profesorInstructor == null }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // NUEVO: Flow para obtener alumno asignado a un profesor específico
    fun getAlumnoAsignado(profesorId: Int): StateFlow<AlumnoEntity?> = alumnos
        .map { listaAlumnos ->
            listaAlumnos.find { it.profesorInstructor == profesorId }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    // --- UI STATE PARA LOS DIALOGOS ---
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

    // --- OPERACIONES CRUD SIMPLES ---
    fun agregarOActualizarProfesor(profesor: ProfesorEntity) {
        viewModelScope.launch {
            if (profesor.id != 0) {
                profesorRepository.updateProfesor(profesor)
                Log.d("ProfesorVM", "Profesor actualizado id=${profesor.id}")
            } else {
                val insertedId = profesorRepository.addProfesor(profesor)
                Log.d("ProfesorVM", "Profesor insertado id=$insertedId")
            }
        }
    }

    /**
     * Inserta o actualiza un profesor y asigna o desasigna el alumno seleccionado.
     */
    fun agregarOActualizarProfesorConAsignacion(profesor: ProfesorEntity, alumnoIdSeleccionado: Int?) {
        viewModelScope.launch {
            val finalId = if (profesor.id == 0) {
                // Insertar nuevo profesor
                val newId = profesorRepository.addProfesor(profesor)
                Log.d("ProfesorVM", "Insertado profesor id=$newId")
                newId.toInt()
            } else {
                profesorRepository.updateProfesor(profesor)
                profesor.id
            }

            // Aplicar la asignación de alumno
            asignarAlumnoAProfesor(alumnoIdSeleccionado, finalId)
        }
    }

    fun eliminarProfesor(profesor: ProfesorEntity) {
        viewModelScope.launch {
            // Desvincular alumnos asignados a este profesor
            val asignados = alumnos.value.filter { it.profesorInstructor == profesor.id }
            asignados.forEach { alumno ->
                alumnoRepository.updateAlumno(alumno.copy(profesorInstructor = null))
                Log.d("ProfesorVM", "Desasignado alumno ${alumno.id} del profesor eliminado")
            }

            profesorRepository.deleteProfesor(profesor)
            Log.d("ProfesorVM", "Profesor eliminado id=${profesor.id}")
        }
    }

    fun countAlumnosAsignados(profesorId: Int): Int {
        return alumnos.value.count { it.profesorInstructor == profesorId }
    }

    /**
     * Asignación corregida:
     * - Primero desasigna cualquier alumno que esté asignado a este profesor
     * - Luego asigna el nuevo alumno si se proporcionó
     */
    private suspend fun asignarAlumnoAProfesor(alumnoId: Int?, profesorId: Int) {
        // 1. Desasignar alumnos actualmente asignados a este profesor
        val alumnosActualmenteAsignados = alumnos.value.filter { it.profesorInstructor == profesorId }
        alumnosActualmenteAsignados.forEach { alumno ->
            alumnoRepository.updateAlumno(alumno.copy(profesorInstructor = null))
            Log.d("ProfesorVM", "Desasignado alumno ${alumno.id} del profesor $profesorId")
        }

        // 2. Asignar nuevo alumno si se proporcionó
        if (alumnoId != null) {
            val alumnoParaAsignar = alumnos.value.find { it.id == alumnoId }
            if (alumnoParaAsignar != null) {
                alumnoRepository.updateAlumno(alumnoParaAsignar.copy(profesorInstructor = profesorId))
                Log.d("ProfesorVM", "Asignado alumno ${alumnoParaAsignar.id} al profesor $profesorId")
            } else {
                Log.e("ProfesorVM", "Error: alumno $alumnoId no encontrado")
            }
        }

        Log.d("ProfesorVM", "Asignación completada - Profesor: $profesorId, Alumno: $alumnoId")
    }
}