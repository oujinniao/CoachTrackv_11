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
    val instanceId:String = UUID.randomUUID().toString() // Usamos UUID importado
) {
    fun toMap(): Map<String, Any> {
        return mapOf(
            "id" to id,
            "nombre" to nombre,
            "enfoque" to enfoque,
            "duracionMinutos" to duracionMinutos,
            "descripcion" to descripcion,
            "imageUrl" to imageUrl
        )
    }
}


data class SesionDeClase(
    val sessionId: String,
    val userId: String = "PROTOTIPO_DEMO",
    val fechaCreacion: String, // Usamos LocalDateTime para tener la fecha y hora exacta, dejamos fechaCreacion como String para simplificar
    val alumnoNombre: String,
    val duracionTotalMinutos: Int,
    val ejercicios: MutableList<Plantilla>, // Esta es una lista mutable estándar
) {
    fun toMap(): Map<String, Any> {
        return mapOf(
            "userId" to userId,
            "fecha" to fechaCreacion.toString(),
            "alumno" to alumnoNombre,
            "duracionTotalMinutos" to duracionTotalMinutos,
            "ejercicios" to ejercicios.map { it.toMap() }
        )
    }
}

// ------------------ MOCK DATA Y HELPERS ------------------

// Lista predefinida de plantillas (las bibliotecas de ejercicios)
fun getMockPlantillas(): List<Plantilla> {
    return listOf(
        Plantilla(
            id = "p1",
            nombre = "Rutina de Calentamiento",
            enfoque = "Prevención y Activación",
            duracionMinutos = 10,
            descripcion = "Secuencia dinámica para aumentar la temperatura corporal, preparar las articulaciones (tobillos, rodillas, hombros) y activar los grupos musculares principales antes de la práctica intensa. Incluye skipping, círculos de brazos y estiramientos dinámicos.",
            imageUrl = "android.resource://com.example.coachtrack/drawable/calentamiento"
        ),
        Plantilla(
            id = "p2",
            nombre = "Drill de Saque Básico",
            enfoque = "Técnica Fundamental",
            duracionMinutos = 30,
            descripcion = "Ejercicio centrado en la mecánica del lanzamiento de bola (Toss) y el punto de impacto. Se realizan 50 saques con énfasis en la fluidez del movimiento y la transferencia de peso. Ideal para corregir vicios iniciales.",
            imageUrl = "https://placehold.co/400x200/2196F3/white?text=IMAGEN+DRILL+SAQUE"
        ),
        Plantilla(
            id = "p3",
            nombre = "Juego de Pies en la Red",
            enfoque = "Movimiento y Agilidad",
            duracionMinutos = 20,
            descripcion = "Rutina de agilidad rápida cerca de la red, usando conos o marcas. Mejora la reacción y el ajuste del cuerpo para voleas y remates. Incluye pasos cruzados laterales y sprints cortos.",
            imageUrl = "https://placehold.co/400x200/FFC107/black?text=IMAGEN+PIES+RED"
        ),
        Plantilla(
            id = "p4",
            nombre = "Ejercicios de Cierre (Cool Down)",
            enfoque = "Recuperación y Flexibilidad",
            duracionMinutos = 15,
            descripcion = "Estiramientos estáticos y suaves para bajar el ritmo cardíaco y reducir la tensión muscular después del entrenamiento. Foco en isquiotibiales, cuádriceps y espalda baja. Es obligatorio para prevenir el dolor post-ejercicio.",
            imageUrl = "https://placehold.co/400x200/9C27B0/white?text=IMAGEN+CIERRE"
        )
    )
}

val PLANTILLAS_MOCK = getMockPlantillas()

fun getMockAlumnos(): List<Alumnos> {
    return listOf(
        Alumnos("a1", "Juan Pérez", 10, 8, EstadoPago.ADELANTADO, "Trabajar en la estabilidad del tobillo izquierdo."),
        Alumnos("a2", "María García", 5, 5, EstadoPago.ADELANTADO, "Mucha motivación, pero necesita centrarse en el Toss."),
        Alumnos("a3", "Carlos Gómez", 12, 1, EstadoPago.DEUDA, "No ha pagado el último mes. Contactar urgente."),
        Alumnos("a4", "Laura Fernández", 8, 3, EstadoPago.PENDIENTE, "Pendiente de respuesta para la renovación de paquete.")
    )
}

// Sesiones globales persistentes
val SESIONES_GUARDADAS = mutableListOf(
    SesionDeClase(
        sessionId = "s1",
        userId = "PROTOTIPO_DEMO",
        fechaCreacion = "2025-09-2025-10:00",
        alumnoNombre = "Juan Pérez",
        duracionTotalMinutos = 40,
        ejercicios = mutableListOf(
            PLANTILLAS_MOCK.first { it.id == "p1" },
            PLANTILLAS_MOCK.first { it.id == "p3" }
        )
    )
)

// Función segura para acceder a la misma lista
fun getMockSesionesGuardadas(): MutableList<SesionDeClase> = SESIONES_GUARDADAS



// Funcion para generar un ID simple para el Mock de sesiones
fun generarNuevoSessionId(sesiones: List<SesionDeClase>): String {
    val ultimoId = sesiones.maxOfOrNull { it.sessionId.substring(1).toIntOrNull() ?: 0 } ?: 0
    return "s${ultimoId + 1}"
}
