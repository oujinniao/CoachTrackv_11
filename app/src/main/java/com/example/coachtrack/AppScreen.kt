package com.example.coachtrack

// Clase sellada que define todos los destinos de navegación
sealed class AppScreen {


    object Splash : AppScreen()
    data object Principal : AppScreen()
    data object Planificacion : AppScreen()
     data object Historial : AppScreen()
    data object VideoAnalisis : AppScreen()
    data object Cartera : AppScreen()
    data object Video : AppScreen()
    data object Camara : AppScreen()
    data object Inicio : AppScreen()
    data object Gestion : AppScreen()
    data object GestionProfesores : AppScreen()
    data object Pagos : AppScreen()

    data object Dashboard : AppScreen()   // ✅ NUEVO


    data class FichaAlumno(val localId: Long) : AppScreen()




}