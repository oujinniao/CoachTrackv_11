package com.example.coachtrack.data.mappers // Ajusta el paquete

import com.example.coachtrack.data.cloud.AlumnoCloud
import com.example.coachtrack.AlumnoEntity
import com.google.firebase.Timestamp
import java.util.Date

/**
 * Convierte un AlumnoEntity (modelo LOCAL de Room) a AlumnoCloud (modelo para la NUBE).
 * @param profesorId El ID único del profesor dueño de este alumno. ¡ES OBLIGATORIO!
 */
fun AlumnoEntity.toCloud(profesorId: String): AlumnoCloud {
    return AlumnoCloud(
        id = this.id,
        nombre = this.nombre,
        nivelActual = this.nivelActual,
        objetivo = this.objetivo,
        clasesPactadas = this.clasesPactadas,
        clasesCursadas = this.clasesCursadas,
        estadoPago = this.estadoPago,
        telefono = this.telefono,
        direccion = this.direccion,
        notasEntrenador = this.notasEntrenador,
        edad = this.edad,
        // Campo CRÍTICO para la seguridad
        profesorId = profesorId,
        fechaCreacion = null,
        fechaUltimaActualizacion = Timestamp.now()
    )
}

/**
 * Convierte un AlumnoCloud (modelo de la NUBE) a AlumnoEntity (modelo LOCAL).
 */
fun AlumnoCloud.toLocal(): AlumnoEntity {
    return AlumnoEntity(
        id = this.id,
        nombre = this.nombre,
        nivelActual = this.nivelActual,
        objetivo = this.objetivo,
        clasesPactadas = this.clasesPactadas,
        clasesCursadas = this.clasesCursadas,
        estadoPago = this.estadoPago,
        telefono = this.telefono,
        direccion = this.direccion,
        notasEntrenador = this.notasEntrenador,
        edad = this.edad,

        profesorInstructor = null // O this.profesorId.toIntOrNull()
    )
}