package com.example.coachtrack

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.coachtrack.data.repository.AlumnoRepositoryHibrido
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

enum class DashboardFiltro { TODOS, AL_DIA, PENDIENTE, DEUDA }

data class DashboardUiState(
    val alumnos: List<AlumnoEntity> = emptyList(),
    val filtro: DashboardFiltro = DashboardFiltro.TODOS,
    val total: Int = 0,
    val alDia: Int = 0,
    val pendientes: Int = 0,
    val deuda: Int = 0
)

class DashboardViewModel(application: Application) : AndroidViewModel(application) {

    private val repo: AlumnoRepositoryHibrido =
        (application as CoachTrackApplication).container.alumnoRepositoryHibrido

    private val _filtro = MutableStateFlow(DashboardFiltro.TODOS)

    val uiState: StateFlow<DashboardUiState> =
        combine(repo.obtenerAlumnosDelProfesor(), _filtro) { alumnos, filtro ->

            val deuda = alumnos.count { it.estadoPago == EstadoPago.DEUDA.name }
            val pendientes = alumnos.count { it.estadoPago == EstadoPago.PENDIENTE.name }
            val alDia = alumnos.count { it.estadoPago == EstadoPago.ADELANTADO.name }

            val filtrados = when (filtro) {
                DashboardFiltro.TODOS -> alumnos
                DashboardFiltro.AL_DIA -> alumnos.filter { it.estadoPago == EstadoPago.ADELANTADO.name }
                DashboardFiltro.PENDIENTE -> alumnos.filter { it.estadoPago == EstadoPago.PENDIENTE.name }
                DashboardFiltro.DEUDA -> alumnos.filter { it.estadoPago == EstadoPago.DEUDA.name }
            }

            DashboardUiState(
                alumnos = filtrados,
                filtro = filtro,
                total = alumnos.size,
                alDia = alDia,
                pendientes = pendientes,
                deuda = deuda
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = DashboardUiState()
        )

    fun setFiltro(nuevo: DashboardFiltro) {
        _filtro.value = nuevo
    }
}
