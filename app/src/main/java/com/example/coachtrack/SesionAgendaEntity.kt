package com.example.coachtrack

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Sesiones agendadas del profesor (agenda de horas).
 * No es lo mismo que el historial técnico de la clase.
 */
@Entity(tableName = "sesiones_agenda")
data class SesionAgendaEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "alumnoId")
    val alumnoId: Int,

    @ColumnInfo(name = "alumnoNombre")
    val alumnoNombre: String,

    // Ej: "2025-11-18"
    @ColumnInfo(name = "fecha")
    val fecha: String,

    // Ej: "10:00"
    @ColumnInfo(name = "hora")
    val hora: String,

    @ColumnInfo(name = "lugar")
    val lugar: String = "",

    // Precio en CLP (puede ser null si no se definió)
    @ColumnInfo(name = "precio")
    val precio: Int? = null,

    // Reutilizamos tu enum EstadoPago (ADELANTADO, PENDIENTE, DEUDA)
    @ColumnInfo(name = "estadoPago")
    val estadoPago: EstadoPago = EstadoPago.PENDIENTE,

    @ColumnInfo(name = "nota")
    val nota: String = ""
)
