package com.example.coachtrack.data.repository

import android.util.Log
import com.example.coachtrack.AlumnoDao
import com.example.coachtrack.AlumnoEntity
import com.example.coachtrack.ProfesorDao
import com.example.coachtrack.SesionDao
import com.example.coachtrack.SesionEntity
import com.example.coachtrack.TacticaDao
import com.example.coachtrack.TacticaEntity
import com.example.coachtrack.data.cloud.AlumnoCloud
import com.example.coachtrack.data.mappers.toCloud
import com.example.coachtrack.data.mappers.toLocal
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlin.jvm.java

private const val ALUMNOS_COLLECTION = "alumnos"

class AlumnoRepositoryHibrido(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val alumnoDao: AlumnoDao,
    private val profesorDao: ProfesorDao,
    private val sesionDao: SesionDao,
    private val tacticaDao: TacticaDao
) {

    private val TAG = "AlumnoRepoHibrido"
    var isCloudSyncEnabled: Boolean = false

    private val alumnosCollection get() = firestore.collection(ALUMNOS_COLLECTION)

    private val userId: String
        get() = auth.currentUser?.uid ?: throw IllegalStateException("Usuario no autenticado")

    fun obtenerAlumnosDelProfesor(): Flow<List<AlumnoEntity>> = alumnoDao.getAll()

    suspend fun getAlumnoLocalById(localId: Long): AlumnoEntity? =
        withContext(Dispatchers.IO) { alumnoDao.getById(localId) }

    suspend fun guardarSesion(sesion: SesionEntity) = withContext(Dispatchers.IO) {
        sesionDao.insert(sesion)
        Log.d(TAG, "Sesión guardada para alumnoId=${sesion.alumnoId}")
    }

    suspend fun guardarAlumno(alumno: AlumnoEntity) = withContext(Dispatchers.IO) {
        val localId = alumnoDao.upsert(alumno)
        val savedLocal = alumno.copy(localId = localId)

        if (!isCloudSyncEnabled) return@withContext

        val firestoreId = savedLocal.firebaseId ?: alumnosCollection.document().id

        val alumnoCloud = savedLocal.toCloud(
            profesorId = userId,
            firestoreId = firestoreId
        )

        alumnosCollection.document(firestoreId).set(alumnoCloud).await()

        if (savedLocal.firebaseId == null) {
            alumnoDao.updateFirebaseId(localId, firestoreId)
        }

        Log.d(TAG, "Alumno sincronizado: ${savedLocal.nombre}")
    }

    suspend fun incrementarClasesCursadas(alumnoLocalId: Long) = withContext(Dispatchers.IO) {
        val alumno = alumnoDao.getById(alumnoLocalId) ?: return@withContext
        val actualizado = alumno.copy(clasesCursadas = alumno.clasesCursadas + 1)
        guardarAlumno(actualizado)
    }

    suspend fun eliminarAlumno(alumno: AlumnoEntity) = withContext(Dispatchers.IO) {
        alumnoDao.delete(alumno)
        Log.d(TAG, "Alumno eliminado de Room: ${alumno.nombre}")

        if (!isCloudSyncEnabled || alumno.firebaseId == null) return@withContext

        try {
            alumnosCollection.document(alumno.firebaseId).delete().await()
            Log.d(TAG, "Alumno eliminado de Cloud")
        } catch (e: Exception) {
            Log.e(TAG, "Error al eliminar en Cloud: ${e.message}")
        }
    }

    suspend fun sincronizarCloudARoom() = withContext(Dispatchers.IO) {
        if (!isCloudSyncEnabled) return@withContext

        val snapshot = alumnosCollection
            .whereEqualTo("profesorId", userId)
            .get()
            .await()

        val alumnosCloud = snapshot.toObjects(AlumnoCloud::class.java)

        for (cloud in alumnosCloud) {
            val local = cloud.toLocal()

            val profesorLocalId = local.profesorFirebaseId?.let {
                profesorDao.getLocalIdByFirebaseId(it)
            }

            val finalEntity = local.copy(profesorInstructor = profesorLocalId)

            val existingLocalId = alumnoDao.getLocalIdByFirebaseId(finalEntity.firebaseId!!)

            val entityToUpsert =
                if (existingLocalId != null) finalEntity.copy(localId = existingLocalId)
                else finalEntity

            alumnoDao.upsert(entityToUpsert)
        }

        Log.d(TAG, "Sincronización Cloud → Room completada")
    }

    fun getTacticasPorAlumno(alumnoLocalId: Long): Flow<List<TacticaEntity>> {
        return tacticaDao.getTacticasPorAlumno(alumnoLocalId)
    }

    suspend fun guardarTactica(tactica: TacticaEntity) = withContext(Dispatchers.IO) {
        tacticaDao.insert(tactica)
    }
}
