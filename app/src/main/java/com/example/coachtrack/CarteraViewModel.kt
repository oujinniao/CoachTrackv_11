package com.example.coachtrack

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CarteraViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AlumnoRepository(application)

    val alumnos: StateFlow<List<AlumnoEntity>> = repository
        .getAlumnos()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // --------------------------
    // 🔹 Estados de la interfaz
    // --------------------------
    var mostrarDialogoAgregar by mutableStateOf(false)
        private set

    var alumnoEnEdicion by mutableStateOf<AlumnoEntity?>(null)
        private set

    fun abrirDialogoAgregar(alumno: AlumnoEntity? = null) {
        alumnoEnEdicion = alumno
        mostrarDialogoAgregar = true
    }

    fun cerrarDialogoAgregar() {
        alumnoEnEdicion = null
        mostrarDialogoAgregar = false
    }

    // --------------------------
    // 🔹 CRUD
    // --------------------------
    fun agregarOActualizarAlumno(alumno: AlumnoEntity) {
        viewModelScope.launch {
            if (alumno.id != 0) repository.updateAlumno(alumno)
            else repository.addAlumno(alumno)
        }
    }

    fun eliminarAlumno(alumno: AlumnoEntity) {
        viewModelScope.launch {
            repository.deleteAlumno(alumno)
        }
    }

    fun eliminarTodos() {
        viewModelScope.launch {
            repository.deleteAllAlumnos()
        }
    }
}
