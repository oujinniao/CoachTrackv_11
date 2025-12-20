package com.example.coachtrack

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.coachtrack.data.repository.AlumnoRepositoryHibrido
import kotlinx.coroutines.flow.*
import java.util.Locale

data class HistorialUiState(
    val query: String = "",
    val alumnosFiltrados: List<AlumnoEntity> = emptyList(),
    val alumnoSeleccionado: AlumnoEntity? = null,
    val sesionesDelSeleccionado: List<SesionEntity> = emptyList()
)

class HistorialViewModel(application: Application) : AndroidViewModel(application) {

    private val alumnoRepo: AlumnoRepositoryHibrido =
        (application as CoachTrackApplication).container.alumnoRepositoryHibrido

    // IMPORTANTE: requiere que tu container exponga db (CoachTrackDatabase)
    private val sesionDao: SesionDao =
        (application as CoachTrackApplication).container.db.sesionDao()

    private val _query = MutableStateFlow("")
    private val _selectedAlumnoId = MutableStateFlow<Long?>(null)

    private val alumnosFlow: Flow<List<AlumnoEntity>> = alumnoRepo.obtenerAlumnosDelProfesor()

    private val alumnoSeleccionadoFlow: Flow<AlumnoEntity?> =
        combine(alumnosFlow, _selectedAlumnoId) { alumnos, selectedId ->
            selectedId?.let { id -> alumnos.firstOrNull { it.localId == id } }
        }

    private val sesionesSeleccionadoFlow: Flow<List<SesionEntity>> =
        _selectedAlumnoId.flatMapLatest { id ->
            if (id == null) flowOf(emptyList())
            else sesionDao.getSesionesPorAlumno(id)
        }

    val uiState: StateFlow<HistorialUiState> =
        combine(
            alumnosFlow,
            _query,
            alumnoSeleccionadoFlow,
            sesionesSeleccionadoFlow
        ) { alumnos, query, alumnoSeleccionado, sesiones ->

            val q = query.trim().lowercase(Locale.getDefault())

            val filtrados = if (q.isBlank()) {
                alumnos
            } else {
                alumnos.filter { a ->
                    a.nombre.lowercase(Locale.getDefault()).contains(q)
                }
            }

            HistorialUiState(
                query = query,
                alumnosFiltrados = filtrados,
                alumnoSeleccionado = alumnoSeleccionado,
                sesionesDelSeleccionado = sesiones
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = HistorialUiState()
        )

    fun setQuery(value: String) {
        _query.value = value
    }

    fun seleccionarAlumno(alumnoId: Long) {
        // Si tocas el mismo alumno, colapsa (toggle)
        _selectedAlumnoId.value =
            if (_selectedAlumnoId.value == alumnoId) null else alumnoId
    }

    fun limpiarSeleccion() {
        _selectedAlumnoId.value = null
    }
}
