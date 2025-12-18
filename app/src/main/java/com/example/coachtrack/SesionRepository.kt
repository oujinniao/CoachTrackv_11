package com.example.coachtrack

import android.content.Context
import androidx.room.Room
import kotlinx.coroutines.flow.Flow


class SesionRepository(context: Context) {

    private val db: CoachTrackDatabase = Room.databaseBuilder(
        context.applicationContext,
        CoachTrackDatabase::class.java,
        "coachtrack_db"
    ).build()

    private val dao = db.sesionDao()

    fun getSesiones(): Flow<List<SesionEntity>> = dao.getAll()

    fun getSesionesPorAlumno(alumnoId: Long): Flow<List<SesionEntity>> =
        dao.getSesionesPorAlumno(alumnoId)

    suspend fun addSesion(sesion: SesionEntity) = dao.insert(sesion)

    suspend fun deleteSesion(sesion: SesionEntity) = dao.delete(sesion)

    suspend fun deleteAll() = dao.deleteAll()
}