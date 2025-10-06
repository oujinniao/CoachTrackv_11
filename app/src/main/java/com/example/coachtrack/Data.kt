package com.example.coachtrack

// Definimos la estructura de un Ejercicio/Plantilla
data class Plantilla(
    val id: Int, // Identificador único
    val nombre: String,
    val duracionMinutos: Int,
    val enfoque: String // Ej: "Revés", "Saque", "Físico"
)

// Datos Ficticios (Mocks) que usaremos
val PLANTILLAS_MOCK = listOf(
    Plantilla(1, "Rutina de Calentamiento General", 15, "Físico"),
    Plantilla(2, "Drill de Saque Básico", 30, "Saque"),
    Plantilla(3, "Volleys en la Red (Baja intensidad)", 20, "Volea"),
    Plantilla(4, "Revés a una mano (Trabajo de pies)", 25, "Revés"),
    Plantilla(5, "Servicio y Bolea (Avazando a la Red)", 40, "Saque")
)

// Estructura para el guardado en Firebase (necesita ser serializable a mapa)
data class SesionData(
    val alumno: String = "Juan Pérez",
    val ejercicios: List<Plantilla> = emptyList(),
    val fecha: Long = System.currentTimeMillis()
)
