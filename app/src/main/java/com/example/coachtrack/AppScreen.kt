package com.example.coachtrack

// Clase sellada que define todos los destinos de navegación
sealed class AppScreen {


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

    object Dashboard : AppScreen()   // ✅ NUEVO


    data class FichaAlumno(val localId: Long) : AppScreen()




}