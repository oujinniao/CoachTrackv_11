package com.example.coachtrack

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CarteraViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AlumnoRepository(application)

    // 🔹 Conectamos directamente al Flow de Room
    // Cada vez que cambia la base de datos, se actualiza automáticamente.
    val alumnos: StateFlow<List<AlumnoEntity>> = repository
        .getAlumnos()  // Flow<List<AlumnoEntity>>
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // 🔹 Agregar un nuevo alumno
    fun agregarAlumno(alumno: AlumnoEntity) {
        viewModelScope.launch {
            try {
                repository.addAlumno(alumno)
                // ❌ ya no llamamos cargarAlumnos(), el Flow se actualiza solo
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // 🔹 Actualizar alumno existente
    fun actualizarAlumno(alumno: AlumnoEntity) {
        viewModelScope.launch {
            try {
                repository.updateAlumno(alumno)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // 🔹 Eliminar alumno
    fun eliminarAlumno(alumno: AlumnoEntity) {
        viewModelScope.launch {
            try {
                repository.deleteAlumno(alumno)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
