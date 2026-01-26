package com.example.coachtrack

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "alumnos",
    indices = [
        Index(value = ["firebaseId"], unique = true),
        Index(value = ["telefono"], unique = true),])  //bloquea alumnos duplicados

data class AlumnoEntity(
    @PrimaryKey(autoGenerate = true)
    val localId: Long = 0L,

    val firebaseId: String? = null,

    val nombre: String = "",
    val nivelActual: String = "",
    val objetivo: String = "",
    val clasesPactadas: Int = 0,
    val clasesCursadas: Int = 0,
    val estadoPago: String = EstadoPago.PENDIENTE.name,
    val edad: Int = 0,
    val telefono: String = "",
    val direccion: String = "",
    val notasEntrenador: String = "",

    val profesorInstructor: Long? = null,
    val profesorFirebaseId: String? = null
)
