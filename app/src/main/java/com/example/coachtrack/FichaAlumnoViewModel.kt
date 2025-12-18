package com.example.coachtrack

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.coachtrack.data.repository.AlumnoRepositoryHibrido
import kotlinx.coroutines.launch


class FichaAlumnoViewModel(application: Application) : AndroidViewModel(application) {

    private val repositoryHibrido: AlumnoRepositoryHibrido =
        (application as CoachTrackApplication).container.alumnoRepositoryHibrido

    // 💡 NECESARIO: Inicializar SesionRepository para pasarlo al ViewModel
    private val sesionRepository = SesionRepository(application.applicationContext)


    // 🎯 CORRECCIÓN: Pasamos el repositorio al constructor de SesionViewModel
    val sesionViewModel = SesionViewModel(application, sesionRepository)
    // TacticaViewModel asume que el constructor con solo Application es suficiente.
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
    // LIVE DATA PRINCIPAL
    // ----------------------------------------------------------------------
    private val _alumno = MutableLiveData<Alumnos?>(null)
    val alumno: LiveData<Alumnos?> = _alumno


    // ----------------------------------------------------------------------
    // CARGAR ALUMNO EXISTENTE
    // ----------------------------------------------------------------------
    fun cargarAlumno(localId: Long) {
        if (localId == 0L) return

        viewModelScope.launch {
            val entity = repositoryHibrido.getAlumnoLocalById(localId)

            entity?.let {
                // Mapear la entidad de Room al modelo de UI
                _alumno.value = mapEntityToUI(it)

                // Cargar las subcolecciones
                sesionViewModel.cargarSesiones(localId)
                tacticaViewModel.cargarTacticas(localId)
            }
        }
    }

    // ----------------------------------------------------------------------
    // FUNCIONES DE ACTUALIZACIÓN DEL ESTADO LOCAL Y REPOSITORIO (NECESARIAS PARA LA UI)
    // ----------------------------------------------------------------------

    /**
     * Actualiza solo las notas del entrenador y guarda en el repositorio.
     */
    fun actualizarNotas(localId: Long, nuevasNotas: String) {
        viewModelScope.launch {
            _alumno.value?.let { currentAlumno ->
                // Actualizamos la copia local (UI Model)
                val updatedAlumno = currentAlumno.copy(notasEntrenador = nuevasNotas)
                _alumno.value = updatedAlumno

                // Guardamos la ENTITY mapeada en el repositorio
                repositoryHibrido.guardarAlumno(mapUIToEntity(updatedAlumno))
            }
        }
    }

    /**
     * Actualiza solo las clases cursadas y guarda en el repositorio.
     */
    fun actualizarClasesCursadas(localId: Long, nuevasClases: Int) {
        viewModelScope.launch {
            _alumno.value?.let { currentAlumno ->
                // Actualizamos la copia local (UI Model)
                val updatedAlumno = currentAlumno.copy(clasesCursadas = nuevasClases)
                _alumno.value = updatedAlumno

                // Guardamos la ENTITY mapeada en el repositorio
                repositoryHibrido.guardarAlumno(mapUIToEntity(updatedAlumno))
            }
        }
    }

    // ... Otras funciones de tu código original (guardarAlumno, actualizarDatosPersonales, etc.)
}