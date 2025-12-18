package com.example.coachtrack

sealed class Screen(val route: String) {
    object Principal : Screen("principal")
    object Planificacion : Screen("planificacion")
    object Historial : Screen("historial")
    object VideoAnalisis : Screen("video_analisis")
    object Cartera : Screen("cartera")
    object Video : Screen("video")
    object Camara : Screen("camara")
    object Inicio : Screen("inicio")
    data class FichaAlumno(val alumno: Alumnos) : Screen("ficha_alumno/${alumno.localId}")

    data class MiniPlanificacion(val alumno: Alumnos) : Screen("mini_planificacion/${alumno.localId}")


}