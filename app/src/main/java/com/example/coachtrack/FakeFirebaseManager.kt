package com.example.coachtrack

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf

// ✅ Le cambiamos el nombre para que no choque con la clase real FirestoreState
data class FakeFirestoreState(
    val isAuthenticated: Boolean = true,
    val userId: String = "offline-demo-user"
)

/**
 * Manager falso para demo offline.
 * Simula que Firebase ya está inicializado y autenticado.
 */
class FakeFirebaseManager {
    private val _firestoreState = mutableStateOf(FakeFirestoreState())
    val firestoreState: State<FakeFirestoreState> = _firestoreState

    // db no existe, lo dejamos como null
    val db: Any? = null
    val appId: String = "offline-demo-app"
}
//