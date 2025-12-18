package com.example.coachtrack.data.mappers // Ajusta el paquete

import com.example.coachtrack.data.cloud.AlumnoCloud
import com.example.coachtrack.AlumnoEntity
import com.google.firebase.Timestamp
import java.util.Date

/**
 * Convierte un AlumnoEntity (modelo LOCAL de Room) a AlumnoCloud (modelo para la NUBE).
 * * NOTA: El profesorId de la Nube (String) se toma del campo profesorFirebaseId de la entidad local.
 * Si el campo es nulo, se usa el ID del usuario actual de Firebase (profesorId).
 * * @param profesorId El ID único del profesor dueño de este alumno (ID de Firebase del usuario actual).
 */
fun AlumnoEntity.toCloud(
    profesorId: String,
    firestoreId: String = this.firebaseId ?: ""
): AlumnoCloud {
    return AlumnoCloud(
        id = firestoreId,
        nombre = nombre,
        nivelActual = nivelActual,
        objetivo = objetivo,
        clasesPactadas = clasesPactadas,
        clasesCursadas = clasesCursadas,
        estadoPago = estadoPago,
        telefono = telefono,
        direccion = direccion,
        notasEntrenador = notasEntrenador,
        edad = edad,

        // ✅ CORRECCIÓN 1: El ID del profesor para la nube es el String de Firebase.
        // Si no está, usa el ID pasado como argumento (el ID del usuario autenticado).
        profesorId = this.profesorFirebaseId ?: profesorId,

        fechaCreacion = null,
        fechaUltimaActualizacion = Timestamp.now()
    )
}

/**
 * Convierte un AlumnoCloud (modelo de la NUBE) a AlumnoEntity (modelo LOCAL).
 */
fun AlumnoCloud.toLocal(): AlumnoEntity {
    return AlumnoEntity(
        firebaseId = id,
        localId = 0,
        nombre = nombre,
        nivelActual = nivelActual,
        objetivo = objetivo,
        clasesPactadas = clasesPactadas,
        clasesCursadas = clasesCursadas,
        estadoPago = estadoPago,
        telefono = telefono,
        direccion = direccion,
        notasEntrenador = notasEntrenador,
        edad = edad,

        // ✅ CORRECCIÓN 2: Almacena el ID de Firebase del profesor en el campo String.
        profesorFirebaseId = profesorId,

        // ✅ CORRECCIÓN 3: La FK LOCAL (Long?) se establece como NULL.
        // La relación Long se resuelve después en el Repositorio tras insertar al profesor.
        profesorInstructor = null
    )
}