package com.example.coachtrack

import android.content.Context
import androidx.room.Room
import androidx.room.withTransaction
import kotlinx.coroutines.flow.Flow
import kotlin.jvm.java

class ProfesorRepository(context: Context) {

    private val db: CoachTrackDatabase = Room.databaseBuilder(
        context.applicationContext,
        CoachTrackDatabase::class.java,
        "coachtrack_db"
    )
        .fallbackToDestructiveMigration()
        .build()

    private val dao = db.profesorDao()

    fun getProfesores(): Flow<List<ProfesorEntity>> = dao.getAll()

    /**
     * Inserta un profesor y devuelve el id generado (Int).
     */
    suspend fun addProfesor(profesor: ProfesorEntity): Int {
        val idLong = dao.insert(profesor)
        return idLong.toInt()
    }

    suspend fun updateProfesor(profesor: ProfesorEntity) = dao.update(profesor)

    suspend fun deleteProfesor(profesor: ProfesorEntity) = dao.delete(profesor)

    suspend fun deleteAllProfesores() = dao.deleteAll()

    /**
     * Inserta un profesor y, en la misma transacción, asigna el alumno (si alumnoId != null).
     * Devuelve el id del profesor insertado.
     */
    suspend fun addProfesorAndAssignAlumno(profesor: ProfesorEntity, alumnoId: Int?): Int {
        return db.withTransaction {
            val newId = dao.insert(profesor).toInt()
            if (alumnoId != null) {
                val alumnoDao = db.alumnoDao()
                val alumno = alumnoDao.getById(alumnoId)
                if (alumno != null) {
                    alumnoDao.update(alumno.copy(profesorInstructor = newId))
                }
            }
            newId
        }
    }
}