package com.example.coachtrack

import androidx.compose.runtime.mutableStateOf

data class FirestoreState(
    val isAuthenticated: Boolean = false,
    val userId: String = "",
    val errorMessage: String? = null
)

class FakeFirebaseManager {
    // ✅ Debe ser así - sin .value al acceder
    val firestoreState = mutableStateOf(
        FirestoreState(
            isAuthenticated = false,
            userId = ""
        )
    )

    fun loginDemo() {
        firestoreState.value = FirestoreState(
            isAuthenticated = true,
            userId = "offline-demo-user"
        )
    }

    fun logout() {
        firestoreState.value = FirestoreState(
            isAuthenticated = false,
            userId = ""
        )
    }
}