package com.example.coachtrack

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf

/**
 * Estado simulado de autenticación y sesión (modo offline).
 */

/**
 * Manager falso para demo offline.
 * Simula el comportamiento básico de FirebaseAuth + Firestore.
 */
class FakeFirebaseManager {

    private val _firestoreState = mutableStateOf(FirestoreState())
    val firestoreState: State<FirestoreState> = _firestoreState

    // Simula la conexión a Firebase
    val db: Any? = null
    val appId: String = "offline-demo-app"

    /**
     * Simula el inicio de sesión del usuario (profesor).
     */
    fun loginDemo() {
        _firestoreState.value = FirestoreState(
            isAuthenticated = true,
            userId = "offline-demo-user"
        )
        println("✅ Usuario autenticado en modo offline.")
    }

    /**
     * Simula el cierre de sesión del usuario.
     */
    fun logout() {
        _firestoreState.value = FirestoreState(
            isAuthenticated = false,
            userId = ""
        )
        println("🚪 Sesión cerrada. Volviendo a la pantalla de inicio.")
    }
}
