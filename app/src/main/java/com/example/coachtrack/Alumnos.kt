package com.example.coachtrack

import com.example.coachtrack.DatosPersonales
import com.example.coachtrack.Sesion
import com.example.coachtrack.Tactica



data class Alumnos(
    val id: String,
    val nombre: String,

    val objetivo: String? = null,
    val clasesPactadas: Int = 0,
    val clasesCursadas: Int = 0,
    val estadoPago: EstadoPago = EstadoPago.PENDIENTE,
    var notasEntrenador: String = "",
    val fechaInicio: String? = null,
    val sesiones: List<Sesion> = emptyList(),
    val tacticas: List<Tactica> = emptyList(),
    val datosPersonales: DatosPersonales = DatosPersonales(),
    val nivelActual: String = ""
) {
    val progreso: Int
        get() = if (clasesPactadas > 0)
            (clasesCursadas * 100) / clasesPactadas
        else 0
}

enum class EstadoPago { ADELANTADO, PENDIENTE, DEUDA }
