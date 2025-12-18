package com.example.coachtrack

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo

// 💡 Si las tácticas son contenido que se guarda, deben ser una Entidad de Room.
@Entity(tableName = "tacticas")
data class TacticaEntity(
    // 1. Clave principal de Room
    @PrimaryKey(autoGenerate = true)
    val localId: Long = 0L,

    // 2. Clave de Firebase para sincronización (Null en modo FREE o si no se sincroniza aún)
    val firebaseId: String? = null,

    // 3. Clave foránea para asociar esta táctica a un alumno específico
    @ColumnInfo(index = true) // 💡 Recomendado para búsquedas rápidas por alumno
    val alumnoLocalId: Long,

    // 4. Los campos de datos originales
    val titulo: String,
    val descripcion: String,
    val nivel: String, // Cambiado de 'nivelActual' a 'nivel' para ser más conciso en la Entidad

    // 5. Opcional: Metadatos
    val fechaCreacion: Long = System.currentTimeMillis()
)