package com.example.coachtrack

import java.util.UUID

// ------------------ ESTRUCTURAS DE DATOS BASE ------------------

data class Plantilla(
    val id: String,
    val nombre: String,
    val enfoque: String,
    val duracionMinutos: Int,
    val descripcion: String,
    val imageUrl: String,
    val instanceId: String = UUID.randomUUID().toString(),
    val esPersonalizado: Boolean = false // 🔑 NUEVO: Indica si fue creado por el usuario
) {
    fun toMap(): Map<String, Any> = mapOf(
        "id" to id,
        "nombre" to nombre,
        "enfoque" to enfoque,
        "duracionMinutos" to duracionMinutos,
        "descripcion" to descripcion,
        "imageUrl" to imageUrl,
        "esPersonalizado" to esPersonalizado // Incluir en el mapeo
    )
}

// ------------------ SESIÓN COMPLETA (para planificación) ------------------

data class SesionDeClase(
    val sessionId: Long,
    val userId: String = "PROTOTIPO_DEMO",
    val fechaCreacion: String,
    val alumnoNombre: String,
    val duracionTotalMinutos: Int,
    val ejercicios: MutableList<Plantilla>,
    val sePuedeCompartir: Boolean = true // Flag para funcionalidad de compartir
) {
    fun toMap(): Map<String, Any> = mapOf(
        "userId" to userId,
        "fecha" to fechaCreacion,
        "alumno" to alumnoNombre,
        "duracionTotalMinutos" to duracionTotalMinutos,
        "ejercicios" to ejercicios.map { it.toMap() }
    )

    /**
     * Genera un texto formateado apto para compartir por WhatsApp o Email (Requisito del profesor).
     */
    fun generarTextoCompartir(): String {
        val listaEjercicios = ejercicios.joinToString("\n  - ") {
            "${it.nombre} (${it.duracionMinutos} min) [${it.enfoque}]"
        }

        return """
            🎾 PLANIFICACIÓN DE SESIÓN
            
            Alumno: ${this.alumnoNombre}
            Fecha/Hora: ${this.fechaCreacion}
            Duración Total: ${this.duracionTotalMinutos} minutos
            
            --- EJERCICIOS ---
            
             - $listaEjercicios
            
            -----------------
            ¡Nos vemos en la cancha! 
            (Enviado por CoachTrack)
        """.trimIndent()
    }
}

// ------------------ SESIÓN SIMPLE (para ficha de alumno) ------------------

data class SesionSimple(
    val id: String,
    val titulo: String,
    val fecha: String,
    val duracionMin: Int,
    val estado: String
)

// ------------------ CONVERSORES ------------------

fun SesionDeClase.toSesion(): SesionSimple {
    val listaEjercicios = ejercicios.joinToString(", ") { it.nombre }
    return SesionSimple(
        id = sessionId.toString(),
        titulo = "Sesión (${ejercicios.size} ejercicios): $listaEjercicios",
        fecha = fechaCreacion,
        duracionMin = duracionTotalMinutos,
        estado = "Completada"
    )
}

fun SesionDeClase.toEntity(alumnoId: Long, alumnoNombre: String): SesionEntity {
    return SesionEntity(
        alumnoId = alumnoId,
        alumnoNombre = alumnoNombre,
        fecha = fechaCreacion,
        duracion = duracionTotalMinutos,
        ejercicios = ejercicios.joinToString(", ") { it.nombre },
        notas = "",
        completada = true
    )
}

// ------------------ MOCK DATA: BIBLIOTECA EXTENDIDA (15 Plantillas de Fábrica) ------------------

