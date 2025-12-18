package com.example.coachtrack.data.repository

import android.util.Log
import androidx.room.Room
import androidx.room.withTransaction
import com.example.coachtrack.AlumnoDao
import com.example.coachtrack.ProfesorDao
import com.example.coachtrack.ProfesorEntity
import com.example.coachtrack.CoachTrackDatabase
import com.example.coachtrack.data.cloud.ProfesorCloud // Asume que existe
import com.example.coachtrack.data.mappers.toCloud // Asume que existe
import com.example.coachtrack.data.mappers.toLocal // Asume que existe
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import java.io.IOException
import javax.inject.Inject
import kotlin.jvm.java

// 🔑 CLAVE: Constructor primario usado para Inyección de Dependencias (@Inject)
class ProfesorRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val profesorDao: ProfesorDao,
    private val alumnoDao: AlumnoDao,
    private val db: CoachTrackDatabase // La base de datos, necesaria para transacciones
) {
    var isCloudSyncEnabled: Boolean = false
    private val profesoresCollection = firestore.collection("profesores")
    private fun getCurrentUserId(): String? = auth.currentUser?.uid

    // ----------------------------------------------------------------------
    // 1. OBTENER DATOS: LECTURA OFFLINE
    // ----------------------------------------------------------------------

    fun getProfesores(): Flow<List<ProfesorEntity>> = profesorDao.getAll()

    // ----------------------------------------------------------------------
    // 2. GUARDAR DATOS: LÓGICA HÍBRIDA (CRUD LOCAL + SYNC CLOUD)
    // ----------------------------------------------------------------------

    /**
     * Guarda un profesor, resolviendo si es un Insert o Update.
     * Sincroniza con la nube si la sincronización está habilitada.
     */
    suspend fun guardarProfesor(profesorLocal: ProfesorEntity): ProfesorEntity {

        // 1. Persistencia LOCAL (Lógica de Upsert en Room)
        val savedLocal: ProfesorEntity = if (profesorLocal.id == 0L) {
            val newId = profesorDao.insert(profesorLocal)
            profesorLocal.copy(id = newId)
        } else {
            profesorDao.update(profesorLocal)
            profesorLocal
        }

        // 2. Sincronización CLOUD (Solo si está habilitada)
        if (!isCloudSyncEnabled) return savedLocal

        val userId = getCurrentUserId()
            ?: throw IllegalStateException("Usuario no autenticado para sincronización")

        val isNewCloudRecord = savedLocal.firebaseId.isNullOrBlank()
        val firestoreId =
            if (isNewCloudRecord) profesoresCollection.document().id else savedLocal.firebaseId!!

        val profesorCloud = savedLocal.toCloud(
            propietarioId = userId,
            firestoreId = firestoreId)
        profesoresCollection.document(firestoreId).set(profesorCloud).await()

        // 3. Actualizar la entidad local con el firebaseId si fue nuevo
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

    /**
     * Trae todos los profesores de la nube y los actualiza o inserta en Room.
     */
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
            var local = cloud.toLocal()

            // 1. PREVENCIÓN DE DUPLICACIÓN (UPSERT)
            val existingLocal = local.firebaseId?.let { profesorDao.getByFirebaseId(it) }

            val finalLocal = if (existingLocal != null) {
                // Si existe, usamos su ID local (Long) para que el REPLACE lo actualice.
                local.copy(id = existingLocal.id)
            } else {
                local // Se insertará con id = 0L
            }

            // 2. Guardar en Room
            profesorDao.insert(finalLocal)
        }
    }


    // ----------------------------------------------------------------------
    // 4. OPERACIONES CRUD CON ASIGNACIÓN
    // ----------------------------------------------------------------------

    /**
     * Inserta un profesor (usando la lógica híbrida) y, en la misma transacción, asigna el alumno.
     */
    suspend fun addProfesorAndAssignAlumno(
        profesor: ProfesorEntity,
        alumnoId: Long?
    ): Long {
        return db.withTransaction {
            // Usamos la lógica híbrida de guardarProfesor
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