package com.example.coachtrack

import androidx.room.Entity
import androidx.room.PrimaryKey



@Entity(tableName = "alumnos")
data class AlumnoEntity(
    @PrimaryKey
    val id: String = "",
    val nombre: String = "",
    val nivelActual: String = "",
    val objetivo: String = "",
    val clasesPactadas: Int=0,
    val clasesCursadas: Int=0,
    val estadoPago: String = EstadoPago.PENDIENTE.name,
    val edad: Int = 0,
    val telefono: String = "",
    val direccion: String = "",
    val notasEntrenador: String = "",
    val profesorInstructor: Int?=null)
