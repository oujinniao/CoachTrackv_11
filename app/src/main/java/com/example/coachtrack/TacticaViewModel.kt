package com.example.coachtrack

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.coachtrack.data.repository.AlumnoRepositoryHibrido
import kotlinx.coroutines.flow.MutableStateFlow // 💡 NECESARIO para gestionar el cambio de ID
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest // 💡 NECESARIO para reconectar el Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel para gestionar la creación y la consulta de Tácticas.
 */
class TacticaViewModel(application: Application) : AndroidViewModel(application) {

    private val repositoryHibrido: AlumnoRepositoryHibrido =
        (application as CoachTrackApplication).container.alumnoRepositoryHibrido

    // 💡 ESTADO CORREGIDO: Usamos MutableStateFlow para sostener el ID del alumno.
    // Al cambiar este valor, se dispara el flatMapLatest.
    private val _alumnoLocalId = MutableStateFlow(0L)

    // 💡 FLOW CORREGIDO: Usa flatMapLatest para reaccionar al cambio de _alumnoLocalId.
    // Esto conecta el Flow del repositorio con el StateFlow observable de la vista.
    val tacticasDelAlumno: StateFlow<List<TacticaEntity>> =
        _alumnoLocalId
            .flatMapLatest { id ->
                if (id == 0L) {
                    flowOf(emptyList<TacticaEntity>())
                } else {
                    // Llama al repositorio, que devuelve un Flow de Room.
                    // Cada vez que Room inserta o actualiza, este Flow emitirá una nueva lista.
                    repositoryHibrido.getTacticasPorAlumno(id)
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList<TacticaEntity>()
            )

    /**
     * Carga las tácticas para un alumno específico.
     * Simplemente actualiza el ID que está siendo observado.
     */
    fun cargarTacticas(alumnoLocalId: Long) {
        if (alumnoLocalId != 0L) {
            _alumnoLocalId.value = alumnoLocalId
        }
    }

    /**
     * Guarda una nueva táctica.
     */
    fun guardarTactica(titulo: String, descripcion: String, nivel: String) {
        val currentAlumnoLocalId = _alumnoLocalId.value
        if (currentAlumnoLocalId == 0L) {
            println("Error: No se puede guardar la táctica sin un ID de alumno válido.")
            return
        }
        viewModelScope.launch {
            val nuevaTactica = TacticaEntity(
                alumnoLocalId = currentAlumnoLocalId,
                titulo = titulo,
                descripcion = descripcion,
                nivel = nivel,
            )
            // 💡 Guardar en el repositorio (esto dispara una actualización en Room,
            // lo cual, a su vez, actualiza tacticasDelAlumno gracias al flatMapLatest).
            repositoryHibrido.guardarTactica(nuevaTactica)
        }
    }
}