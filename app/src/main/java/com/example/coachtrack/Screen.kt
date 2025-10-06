package com.example.coachtrack

sealed class Screen(val route: String) {
    object Principal : Screen("principal")
    object Planificacion : Screen("planificacion")
    object Historial : Screen("historial")
    object VideoAnalisis : Screen("video_analisis")
}
