package com.example.coachtrack.data.repository

import com.example.coachtrack.data.cloud.AlumnoCloud
import com.example.coachtrack.AlumnoEntity
import com.example.coachtrack.data.mappers.toCloud
import com.example.coachtrack.data.mappers.toLocal
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.snapshots
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf // ← AÑADE ESTE IMPORT
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlin.jvm.java

class AlumnoRepositoryHibrido @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {
    constructor() : this(FirebaseAuth.getInstance(), Firebase.firestore)

    private val alumnosCollection = firestore.collection("alumnos")

    // ✅ CAMBIO 1: Hacerla función en lugar de propiedad
    private fun getCurrentUserId(): String? {
        return auth.currentUser?.uid // ← Devuelve null en lugar de lanzar excepción
    }

    suspend fun guardarAlumno(alumnoLocal: AlumnoEntity): String {
        // ✅ CAMBIO 2: Verificar con la nueva función
        val userId = getCurrentUserId()
            ?: throw IllegalStateException("Usuario no autenticado")

        val alumnoCloud = alumnoLocal.toCloud(profesorId = userId)
        val documentReference = alumnosCollection.document(alumnoCloud.id).apply {
            set(alumnoCloud).await()
        }
        return documentReference.id
    }

    fun obtenerAlumnosDelProfesor(): Flow<List<AlumnoEntity>> {
        // ✅ CAMBIO 3: Verificar usuario primero
        val userId = getCurrentUserId()
        if (userId == null) {
            // Si no hay usuario, devolver un Flow vacío
            return flowOf(emptyList())
        }

        val query = alumnosCollection.whereEqualTo("profesorId", userId)
        return query.snapshots().map { querySnapshot ->
            querySnapshot.documents.mapNotNull { document ->
                val alumnoCloud = document.toObject(AlumnoCloud::class.java)
                alumnoCloud?.toLocal()
            }
        }
    }

    suspend fun eliminarAlumno(alumnoId: String) {
        // ✅ CAMBIO 4: Verificar autenticación
        val userId = getCurrentUserId()
            ?: throw IllegalStateException("Usuario no autenticado")

        alumnosCollection.document(alumnoId).delete().await()
    }

    suspend fun obtenerAlumnoPorId(alumnoId: String): AlumnoEntity? {
        // ✅ CAMBIO 5: Verificar autenticación
        val userId = getCurrentUserId()
            ?: return null

        val document = alumnosCollection.document(alumnoId).get().await()
        return document.toObject(AlumnoCloud::class.java)?.toLocal()
    }
}