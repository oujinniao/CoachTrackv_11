package com.example.coachtrack

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [
        AlumnoEntity::class,
        SesionEntity::class,
        SesionAgendaEntity::class,
         ProfesorEntity::class],
    version = 8,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class CoachTrackDatabase : RoomDatabase() {
    abstract fun alumnoDao(): AlumnoDao
    abstract fun sesionDao(): SesionDao
    abstract fun sesionAgendaDao(): SesionAgendaDao
    abstract fun profesorDao(): ProfesorDao
}