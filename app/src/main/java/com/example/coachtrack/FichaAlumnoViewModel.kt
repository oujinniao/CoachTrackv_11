package com.example.coachtrack

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


class FichaAlumnoViewModel : ViewModel() {

    private val _alumno = MutableLiveData(Alumnos(

        id = "",
        nombre = "",
        nivelActual = "",
        objetivo = "",
        clasesPactadas = 0,
        clasesCursadas = 0,
        estadoPago = EstadoPago.PENDIENTE,
        datosPersonales = DatosPersonales(),


    ))
            val alumno: LiveData<Alumnos> = _alumno

    fun actualizarDatosPersonales(dp: DatosPersonales) {
        _alumno.value = _alumno.value?.copy(datosPersonales = dp)
    }
    fun guardarAlumno(){
        println("Alumno guardado: ${alumno.value}")
        //mas adelante guardar en Room o Firestore
    }
}







