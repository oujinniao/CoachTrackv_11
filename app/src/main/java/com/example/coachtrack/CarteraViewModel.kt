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

/**
 * ViewModel que maneja la lista de alumnos y operaciones
 * como agregar, actualizar o eliminar desde la base de datos Room.
 */
class CarteraViewModel(application: Application) : AndroidViewModel(application) {

    // Repositorio de acceso a la base de datos
    private val repository = AlumnoRepository(application)

    // 🔹 Flujo de alumnos desde Room (se actualiza automáticamente)
    val alumnos: StateFlow<List<AlumnoEntity>> = repository
        .getAlumnos()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // ---------------------------------------------------------
    // 🔹 Estado UI para mostrar/ocultar el diálogo "Agregar Alumno"
    // ---------------------------------------------------------
    var mostrarDialogoAgregar by mutableStateOf(false)
        private set

    fun abrirDialogoAgregar() { mostrarDialogoAgregar = true }
    fun cerrarDialogoAgregar() { mostrarDialogoAgregar = false }

    // ---------------------------------------------------------
    // 🔹 AGREGAR un nuevo alumno DEMO (para pruebas rápidas)
    // ---------------------------------------------------------
    fun agregarAlumnoDemo() {
        viewModelScope.launch {
            val nuevoAlumno = AlumnoEntity(
                nombre = "Alumno Demo ${(1000..9999).random()}",
                nivelActual = "Inicial",
                objetivo = "Mejorar técnica de saque",
                clasesPactadas = 5,
                clasesCursadas = 0,
                estadoPago = EstadoPago.PENDIENTE,
                edad = 15,
                telefono = "987654321",
                direccion = "Club Tenis Providencia",
                notasEntrenador = "Alumno creado automáticamente para pruebas."
            )
            repository.addAlumno(nuevoAlumno)
        }
    }

    // ---------------------------------------------------------
    // 🔹 AGREGAR/MODIFICAR/ELIMINAR
    // ---------------------------------------------------------
    fun agregarAlumnoManual(alumno: AlumnoEntity) {
        viewModelScope.launch {
            repository.addAlumno(alumno)
        }
    }

    fun actualizarAlumno(alumno: AlumnoEntity) {
        viewModelScope.launch {
            repository.updateAlumno(alumno)
        }
    }

    fun eliminarAlumno(alumno: AlumnoEntity) {
        viewModelScope.launch {
            repository.deleteAlumno(alumno)
        }
    }

    // ---------------------------------------------------------
    // 🔹 ELIMINAR TODOS (modo desarrollador)
    // ---------------------------------------------------------
    fun eliminarTodos() {
        viewModelScope.launch {
            repository.deleteAllAlumnos()
        }
    }
}
