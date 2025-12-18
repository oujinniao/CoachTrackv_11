package com.example.coachtrack.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coachtrack.Plantilla
import com.example.coachtrack.SesionDeClase
import com.example.coachtrack.data.repository.AlumnoRepositoryHibrido
import com.example.coachtrack.obtenerBibliotecaCompletaDePlantillas
import com.example.coachtrack.toEntity // Función de conversión
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

// Estado de la UI para la pantalla de planificación
data class PlanificacionUiState(
    val bibliotecaPlantillas: List<Plantilla> = emptyList(),
    val sesionActual: SesionDeClase = SesionDeClase(
        sessionId = 0L,
        fechaCreacion = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date()),
        alumnoNombre = "Seleccionar Alumno",
        duracionTotalMinutos = 0,
        ejercicios = mutableListOf()
    ),
    val isLoading: Boolean = false,
    val error: String? = null,
    val shareEventText: String? = null // 🔑 Disparador para la acción de Compartir en la UI
)

class PlanificacionViewModel @Inject constructor(
    private val alumnoRepository: AlumnoRepositoryHibrido
) : ViewModel() {

    private val _uiState = MutableStateFlow(PlanificacionUiState())
    val uiState: StateFlow<PlanificacionUiState> = _uiState.asStateFlow()

    init {
        cargarBiblioteca()
    }

    // --- 1. GESTIÓN DE LA BIBLIOTECA ---

    private fun cargarBiblioteca() {
        // Carga la lista unificada (fábrica + personalizados)
        val plantillas = obtenerBibliotecaCompletaDePlantillas()
        _uiState.update { it.copy(bibliotecaPlantillas = plantillas) }
    }

    // Función para manejar la creación de un nuevo recurso personalizado
    fun crearNuevoRecurso(nombre: String, duracion: Int, enfoque: String, descripcion: String) {
        // En un entorno de producción:
        // 1. Crear PlantillaEntity
        // 2. Guardar en Room/Cloud usando un PlantillaRepository.

        // --- Lógica Mock de Creación ---
        val nuevaPlantilla = Plantilla(
            id = "user_${System.currentTimeMillis()}",
            nombre = nombre,
            enfoque = enfoque,
            duracionMinutos = duracion,
            descripcion = descripcion,
            imageUrl = "https://placehold.co/400x200/40C4FF/black?text=CUSTOM",
            esPersonalizado = true
        )
        // Por ahora, como es Mock, asumimos que se actualiza el mock en data.kt
        // y recargamos la biblioteca para que el usuario la vea inmediatamente.
        cargarBiblioteca()
    }

    // --- 2. CONSTRUCCIÓN DE LA SESIÓN ---

    fun seleccionarAlumno(nombre: String, alumnoId: Long) {
        // ⚠️ En una app real, podrías querer guardar el alumnoId de forma separada
        _uiState.update {
            it.copy(sesionActual = it.sesionActual.copy(alumnoNombre = nombre, sessionId = alumnoId))
        }
    }

    fun agregarPlantilla(plantilla: Plantilla) {
        // Clonamos la lista para asegurar la inmutabilidad de StateFlow
        val listaActual = _uiState.value.sesionActual.ejercicios.toMutableList()
        listaActual.add(plantilla)

        val nuevaDuracion = listaActual.sumOf { it.duracionMinutos }

        _uiState.update {
            it.copy(
                sesionActual = it.sesionActual.copy(
                    ejercicios = listaActual, // Usamos la lista clonada y modificada
                    duracionTotalMinutos = nuevaDuracion
                )
            )
        }
    }

    fun quitarPlantilla(plantillaInstanceId: String) {
        val listaActual = _uiState.value.sesionActual.ejercicios.toMutableList()

        // Removemos por la instancia ID, ya que puede haber plantillas repetidas.
        listaActual.removeAll { it.instanceId == plantillaInstanceId }

        val nuevaDuracion = listaActual.sumOf { it.duracionMinutos }

        _uiState.update {
            it.copy(
                sesionActual = it.sesionActual.copy(
                    ejercicios = listaActual,
                    duracionTotalMinutos = nuevaDuracion
                )
            )
        }
    }

    // --- 3. ACCIONES FINALES ---

    /**
     * 🔑 FUNCIÓN COMPARTIR: Lanza el evento de compartir. La UI monitorea 'shareEventText'.
     */
    fun onCompartirSesionClick() {
        val texto = _uiState.value.sesionActual.generarTextoCompartir()
        _uiState.update { it.copy(shareEventText = texto) }
    }

    /**
     * Limpia el evento de compartir después de que la UI lo ha manejado para evitar re-envíos.
     */
    fun shareEventHandled() {
        _uiState.update { it.copy(shareEventText = null) }
    }

    /**
     * 🔑 FUNCIÓN GUARDAR: Guarda la Sesión en la base de datos (Room).
     */
    fun guardarSesion(alumnoId: Long) = viewModelScope.launch {
        if (alumnoId == 0L || _uiState.value.sesionActual.ejercicios.isEmpty()) {
            _uiState.update { it.copy(error = "Debe seleccionar un alumno y agregar ejercicios.") }
            return@launch
        }

        _uiState.update { it.copy(isLoading = true) }
        try {
            val sesionAGuardar = _uiState.value.sesionActual

            val sesionEntity = sesionAGuardar.toEntity(
                alumnoId = alumnoId,
                alumnoNombre = sesionAGuardar.alumnoNombre
            )

            // ✅ Conectado: Llama al repositorio para guardar en Room.
            alumnoRepository.guardarSesion(sesionEntity)

            // Reiniciar el estado para una nueva sesión
            _uiState.update {
                PlanificacionUiState(
                    bibliotecaPlantillas = it.bibliotecaPlantillas,
                    isLoading = false
                )
            }
        } catch (e: Exception) {
            _uiState.update { it.copy(error = "Error al guardar la sesión: ${e.localizedMessage}", isLoading = false) }
        }
    }
}