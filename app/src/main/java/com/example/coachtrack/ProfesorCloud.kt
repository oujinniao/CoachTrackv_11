package com.example.coachtrack.data.cloud

import com.google.firebase.Timestamp
import java.util.Date

// Clase ProfesorCloud.kt
data class ProfesorCloud(
    // ID de Firebase del registro (profesor colega)
    val id: String = "",

    // 🔑 CLAVE: El ID del usuario monousuario (Tú) que es dueño de este registro.
    // Esto se usa en el ProfesorRepository para filtrar la descarga de datos.
    val propietarioId: String = "",

    // Datos del Profesor Colega
    val nombre: String = "",
    val especialidad: String = "",
    val telefono: String = "",
    val email: String = "",
    val descripcion: String = "",
    val disponibilidad: String = "",

    // Metadatos de Sincronización
    val fechaCreacion: Timestamp? = null,
    val fechaUltimaActualizacion: Timestamp? = null
)