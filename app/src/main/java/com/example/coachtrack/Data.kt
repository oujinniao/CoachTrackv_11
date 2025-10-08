package com.example.coachtrack

import androidx.compose.runtime.mutableStateListOf

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

// Inicializa el historial con la primera sesión predefinida
fun getMockSesionesGuardadas(): MutableList<SesionDeClase> {
    return mutableStateListOf(
        SesionDeClase(
            sessionId = "s1",
            userId = "PROTOTIPO_DEMO",
            fecha = "2024-10-07",
            alumno = "Juan Pérez",
            duracionTotalMinutos = 40,
            ejercicios = mutableStateListOf(
                getMockPlantillas().first { it.id == "p1" }, // Rutina Calentamiento
                getMockPlantillas().first { it.id == "p3" }  // Juego de Pies
            )
        )
    )
}

// Estructuras de Datos

data class Plantilla(
    val id: String,
    val nombre: String,
    val enfoque: String,
    val duracionMinutos: Int,
    val descripcion: String, // Nuevo campo para el broucher
    val imageUrl: String      // Nuevo campo para simular imagen/video
) {
    // Función de extensión para convertir la plantilla a un Mapa que Firestore pueda guardar.
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
    val userId: String,
    val fecha: String,
    val alumno: String,
    val duracionTotalMinutos: Int,
    val ejercicios: MutableList<Plantilla>
) {
    // Función de extensión para convertir la SesiónDeClase a un Mapa.
    fun toMap(): Map<String, Any> {
        return mapOf(
            "userId" to userId,
            "fecha" to fecha,
            "alumno" to alumno,
            "duracionTotalMinutos" to duracionTotalMinutos,
            "ejercicios" to ejercicios.map { it.toMap() } // Convierte la lista de Plantillas a lista de Mapas
        )
    }

}

val PLANTILLAS_MOCK = getMockPlantillas()