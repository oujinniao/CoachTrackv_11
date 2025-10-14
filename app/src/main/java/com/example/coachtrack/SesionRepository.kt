package com.example.coachtrack



object SesionRepository {
    private val sesionesGuardadas = mutableListOf<Sesion>()

    fun agregarSesion(sesion: Sesion) {
        sesionesGuardadas.add(0, sesion) // se agrega al inicio (más reciente primero)
    }

    fun obtenerSesiones(): List<Sesion> = sesionesGuardadas
}
