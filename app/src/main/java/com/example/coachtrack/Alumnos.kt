package com.example.coachtrack

data class Alumnos(
    val id: String,
    val nombre: String,
    val clasesPactadas: Int,
    val clasesCursadas: Int,
    val estadoPago: EstadoPago

)

enum class EstadoPago { ADELANTADO, PENDIENTE, DEUDA }
