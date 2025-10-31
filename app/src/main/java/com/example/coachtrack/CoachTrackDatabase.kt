package com.example.coachtrack

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [AlumnoEntity::class], version = 2)
abstract class CoachTrackDatabase : RoomDatabase() {
    abstract fun alumnoDao(): AlumnoDao
}