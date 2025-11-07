package com.example.coachtrack

import androidx.room.Entity
import androidx.room.PrimaryKey




@Entity(tableName = "alumnos")
data class AlumnoEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nombre: String = "",
    val nivelActual: String = "",
    val objetivo: String = "",
    val clasesPactadas: Int,
    val clasesCursadas: Int,
    val estadoPago: EstadoPago = EstadoPago.PENDIENTE, // Ahora usa el enum directamente
    val edad: Int = 0,
    val telefono: String = "",
    val direccion: String = "",
    val notasEntrenador: String = "")
