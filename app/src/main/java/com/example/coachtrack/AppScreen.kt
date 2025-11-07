package com.example.coachtrack

// ✅ NUEVA SEALED CLASS SIMPLIFICADA
sealed class AppScreen {
    object Principal : AppScreen()
    object Planificacion : AppScreen()
    object Historial : AppScreen()
    object VideoAnalisis : AppScreen()
    object Cartera : AppScreen()
    object Video : AppScreen()
    object Camara : AppScreen()
    object Inicio : AppScreen()
}