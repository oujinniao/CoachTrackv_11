package com.example.coachtrack.data.mappers

import com.example.coachtrack.ProfesorEntity
import com.example.coachtrack.data.cloud.ProfesorCloud
import com.google.firebase.Timestamp

// Asegúrate de que este paquete coincida con donde definiste ProfesorCloud
// import com.example.coachtrack.data.cloud.ProfesorCloud

/**
 * Convierte un ProfesorEntity (modelo LOCAL) a ProfesorCloud (modelo para la NUBE).
 * * @param propietarioId El ID de Firebase del usuario monousuario (Tú) que es dueño de este registro.
 * @param firestoreId El ID de Firebase (String) del colega. Si se omite, usa el firebaseId existente.
 */
fun ProfesorEntity.toCloud(
    propietarioId: String,
    firestoreId: String = this.firebaseId ?: ""
): ProfesorCloud {
    return ProfesorCloud(
        id = firestoreId,
        nombre = nombre,
        especialidad = especialidad,
        telefono = telefono,
        email = email,
        descripcion = descripcion,
        disponibilidad = disponibilidad,

        // 🔑 CLAVE: Marcamos este colega como PROPIEDAD TUYA.
        propietarioId = propietarioId,

        fechaCreacion = null,
        fechaUltimaActualizacion = Timestamp.now()
    )
}

/**
 * Convierte un ProfesorCloud (modelo de la NUBE) a ProfesorEntity (modelo LOCAL).
 */
fun ProfesorCloud.toLocal(): ProfesorEntity {
    return ProfesorEntity(
        id = 0L, // 0L: Para que Room sepa que debe autogenerar o usar la estrategia de Upsert (REPLACE)
        firebaseId = id,
        nombre = nombre,
        especialidad = especialidad,
        telefono = telefono,
        email = email,
        descripcion = descripcion,
        disponibilidad = disponibilidad
        // Nota: El campo 'propietarioId' de Cloud no se almacena en la Entidad local, ya que eres el único usuario.
    )
}