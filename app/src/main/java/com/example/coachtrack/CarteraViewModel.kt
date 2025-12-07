package com.example.coachtrack

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import com.example.coachtrack.data.repository.AlumnoRepositoryHibrido

class CarteraViewModel : ViewModel() {

    // Repositorio con inicialización diferida
    private val repository by lazy { AlumnoRepositoryHibrido() }

    // Estados para manejar errores
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    // Estado para controlar si hay datos cargados
    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    // Flow de alumnos con manejo de errores
    val alumnos = repository
        .obtenerAlumnosDelProfesor()
        .catch { exception ->
            // Capturar y manejar la excepción
            _error.value = when {
                exception is IllegalStateException &&
                        exception.message == "Usuario no autenticado" -> {
                    "Por favor, inicia sesión para ver los alumnos"
                }
                else -> "Error: ${exception.message}"
            }
            _isLoading.value = false
            emit(emptyList()) // Devolver lista vacía en caso de error
        }
        .stateIn(
            scope = viewModelScope,
            started = kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        // Cuando los datos se carguen, actualizar loading
        viewModelScope.launch {
            alumnos.collect {
                _isLoading.value = false
            }
        }
    }

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
    // 🔹 AGREGAR / ACTUALIZAR
    // --------------------------
    fun agregarOActualizarAlumno(alumno: AlumnoEntity, onResultado: (Boolean, Boolean) -> Unit) {
        viewModelScope.launch {
            // Limpiar error anterior
            _error.value = null

            // 1. Validar duplicados LOCALMENTE
            val listaActual = alumnos.value
            val existe = listaActual.any {
                it.nombre.equals(alumno.nombre, ignoreCase = true) && it.id != alumno.id
            }

            if (existe) {
                onResultado(false, false) // ❌ Duplicado
                return@launch
            }

            try {
                // 2. Guardar en Firebase
                val firestoreId = repository.guardarAlumno(alumno)
                val fueActualizacion = alumno.id.isNotEmpty()
                onResultado(true, fueActualizacion)

            } catch (e: IllegalStateException) {
                _error.value = "Debe iniciar sesión para guardar alumnos"
                onResultado(false, false)
            } catch (e: Exception) {
                _error.value = "Error al guardar: ${e.message}"
                onResultado(false, false)
            }
        }
    }

    // --------------------------
    // 🔹 ELIMINAR
    // --------------------------
    fun eliminarAlumno(alumno: AlumnoEntity) {
        viewModelScope.launch {
            try {
                _error.value = null
                repository.eliminarAlumno(alumno.id.toString())
            } catch (e: IllegalStateException) {
                _error.value = "Debe iniciar sesión para eliminar alumnos"
            } catch (e: Exception) {
                _error.value = "Error al eliminar: ${e.message}"
            }
        }
    }

    // --------------------------
    // 🔹 LIMPIAR ERROR
    // --------------------------
    fun limpiarError() {
        _error.value = null
    }

    // --------------------------
    // 🔹 ELIMINAR TODOS
    // --------------------------
    fun eliminarTodos() {
        viewModelScope.launch {
            println("⚠️ eliminarTodos() llamado. Necesita implementación para Firebase.")
        }
    }
}