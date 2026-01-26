package com.example.coachtrack.data.repository

import androidx.room.withTransaction
import com.example.coachtrack.AlumnoDao
import com.example.coachtrack.CoachTrackDatabase
import com.example.coachtrack.ProfesorDao
import com.example.coachtrack.ProfesorEntity
import com.example.coachtrack.data.cloud.ProfesorCloud
import com.example.coachtrack.data.mappers.toCloud
import com.example.coachtrack.data.mappers.toLocal
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import java.io.IOException
import javax.inject.Inject

class ProfesorRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val profesorDao: ProfesorDao,
    private val alumnoDao: AlumnoDao,
    private val db: CoachTrackDatabase
) {
    var isCloudSyncEnabled: Boolean = false

    private val profesoresCollection = firestore.collection("profesores")
    private fun getCurrentUserId(): String? = auth.currentUser?.uid

    // ----------------------------------------------------------------------
    // 1. OBTENER DATOS: LECTURA OFFLINE
    // ----------------------------------------------------------------------
    fun getProfesores(): Flow<List<ProfesorEntity>> = profesorDao.getAll()

    // ----------------------------------------------------------------------
    // 2. GUARDAR DATOS: UPSERT LOCAL POR EMAIL + SYNC CLOUD (OPCIONAL)
    // ----------------------------------------------------------------------
    suspend fun guardarProfesor(profesorLocal: ProfesorEntity): ProfesorEntity {

        val emailKey = profesorLocal.email.trim().lowercase()
        require(emailKey.isNotBlank()) { "El email del profesor no puede estar vacío." }

        // ✅ Buscar si ya existe por email (clave única)
        val existingByEmail = profesorDao.getByEmail(emailKey)

        val profesorNormalizado = profesorLocal.copy(email = emailKey)

        // 1) Persistencia LOCAL: si existe por email → update; si no → insert
        val savedLocal: ProfesorEntity = if (existingByEmail == null) {
            val newId = profesorDao.insert(profesorNormalizado.copy(id = 0L))
            profesorNormalizado.copy(id = newId)
        } else {
            val toUpdate = profesorNormalizado.copy(
                id = existingByEmail.id,
                firebaseId = existingByEmail.firebaseId // preserva firebaseId si existía
            )
            profesorDao.update(toUpdate)
            toUpdate
        }

        // 2) Sincronización CLOUD (solo si está habilitada)
        if (!isCloudSyncEnabled) return savedLocal

        val userId = getCurrentUserId()
            ?: throw IllegalStateException("Usuario no autenticado para sincronización")

        val isNewCloudRecord = savedLocal.firebaseId.isNullOrBlank()
        val firestoreId =
            if (isNewCloudRecord) profesoresCollection.document().id else savedLocal.firebaseId!!

        val profesorCloud = savedLocal.toCloud(
            propietarioId = userId,
            firestoreId = firestoreId
        )

        profesoresCollection.document(firestoreId).set(profesorCloud).await()

        // 3) Actualizar firebaseId local si fue nuevo en cloud
        return if (isNewCloudRecord) {
            val withFirebase = savedLocal.copy(firebaseId = firestoreId)
            profesorDao.update(withFirebase)
            withFirebase
        } else {
            savedLocal
        }
    }

    // ----------------------------------------------------------------------
    // 3. SINCRONIZACIÓN DE CLOUD A ROOM (UPSERT PROFESOR)
    // ----------------------------------------------------------------------
    suspend fun sincronizarCloudARoom() {
        if (!isCloudSyncEnabled) return

        val userId = getCurrentUserId()
            ?: throw IllegalStateException("Usuario no autenticado para sincronización")

        val snapshot = profesoresCollection
            .whereEqualTo("propietarioId", userId)
            .get()
            .await()

        val profesoresCloud = snapshot.toObjects(ProfesorCloud::class.java)

        for (cloud in profesoresCloud) {
            val local = cloud.toLocal()

            // Upsert por firebaseId (para sync)
            val existingLocal = local.firebaseId?.let { profesorDao.getByFirebaseId(it) }

            val finalLocal = if (existingLocal != null) {
                local.copy(id = existingLocal.id)
            } else {
                local.copy(id = 0L)
            }

            profesorDao.insert(finalLocal)
        }
    }

    // ----------------------------------------------------------------------
    // 4. OPERACIONES CRUD CON ASIGNACIÓN
    // ----------------------------------------------------------------------
    suspend fun addProfesorAndAssignAlumno(
        profesor: ProfesorEntity,
        alumnoId: Long?
    ): Long {
        return db.withTransaction {
            val savedProfesor = guardarProfesor(profesor)
            val newProfesorId: Long = savedProfesor.id

            if (alumnoId != null) {
                val alumno = alumnoDao.getById(alumnoId)
                if (alumno != null) {
                    alumnoDao.update(
                        alumno.copy(profesorInstructor = newProfesorId)
                    )
                }
            }

            newProfesorId
        }
    }

    // ----------------------------------------------------------------------
    // 5. ELIMINACIÓN HÍBRIDA
    // ----------------------------------------------------------------------
    suspend fun deleteProfesor(profesor: ProfesorEntity) {
        // 1. Borrar local
        profesorDao.delete(profesor)

        // 2. Desasignar alumnos
        val alumnosAsignados = alumnoDao.getAlumnosByProfesorId(profesor.id)
        alumnosAsignados.forEach { alumno ->
            alumnoDao.update(alumno.copy(profesorInstructor = null))
        }

        // 3. Borrar en la nube
        if (!isCloudSyncEnabled) return

        val firestoreId = profesor.firebaseId
        if (firestoreId.isNullOrBlank()) return

        try {
            profesoresCollection.document(firestoreId).delete().await()
        } catch (e: Exception) {
            if (e !is IOException) throw e
        }
    }

    suspend fun deleteAllProfesores() = profesorDao.deleteAll()
}
