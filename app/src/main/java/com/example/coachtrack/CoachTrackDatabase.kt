package com.example.coachtrack

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [AlumnoEntity::class, SesionEntity::class],
    version = 5,  // Incrementa la versión por los cambios
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class CoachTrackDatabase : RoomDatabase() {
    abstract fun alumnoDao(): AlumnoDao
    abstract fun sesionDao(): SesionDao
}