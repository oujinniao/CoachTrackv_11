package com.example.coachtrack

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [
        AlumnoEntity::class,
        SesionEntity::class,
        SesionAgendaEntity::class,
        ProfesorEntity::class,
        TacticaEntity::class
    ],
    version = 14,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class CoachTrackDatabase : RoomDatabase() {
    abstract fun alumnoDao(): AlumnoDao
    abstract fun sesionDao(): SesionDao
    abstract fun sesionAgendaDao(): SesionAgendaDao
    abstract fun profesorDao(): ProfesorDao
    abstract fun tacticaDao(): TacticaDao

    companion object {
        val MIGRATION_13_14 = object : Migration(13, 14) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "ALTER TABLE alumnos ADD COLUMN tarifaPorClase INTEGER NOT NULL DEFAULT 0"
                )
            }
        }
    }
}