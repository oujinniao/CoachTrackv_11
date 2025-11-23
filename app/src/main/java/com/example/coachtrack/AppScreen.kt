package com.example.coachtrack


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

}