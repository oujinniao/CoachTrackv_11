package com.example.coachtrack

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo

@Entity(tableName = "sesiones")
data class SesionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "alumnoId")
    val alumnoId: Int,

    @ColumnInfo(name = "fecha")
    val fecha: String,

    @ColumnInfo(name = "duracion")
    val duracion: Int,

    @ColumnInfo(name = "ejercicios")
    val ejercicios: String, // Guardar como JSON o string separado por comas

    @ColumnInfo(name = "notas")
    val notas: String = "",

    @ColumnInfo(name = "completada")
    val completada: Boolean = true
)