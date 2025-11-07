package com.example.coachtrack

import android.content.Context
import androidx.room.Room
import kotlinx.coroutines.flow.Flow
import kotlin.jvm.java
import com.example.coachtrack.AlumnoEntity
import com.example.coachtrack.CoachTrackDatabase

class AlumnoRepository(context: Context) {

    private val db: CoachTrackDatabase = Room.databaseBuilder(
        context.applicationContext,
        CoachTrackDatabase::class.java,
        "coachtrack_db"
    ).build()

    private val dao = db.alumnoDao()

     fun getAlumnos(): Flow<List<AlumnoEntity>> = dao.getAll()

    suspend fun addAlumno(alumno: AlumnoEntity) = dao.insert(alumno)

    suspend fun updateAlumno(alumno: AlumnoEntity) = dao.update(alumno)

    suspend fun deleteAlumno(alumno: AlumnoEntity) = dao.delete(alumno)
}
