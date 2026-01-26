package com.example.coachtrack

import android.content.Context
import androidx.room.Room
import kotlinx.coroutines.flow.Flow

class AlumnoRepository(context: Context) {

    private val db: CoachTrackDatabase = Room.databaseBuilder(
        context.applicationContext,
        CoachTrackDatabase::class.java,
        "coachtrack_db"
    )
        .fallbackToDestructiveMigration()
        .build()

    private val dao = db.alumnoDao()

    fun getAlumnos(): Flow<List<AlumnoEntity>> = dao.getAll()

    /**
     * Guarda evitando duplicidad por teléfono (clave única).
     * - Si no existe alumno con ese teléfono => INSERT
     * - Si existe => UPDATE sobre el mismo localId
     */
    suspend fun guardarAlumno(alumno: AlumnoEntity): Long {
        val telKey = alumno.telefono.trim()
        require(telKey.isNotBlank()) { "El teléfono no puede estar vacío si es la clave única." }

        val existing = dao.getByTelefono(telKey)

        return if (existing == null) {
            dao.insert(alumno.copy(localId = 0L, telefono = telKey))
        } else {
            dao.update(
                alumno.copy(
                    localId = existing.localId,
                    firebaseId = existing.firebaseId, // preserva si existe
                    telefono = telKey
                )
            )
            existing.localId
        }
    }

    suspend fun updateAlumno(alumno: AlumnoEntity) = dao.update(alumno)

    suspend fun deleteAlumno(alumno: AlumnoEntity) = dao.delete(alumno)

    // 👇 NUEVO: usado por CarteraViewModel.eliminarTodos()
    suspend fun deleteAllAlumnos() = dao.deleteAll()
}
