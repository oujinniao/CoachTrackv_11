package com.example.coachtrack

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class FichaAlumnoViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AlumnoRepository(application)

    private val _alumno = MutableLiveData(
        Alumnos(
            id = "",
            nombre = "",
            nivelActual = "",
            objetivo = "",
            clasesPactadas = 0,
            clasesCursadas = 0,
            estadoPago = EstadoPago.PENDIENTE,
            datosPersonales = DatosPersonales()
        )
    )
    val alumno: LiveData<Alumnos> = _alumno

    fun actualizarDatosPersonales(dp: DatosPersonales) {
        _alumno.value = _alumno.value?.copy(datosPersonales = dp)
    }

    fun actualizarClasesPactadas(nuevoValor: Int) {
        _alumno.value = _alumno.value?.copy(clasesPactadas = nuevoValor)
    }

    fun guardarAlumno() {
        viewModelScope.launch {
            _alumno.value?.let { alumno ->
                val entity = AlumnoEntity(
                    id = alumno.id.toIntOrNull() ?: 0,
                    nombre = alumno.nombre ?: "",
                    nivelActual = alumno.nivelActual ?: "",
                    objetivo = alumno.objetivo ?: "",
                    clasesPactadas = alumno.clasesPactadas,
                    clasesCursadas = alumno.clasesCursadas,
                    estadoPago = alumno.estadoPago, // ✅ Enum directo
                    edad = alumno.datosPersonales.edad,
                    telefono = alumno.datosPersonales.telefono ?: "",
                    direccion = alumno.datosPersonales.direccion ?: "",
                    notasEntrenador = alumno.notasEntrenador ?: ""
                )
                repository.updateAlumno(entity)
                println("✅ Alumno actualizado: ${alumno.nombre} → Pactadas=${alumno.clasesPactadas}")
            }
        }
    }
}
