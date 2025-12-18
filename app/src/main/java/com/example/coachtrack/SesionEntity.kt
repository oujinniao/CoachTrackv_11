package com.example.coachtrack

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo

@Entity(tableName = "sesiones")
data class SesionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "alumnoId")
    val alumnoId: Long, // 💡 CORREGIDO: De Int a Long

    @ColumnInfo(name = "alumnoNombre")
    val alumnoNombre: String,   // ✅ campo: nombre del alumno

    @ColumnInfo(name = "fecha")
    val fecha: String,

    @ColumnInfo(name = "duracion")
    val duracion: Int,

    @ColumnInfo(name = "ejercicios")
    val ejercicios: String, // Guardamos los ejercicios como string (ej: "Drill de Saque, Calentamiento")

    @ColumnInfo(name = "notas")
    val notas: String = "",

    @ColumnInfo(name = "completada")
    val completada: Boolean = true
)