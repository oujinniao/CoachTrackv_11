package com.example.coachtrack

data class Plantilla(
    val id: Int,
    val nombre: String,
    val duracionMinutos: Int,
    val enfoque: String
)

val PLANTILLAS_MOCK = listOf(
    Plantilla(1, "Rutina de Calentamiento General", 15, "Físico"),
    Plantilla(2, "Drill de Saque Básico", 30, "Saque"),
    Plantilla(3, "Volleys en la Red (Baja intensidad)", 20, "Volea"),
    Plantilla(4, "Revés a una mano (Trabajo de pies)", 25, "Revés"),
    Plantilla(5, "Servicio y Bolea (Avanzando a la Red)", 40, "Saque")
)
