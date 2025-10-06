package com.example.coachtrack

data class Alumno(
    val nombre: String,
    val ultimaSesion: String,
    val progreso: String
)

// Datos de ejemplo
val ALUMNOS_MOCK = listOf(
    Alumno("Juan Pérez", "20/09/2025", "Mejora Revés"),
    Alumno("María López", "19/09/2025", "Saque consistente"),
    Alumno("Carlos Gómez", "18/09/2025", "Trabajo físico avanzado"),
    Alumno("Lucía Fernández", "15/09/2025", "Volleys en la red")
)
