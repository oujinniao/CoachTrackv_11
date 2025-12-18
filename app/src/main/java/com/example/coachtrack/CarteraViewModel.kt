package com.example.coachtrack


import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.coachtrack.data.repository.AlumnoRepositoryHibrido
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


class CarteraViewModel(application: Application) : AndroidViewModel(application) {

    // 1. Acceso al Repositorio Híbrido a través del Service Locator

    private val repository: AlumnoRepositoryHibrido =
        (application as CoachTrackApplication).container.alumnoRepositoryHibrido

    // 2. Lógica de Suscripción (Controla FREE vs. PRO)
    // NOTA: En producción, esto se obtendría del perfil de Firebase.
    // Por ahora, lo establecemos como PRO para probar la sincronización.
    var isProUser by mutableStateOf(true)

// ... (El resto del código es correcto y se mantiene igual) ...

    private val MAX_ALUMNOS_FREE = 20

    // 3. Estados de manejo de datos (Lectura de Room)
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    // El Flow siempre lee de Room, garantizando el Offline-First
    val alumnos: StateFlow<List<AlumnoEntity>> = repository
        .obtenerAlumnosDelProfesor()
        .catch { exception ->
            _error.value = "Error al leer alumnos: ${exception.message}"
            _isLoading.value = false
            emit(emptyList<AlumnoEntity>())
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList<AlumnoEntity>()
        )

    init {
        // ... (código init existente) ...
        viewModelScope.launch {
            alumnos.collect {
                _isLoading.value = false
            }
        }
    }

    // ----------------------------------------------------------------------
    // 🔹 RECUPERACIÓN / SINCRONIZACIÓN INICIAL (Función PRO)
    // ----------------------------------------------------------------------
    fun iniciarSincronizacionInicial() {
        // 1. Conecta el estado del ViewModel con el Repositorio
        repository.isCloudSyncEnabled = isProUser

        if (!isProUser) {
            println("Usuario FREE: No se ejecuta sincronización con la nube (Función PRO).")
            return
        }

        viewModelScope.launch {
            _error.value = null
            try {
                // 2. Ejecuta la recuperación (Cloud -> Room)
                repository.sincronizarCloudARoom()
                println("✅ Recuperación de datos PRO completada con éxito.")
            } catch (e: Exception) {
                _error.value = "Error al sincronizar datos iniciales: ${e.message}. Trabajando con datos locales."
            }
        }
    }


    // --------------------------
    // 🔹 AGREGAR / ACTUALIZAR (Con Límite FREE)
    // --------------------------
    fun agregarOActualizarAlumno(alumno: AlumnoEntity, onResultado: (Boolean, Boolean) -> Unit) {
        viewModelScope.launch {
            _error.value = null

            val listaActual = alumnos.value
            val esNuevo = alumno.localId == 0L

            if (esNuevo && !isProUser && listaActual.size >= MAX_ALUMNOS_FREE) {
                _error.value = "Límite alcanzado: La versión Básica solo permite ${MAX_ALUMNOS_FREE} alumnos. Actualiza a PRO."
                onResultado(false, false)
                return@launch
            }

            val existe = listaActual.any {
                it.nombre.equals(alumno.nombre, ignoreCase = true) && it.localId != alumno.localId
            }
            if (existe) {
                onResultado(false, false)
                return@launch
            }

            try {
                // Guarda en Room (y en Cloud si corresponde)
                repository.guardarAlumno(alumno)

                val fueActualizacion = alumno.localId != 0L
                onResultado(true, fueActualizacion)

            } catch (e: IllegalStateException) {
                _error.value = "Debe iniciar sesión para guardar alumnos."
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

                // El repositorio maneja la eliminación local y remota (según isCloudSyncEnabled)
                repository.eliminarAlumno(alumno)

            } catch (e: Exception) {
                _error.value = "Error al eliminar: ${e.message}"
            }
        }
    }

    // --------------------------
    // 🔹 ELIMINAR TODOS
    // --------------------------
    fun eliminarTodos() {
        // NOTA: Esta función debe ser implementada en el repositorio si deseas eliminar todo,
        // incluyendo todos los registros de la nube si el usuario es PRO.
        viewModelScope.launch {
            println("⚠️ eliminarTodos() llamado. Necesita implementación en el repositorio para eliminar Room y Cloud (si es PRO).")
        }
    }

    // ... (otras funciones como abrir/cerrar diálogo y limpiar error) ...
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

    fun limpiarError() {
        _error.value = null
    }
}