fun getMockPlantillas(): List<Plantilla> = listOf(
    // 🎯 FASE 1: CALENTAMIENTO Y CIERRE
    Plantilla(
        id = "p1", nombre = "Rutina de Calentamiento Dinámico", enfoque = "Activación Muscular", duracionMinutos = 10,
        descripcion = "Secuencia de movimientos dinámicos para preparar articulaciones y músculos principales.",
        imageUrl = "android.resource://com.example.coachtrack/drawable/calentamiento"
    ),
    Plantilla(
        id = "p5", nombre = "Estiramiento Post-Sesión", enfoque = "Recuperación y Flexibilidad", duracionMinutos = 8,
        descripcion = "Estiramientos estáticos y suaves de los grupos musculares más utilizados.",
        imageUrl = "https://placehold.co/400x200/9C27B0/white?text=IMAGEN+CIERRE"
    ),

    // 🎾 FASE 2: ENFOQUE TÉCNICO
    Plantilla(
        id = "p2", nombre = "Drill de Saque Básico", enfoque = "Técnica Fundamental", duracionMinutos = 30,
        descripcion = "Repeticiones de saque con énfasis en el lanzamiento de bola y punto de impacto.",
        imageUrl = "https://placehold.co/400x200/2196F3/white?text=IMAGEN+DRILL+SAQUE"
    ),
    Plantilla(
        id = "p6", nombre = "Coordinación y Contacto de Derecha", enfoque = "Golpe de Fondo", duracionMinutos = 25,
        descripcion = "Trabajo en la cadena cinética del golpe de derecha, énfasis en el peso del cuerpo.",
        imageUrl = "https://placehold.co/400x200/4CAF50/white?text=IMAGEN+DERECHA"
    ),
    Plantilla(
        id = "p7", nombre = "Consistencia del Revés con Cesta", enfoque = "Golpe de Fondo", duracionMinutos = 25,
        descripcion = "El entrenador alimenta bolas repetitivas al revés. Objetivo: 10 bolas seguidas dentro del cuadro de servicio.",
        imageUrl = "https://placehold.co/400x200/00BCD4/white?text=IMAGEN+REVÉS"
    ),
    Plantilla(
        id = "p8", nombre = "Voleas Cortas y Anguladas", enfoque = "Juego de Red", duracionMinutos = 20,
        descripcion = "Práctica de voleas cerca de la red, variando el ángulo.",
        imageUrl = "https://placehold.co/400x200/FF5722/white?text=IMAGEN+VOLEAS"
    ),
    Plantilla(
        id = "p9", nombre = "Mecánica del Smash", enfoque = "Golpe Alto", duracionMinutos = 15,
        descripcion = "Trabajo de puntería en el smash, simulando globos.",
        imageUrl = "https://placehold.co/400x200/607D8B/white?text=IMAGEN+SMASH"
    ),

    // 🧠 FASE 3: ENFOQUE TÁCTICO
    Plantilla(
        id = "p10", nombre = "Patrón Saque-Subida", enfoque = "Táctica Ofensiva", duracionMinutos = 35,
        descripcion = "Jugar puntos donde el jugador debe subir a la red inmediatamente después del saque.",
        imageUrl = "https://placehold.co/400x200/8BC34A/black?text=IMAGEN+SAQUE+RED"
    ),
    Plantilla(
        id = "p11", nombre = "Táctica de Defensa Profunda", enfoque = "Juego Defensivo", duracionMinutos = 30,
        descripcion = "Jugar puntos forzando al alumno a defender desde 1-2 metros detrás de la línea de fondo.",
        imageUrl = "https://placehold.co/400x200/FFEB3B/black?text=IMAGEN+DEFENSA"
    ),
    Plantilla(
        id = "p12", nombre = "Puntos con Presión (40-40)", enfoque = "Manejo de Presión", duracionMinutos = 25,
        descripcion = "Jugar solo puntos decisivos (Deuce/Ventaja o Tie-breaks simulados).",
        imageUrl = "https://placehold.co/400x200/E91E63/white?text=IMAGEN+PRESIÓN"
    ),
    Plantilla(
        id = "p13", nombre = "Ataque al Revés (Forehand Inside-Out)", enfoque = "Dominio de la Derecha", duracionMinutos = 25,
        descripcion = "El jugador solo puede terminar el punto con la derecha, forzando la carrera para utilizar el 'Inside-Out'.",
        imageUrl = "https://placehold.co/400x200/03A9F4/white?text=IMAGEN+INSIDE+OUT"
    ),

    // 🏃 FASE 4: ENFOQUE FÍSICO Y MOVIMIENTO
    Plantilla(
        id = "p3", nombre = "Juego de Pies en la Red (Drill V)", enfoque = "Movimiento y Agilidad", duracionMinutos = 20,
        descripcion = "Movimiento en forma de 'V' entre la red y el fondo, con remates y voleas dirigidas.",
        imageUrl = "https://placehold.co/400x200/FFC107/black?text=IMAGEN+PIES+RED"
    ),
    Plantilla(
        id = "p14", nombre = "Drill de Conos y Velocidad", enfoque = "Resistencia Específica", duracionMinutos = 15,
        descripcion = "Carreras cortas y explosivas entre conos simulando movimientos laterales de la cancha.",
        imageUrl = "https://placehold.co/400x200/795548/white?text=IMAGEN+CONOS"
    ),
    Plantilla(
        id = "p15", nombre = "Circuito de Resistencia de 5 Bolas", enfoque = "Resistencia Aeróbica", duracionMinutos = 30,
        descripcion = "El entrenador alimenta 5 bolas en movimiento constante para trabajar resistencia en puntos.",
        imageUrl = "https://placehold.co/400x200/E53935/white?text=IMAGEN+RESISTENCIA"
    )
)

val PLANTILLAS_MOCK = getMockPlantillas()

// ------------------ NUEVO: RECURSOS PERSONALIZADOS (Mock) ------------------

val RECURSOS_PERSONALIZADOS_MOCK = mutableListOf<Plantilla>(
    Plantilla(
        id = "user_p1",
        nombre = "Mi Drill Secreto de Passing Shot",
        enfoque = "Táctica Personalizada",
        duracionMinutos = 20,
        descripcion = "Ejercicio específico para mejorar el 'passing shot' cruzado en situaciones de desventaja.",
        imageUrl = "https://placehold.co/400x200/40C4FF/black?text=RECURSO+PERSONAL",
        esPersonalizado = true // Marcado como creado por el usuario
    )
)

// ------------------ HELPERS ------------------

/**
 * Función CLAVE: Combina las plantillas de fábrica con las creadas por el usuario.
 * Esta es la lista que deberá usar el ViewModel de Planificación.
 */
fun obtenerBibliotecaCompletaDePlantillas(): List<Plantilla> {
    return PLANTILLAS_MOCK + RECURSOS_PERSONALIZADOS_MOCK.toList()
}

// ... (Resto del código, como SESIONES_GUARDADAS y generarNuevoSessionId, se mantiene)