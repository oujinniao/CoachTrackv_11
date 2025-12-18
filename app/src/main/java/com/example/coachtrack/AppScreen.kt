package com.example.coachtrack

// Clase sellada que define todos los destinos de navegación
sealed class AppScreen {

    // Estados que no necesitan datos (usados como object)
    object Splash : AppScreen()
    object Principal : AppScreen()
    object Planificacion : AppScreen()
    object Historial : AppScreen()
    object VideoAnalisis : AppScreen()
    object Cartera : AppScreen()
    object Video : AppScreen()
    object Camara : AppScreen()
    object Inicio : AppScreen()
    object Gestion : AppScreen()
    object GestionProfesores : AppScreen()
    object Pagos : AppScreen()

    // 💡 CORRECCIÓN CRÍTICA: Estado que necesita datos (usado como data class)
    // Esto permite que el estado de navegación lleve el ID del alumno de Room.
    data class FichaAlumno(val localId: Long) : AppScreen()

}