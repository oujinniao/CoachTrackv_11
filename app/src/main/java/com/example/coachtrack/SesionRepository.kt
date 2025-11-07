package com.example.coachtrack

import android.content.Context
import androidx.room.Room
import kotlinx.coroutines.flow.Flow
import kotlin.collections.List
import kotlin.jvm.java
import com.example.coachtrack.SesionDao

/**
 * Repositorio que maneja las operaciones con la base de datos de sesiones.
 * ASUME QUE COACHTRACKDATABASE EXISTE.
 */
class SesionRepository(context: Context) {

    private val db: CoachTrackDatabase = Room.databaseBuilder(
        context.applicationContext,
        CoachTrackDatabase::class.java,
        "coachtrack_db"
    ).build()

    private val dao = db.sesionDao()

    fun getSesiones(): Flow<List<SesionEntity>> = dao.getAll()

    fun getSesionesPorAlumno(alumnoId: Int): Flow<List<SesionEntity>> =
        dao.getSesionesPorAlumno(alumnoId)

    suspend fun addSesion(sesion: SesionEntity) = dao.insert(sesion)

    suspend fun deleteSesion(sesion: SesionEntity) = dao.delete(sesion)

    suspend fun deleteAll() = dao.deleteAll()
}