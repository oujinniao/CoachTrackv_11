package com.example.coachtrack.data.cloud // Ajusta el paquete

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp

/**
 * Representa un Alumno tal como se guarda en Cloud Firestore.
 * Incluye campos para la seguridad (profesorId) y metadatos automáticos.
 */
data class AlumnoCloud(
    // Firestore asignará automáticamente un ID al crear el documento.
    @DocumentId
    val id: String = "",    //este es el ID de Firestore

    // --- CAMPOS DE TU ALUMNO ACTUAL (de AlumnoEntity) ---
    val nombre: String = "",
    val nivelActual: String = "",
    val objetivo: String = "",
    val clasesPactadas: Int = 0,
    val clasesCursadas: Int = 0,
    val estadoPago: String = "PENDIENTE", // Usamos String para Firestore
    val telefono: String = "",
    val direccion: String = "",
    val notasEntrenador: String = "",
    val edad: Int = 0,

    // --- CAMPOS NUEVOS CRÍTICOS PARA LA NUBE ---
    // ID del profesor dueño de este alumno. ESENCIAL para las reglas de seguridad.
    val profesorId: String = "",

    // Marca de tiempo que Firestore asigna AUTOMÁTICAMENTE al crear el documento.
    @ServerTimestamp
    val fechaCreacion: Timestamp? = null,

    // Puedes actualizar esto manualmente cuando modifiques el alumno.
    val fechaUltimaActualizacion: Timestamp? = null
)