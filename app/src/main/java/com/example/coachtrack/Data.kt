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

data class Sesion(
    val id: String,          // identificador interno (sessionId en Data)
    val titulo: String,      // nombre o tema de la sesión
    val fecha: String,       // fecha de realización
    val duracionMin: Int,    // duración en minutos
    val estado: String       // estado (Completada, Pendiente, etc.)
)

// ------------------ CONVERSOR ENTRE TIPOS ------------------

fun SesionDeClase.toSesion(): Sesion {
    return Sesion(
        id = sessionId,
        titulo = ejercicios.firstOrNull()?.nombre ?: "Sesión sin título",
        fecha = fechaCreacion,
        duracionMin = duracionTotalMinutos,
        estado = "Completada"
    )
}

// ------------------ MOCK DATA Y HELPERS ------------------

fun getMockPlantillas(): List<Plantilla> = listOf(
    Plantilla(
        id = "p1",
        nombre = "Rutina de Calentamiento",
        enfoque = "Prevención y Activación",
        duracionMinutos = 10,
        descripcion = "Secuencia dinámica para aumentar la temperatura corporal y preparar las articulaciones.",
        imageUrl = "android.resource://com.example.coachtrack/drawable/calentamiento"
    ),
    Plantilla(
        id = "p2",
        nombre = "Drill de Saque Básico",
        enfoque = "Técnica Fundamental",
        duracionMinutos = 30,
        descripcion = "Ejercicio centrado en la mecánica del lanzamiento de bola (Toss) y el punto de impacto.",
        imageUrl = "https://placehold.co/400x200/2196F3/white?text=IMAGEN+DRILL+SAQUE"
    ),
    Plantilla(
        id = "p3",
        nombre = "Juego de Pies en la Red",
        enfoque = "Movimiento y Agilidad",
        duracionMinutos = 20,
        descripcion = "Rutina de agilidad cerca de la red para mejorar reacción y desplazamiento.",
        imageUrl = "https://placehold.co/400x200/FFC107/black?text=IMAGEN+PIES+RED"
    ),
    Plantilla(
        id = "p4",
        nombre = "Ejercicios de Cierre (Cool Down)",
        enfoque = "Recuperación y Flexibilidad",
        duracionMinutos = 15,
        descripcion = "Estiramientos estáticos para bajar el ritmo y relajar músculos tras la sesión.",
        imageUrl = "https://placehold.co/400x200/9C27B0/white?text=IMAGEN+CIERRE"
    )
)

val PLANTILLAS_MOCK = getMockPlantillas()

// ------------------ SESIONES GLOBALES ------------------

val SESIONES_GUARDADAS = mutableListOf(
    SesionDeClase(
        sessionId = "s1",
        userId = "PROTOTIPO_DEMO",
        fechaCreacion = "2025-10-01 10:00",
        alumnoNombre = "Juan Pérez",
        duracionTotalMinutos = 40,
        ejercicios = mutableListOf(
            PLANTILLAS_MOCK.first { it.id == "p1" },
            PLANTILLAS_MOCK.first { it.id == "p3" }
        )
    )
)

fun getMockSesionesGuardadas(): MutableList<SesionDeClase> = SESIONES_GUARDADAS

// ------------------ ALUMNOS MOCK ------------------

fun getMockAlumnos(): List<Alumnos> {
    val sesionesJuan = getMockSesionesGuardadas()
        .filter { it.alumnoNombre == "Juan Pérez" }
        .map { it.toSesion() }

    return listOf(
        Alumnos(
            id = "a1",
            nombre = "Juan Pérez",
            nivel = "Intermedio",
            objetivo = "Estabilidad del tobillo",
            clasesPactadas = 10,
            clasesCursadas = 8,
            estadoPago = EstadoPago.ADELANTADO,
            notasEntrenador = "Trabajar en la estabilidad del tobillo izquierdo.",
            sesiones = sesionesJuan
        ),
        Alumnos(
            id = "a2",
            nombre = "María García",
            nivel = "Avanzado",
            objetivo = "Mejorar el Toss",
            clasesPactadas = 5,
            clasesCursadas = 5,
            estadoPago = EstadoPago.ADELANTADO,
            notasEntrenador = "Mucha motivación, pero necesita centrarse en el Toss."
        ),
        Alumnos(
            id = "a3",
            nombre = "Carlos Gómez",
            nivel = "Intermedio",
            objetivo = "Ser más constante",
            clasesPactadas = 12,
            clasesCursadas = 1,
            estadoPago = EstadoPago.DEUDA,
            notasEntrenador = "No ha pagado el último mes. Contactar urgente."
        ),
        Alumnos(
            id = "a4",
            nombre = "Laura Fernández",
            nivel = "Inicial",
            objetivo = "Reforzar técnica de revés",
            clasesPactadas = 8,
            clasesCursadas = 3,
            estadoPago = EstadoPago.PENDIENTE,
            notasEntrenador = "Pendiente de respuesta para la renovación de paquete."
        )
    )
}

// ------------------ HELPERS ------------------

fun generarNuevoSessionId(sesiones: List<SesionDeClase>): String {
    val ultimoId = sesiones.maxOfOrNull { it.sessionId.removePrefix("s").toIntOrNull() ?: 0 } ?: 0
    return "s${ultimoId + 1}"
}
