package com.example.coachtrack

data class Alumnos(
    val id: String,
    val nombre: String,
    val nivel: String,
    val objetivo: String? = null,
    val clasesPactadas: Int = 0,
    val clasesCursadas: Int = 0,
    val estadoPago: EstadoPago = EstadoPago.PENDIENTE,
    var notasEntrenador: String = "",
    val fechaInicio: String? = null,
    val sesiones: List<Sesion> = emptyList(),
    val tacticas: List<Tactica> = emptyList(),

    // 🔗 Referencia al nuevo modelo
    val datosPersonales: DatosPersonales = DatosPersonales(),
    val nivelJuego: String = ""
) {
    val progreso: Int
        get() = if (clasesPactadas > 0)
            (clasesCursadas * 100) / clasesPactadas
        else 0
}

enum class EstadoPago { ADELANTADO, PENDIENTE, DEUDA }
