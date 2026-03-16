package com.example.coachtrack.data.repository

import android.database.sqlite.SQLiteConstraintException
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
import timber.log.Timber
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

    //private val TAG = "AlumnoRepoHibrido"
    var isCloudSyncEnabled: Boolean = false

    private val alumnosCollection get() = firestore.collection(ALUMNOS_COLLECTION)

    private val userId: String
        get() = auth.currentUser?.uid ?: throw IllegalStateException("Usuario no autenticado")

    fun obtenerAlumnosDelProfesor(): Flow<List<AlumnoEntity>> = alumnoDao.getAll()

    suspend fun getAlumnoLocalById(localId: Long): AlumnoEntity? =
        withContext(Dispatchers.IO) { alumnoDao.getById(localId) }

    suspend fun guardarSesion(sesion: SesionEntity) = withContext(Dispatchers.IO) {
        sesionDao.insert(sesion)
        Timber.d( "Sesión guardada para alumnoId=${sesion.alumnoId}")
    }

    suspend fun guardarAlumno(alumno: AlumnoEntity) = withContext(Dispatchers.IO) {

        // 1) Normalizar la clave única (teléfono)
        val telKey = alumno.telefono.trim()

        // Recomendación: si el teléfono será la "clave única", debe ser obligatorio
        require(telKey.isNotBlank()) {
            "El teléfono es obligatorio para evitar duplicidad de alumnos."
        }

        // 2) Resolver duplicidad: si es "nuevo" (localId=0) pero ya existe por teléfono, actualizar en lugar de insertar
        val existingByTelefono = alumnoDao.getByTelefono(telKey)

        val alumnoParaGuardar: AlumnoEntity =
            if (alumno.localId == 0L && existingByTelefono != null) {
                // ✅ Convertimos el insert en update: usamos el localId existente
                // y preservamos firebaseId existente (si lo hubiera)
                alumno.copy(
                    localId = existingByTelefono.localId,
                    firebaseId = existingByTelefono.firebaseId,
                    telefono = telKey
                )
            } else {
                // ✅ Caso normal: update (localId>0) o insert real (localId=0 sin existente)
                alumno.copy(telefono = telKey)
            }

        // 3) Persistencia local
        val localId: Long = try {
            alumnoDao.upsert(alumnoParaGuardar)
        } catch (e: SQLiteConstraintException) {
            // Fallback defensivo: si el índice unique se dispara, hacemos update sobre el existente
            val current = alumnoDao.getByTelefono(telKey)
                ?: throw e

            val forceUpdate = alumnoParaGuardar.copy(
                localId = current.localId,
                firebaseId = current.firebaseId
            )
            alumnoDao.update(forceUpdate)
            current.localId
        }

        val savedLocal = alumnoParaGuardar.copy(localId = localId)

        // 4) Cloud Sync (queda igual; si está deshabilitado, se corta aquí)
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

        Timber.d( "Alumno sincronizado: ${savedLocal.nombre}")
    }

    suspend fun incrementarClasesCursadas(alumnoLocalId: Long) = withContext(Dispatchers.IO) {
        val alumno = alumnoDao.getById(alumnoLocalId) ?: return@withContext
        val actualizado = alumno.copy(clasesCursadas = alumno.clasesCursadas + 1)
        guardarAlumno(actualizado)
    }

    suspend fun eliminarAlumno(alumno: AlumnoEntity) = withContext(Dispatchers.IO) {
        alumnoDao.delete(alumno)
        Timber.d( "Alumno eliminado de Room: ${alumno.nombre}")

        if (!isCloudSyncEnabled || alumno.firebaseId == null) return@withContext

        try {
            alumnosCollection.document(alumno.firebaseId).delete().await()
            Timber.d( "Alumno eliminado de Cloud")
        } catch (e: Exception) {
            Timber.e( "Error al eliminar en Cloud: ${e.message}")
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

        Timber.d( "Sincronización Cloud → Room completada")
    }

    fun getTacticasPorAlumno(alumnoLocalId: Long): Flow<List<TacticaEntity>> {
        return tacticaDao.getTacticasPorAlumno(alumnoLocalId)
    }

    suspend fun guardarTactica(tactica: TacticaEntity) = withContext(Dispatchers.IO) {
        tacticaDao.insert(tactica)
    }
}
