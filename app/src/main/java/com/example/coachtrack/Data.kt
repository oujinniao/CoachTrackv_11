package com.example.coachtrack

import java.util.UUID

// ------------------ ESTRUCTURAS DE DATOS ------------------

data class Plantilla(
    val id: String,
    val nombre: String,
    val enfoque: String,
    val duracionMinutos: Int,
    val descripcion: String,
    val imageUrl: String,
    val instanceId: String = UUID.randomUUID().toString()
) {
    fun toMap(): Map<String, Any> = mapOf(
        "id" to id,
        "nombre" to nombre,
        "enfoque" to enfoque,
        "duracionMinutos" to duracionMinutos,
        "descripcion" to descripcion,
        "imageUrl" to imageUrl
    )
}

// ------------------ SESIÓN COMPLETA (para planificación) ------------------

data class SesionDeClase(
    val sessionId: String,
    val userId: String = "PROTOTIPO_DEMO",
    val fechaCreacion: String,
    val alumnoNombre: String,
    val duracionTotalMinutos: Int,
    val ejercicios: MutableList<Plantilla>
) {
    fun toMap(): Map<String, Any> = mapOf(
        "userId" to userId,
        "fecha" to fechaCreacion,
        "alumno" to alumnoNombre,
        "duracionTotalMinutos" to duracionTotalMinutos,
        "ejercicios" to ejercicios.map { it.toMap() }
    )
}

// ------------------ SESIÓN SIMPLE (para ficha de alumno) ------------------

data class SesionSimple(
    val id: String,          // identificador interno (sessionId)
    val titulo: String,      // nombre o tema de la sesión
    val fecha: String,       // fecha de realización
    val duracionMin: Int,    // duración en minutos
    val estado: String       // estado (Completada, Pendiente, etc.)
)

// ------------------ CONVERSORES ------------------

fun SesionDeClase.toSesion(): SesionSimple {
    val listaEjercicios = ejercicios.joinToString(", ") { it.nombre }
    return SesionSimple(
        id = sessionId,
        titulo = "Sesión (${ejercicios.size} ejercicios): $listaEjercicios",
        fecha = fechaCreacion,
        duracionMin = duracionTotalMinutos,
        estado = "Completada"
    )
}

// ------------------ CONVERSOR: SesionDeClase → SesionEntity (para guardar en Room) ------------------

fun SesionDeClase.toEntity(alumnoId: Int, alumnoNombre: String): SesionEntity {
    return SesionEntity(
        alumnoId = alumnoId,
        alumnoNombre = alumnoNombre, // ✅ guardamos el nombre del alumno
        fecha = fechaCreacion,
        duracion = duracionTotalMinutos,
        ejercicios = ejercicios.joinToString(", ") { it.nombre },
        notas = "",
        completada = true
    )
}

// ------------------ MOCK DATA ------------------

fun getMockPlantillas(): List<Plantilla> = listOf(
    Plantilla(
        id = "p1",
        nombre = "Rutina de Calentamiento",
        enfoque = "Prevención y Activación",
        duracionMinutos = 10,
        descripcion = "Secuencia dinámica para preparar articulaciones.",
        imageUrl = "android.resource://com.example.coachtrack/drawable/calentamiento"
    ),
    Plantilla(
        id = "p2",
        nombre = "Drill de Saque Básico",
        enfoque = "Técnica Fundamental",
        duracionMinutos = 30,
        descripcion = "Mecánica del lanzamiento de bola y punto de impacto.",
        imageUrl = "https://placehold.co/400x200/2196F3/white?text=IMAGEN+DRILL+SAQUE"
    ),
    Plantilla(
        id = "p3",
        nombre = "Juego de Pies en la Red",
        enfoque = "Movimiento y Agilidad",
        duracionMinutos = 20,
        descripcion = "Agilidad cerca de la red para mejorar reacción.",
        imageUrl = "https://placehold.co/400x200/FFC107/black?text=IMAGEN+PIES+RED"
    ),
    Plantilla(
        id = "p4",
        nombre = "Ejercicios de Cierre (Cool Down)",
        enfoque = "Recuperación y Flexibilidad",
        duracionMinutos = 15,
        descripcion = "Estiramientos para relajar músculos tras la sesión.",
        imageUrl = "https://placehold.co/400x200/9C27B0/white?text=IMAGEN+CIERRE"
    )
)

val PLANTILLAS_MOCK = getMockPlantillas()

val SESIONES_GUARDADAS = mutableListOf(
    SesionDeClase(
        sessionId = "s1",
        userId = "PROTOTIPO_DEMO",
        fechaCreacion = "2025-10-01 10:00",
        alumnoNombre = "Juan Pérez",
        duracionTotalMinutos = 40,
        ejercicios = mutableListOf(
            PLANTILLAS_MOCK[0],
            PLANTILLAS_MOCK[2]
        )
    )
)

// ------------------ HELPERS ------------------

fun generarNuevoSessionId(sesiones: List<SesionEntity>): String {
    val ultimoId = sesiones.maxOfOrNull { it.id } ?: 0
    return "s${ultimoId + 1}"
}
