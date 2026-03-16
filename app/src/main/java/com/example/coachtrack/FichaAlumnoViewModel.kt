package com.example.coachtrack

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.coachtrack.data.repository.AlumnoRepositoryHibrido
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FichaAlumnoViewModel(application: Application) : AndroidViewModel(application) {

    private val repositoryHibrido: AlumnoRepositoryHibrido =
        (application as CoachTrackApplication).container.alumnoRepositoryHibrido

    private val sesionRepository = SesionRepository(
        (application as CoachTrackApplication).container.db.sesionDao()
    )

    val sesionViewModel = SesionViewModel(application, sesionRepository)
    val tacticaViewModel = TacticaViewModel(application)

    // ----------------------------------------------------------------------
    // MAPEO ENTRE ENTIDAD (Room) Y MODELO (UI)
    // ----------------------------------------------------------------------

    private fun mapEntityToUI(entity: AlumnoEntity): Alumnos {
        return Alumnos(
            localId = entity.localId,
            firebaseId = entity.firebaseId,
            nombre = entity.nombre,
            nivelActual = entity.nivelActual,
            objetivo = entity.objetivo.ifBlank { null },
            clasesPactadas = entity.clasesPactadas,
            clasesCursadas = entity.clasesCursadas,
            estadoPago = EstadoPago.valueOf(entity.estadoPago),
            datosPersonales = DatosPersonales(
                edad = entity.edad,
                telefono = entity.telefono,
                direccion = entity.direccion
            ),
            notasEntrenador = entity.notasEntrenador,
            fechaInicio = null,
            sesiones = emptyList(),
            tacticas = emptyList()
        )
    }

    private fun mapUIToEntity(uiModel: Alumnos): AlumnoEntity {
        return AlumnoEntity(
            localId = uiModel.localId,
            firebaseId = uiModel.firebaseId,
            nombre = uiModel.nombre,
            nivelActual = uiModel.nivelActual,
            objetivo = uiModel.objetivo ?: "",
            clasesPactadas = uiModel.clasesPactadas,
            clasesCursadas = uiModel.clasesCursadas,
            estadoPago = uiModel.estadoPago.name,
            edad = uiModel.datosPersonales.edad,
            telefono = uiModel.datosPersonales.telefono,
            direccion = uiModel.datosPersonales.direccion,
            notasEntrenador = uiModel.notasEntrenador,
            profesorInstructor = null
        )
    }

    // ----------------------------------------------------------------------
    // STATE FLOW PRINCIPAL
    // ----------------------------------------------------------------------
    private val _alumno = MutableStateFlow<Alumnos?>(null)
    val alumno: StateFlow<Alumnos?> = _alumno

    // ----------------------------------------------------------------------
    // CARGAR ALUMNO EXISTENTE
    // ----------------------------------------------------------------------
    fun cargarAlumno(localId: Long) {
        if (localId == 0L) return

        viewModelScope.launch {
            val entity = repositoryHibrido.getAlumnoLocalById(localId)
            entity?.let {
                _alumno.value = mapEntityToUI(it)
                sesionViewModel.cargarSesiones(localId)
                tacticaViewModel.cargarTacticas(localId)
            }
        }
    }

    // ----------------------------------------------------------------------
    // ACTUALIZACIÓN DE NOTAS
    // ----------------------------------------------------------------------
    fun actualizarNotas(nuevasNotas: String) {
        viewModelScope.launch {
            _alumno.value?.let { currentAlumno ->
                val updatedAlumno = currentAlumno.copy(notasEntrenador = nuevasNotas)
                _alumno.value = updatedAlumno
                repositoryHibrido.guardarAlumno(mapUIToEntity(updatedAlumno))
            }
        }
    }

    // ----------------------------------------------------------------------
    // ACTUALIZACIÓN DE CLASES CURSADAS
    // ----------------------------------------------------------------------
    fun actualizarClasesCursadas(nuevasClases: Int) {
        viewModelScope.launch {
            _alumno.value?.let { currentAlumno ->
                val updatedAlumno = currentAlumno.copy(clasesCursadas = nuevasClases)
                _alumno.value = updatedAlumno
                repositoryHibrido.guardarAlumno(mapUIToEntity(updatedAlumno))
            }
        }
    }
}