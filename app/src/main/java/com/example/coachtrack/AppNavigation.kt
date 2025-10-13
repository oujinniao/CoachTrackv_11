package com.example.coachtrack

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

// Definición de las pantallas (Asegúrate de que este enum esté en Data.kt o similar)
// Si esta clase no está en AppNavigation.kt, deberás importarla.
// enum class Screen { Principal, Planificacion, Historial, VideoAnalisis, Camara, Cartera, Video }

@Composable
fun AppNavigation(firebaseManager: FakeFirebaseManager) {
    val firestoreState by firebaseManager.firestoreState

    // Pantalla de carga mientras se "autentica"
    if (!firestoreState.isAuthenticated) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
            Text("Cargando CoachTrack...", modifier = Modifier.padding(top = 80.dp))
        }
        return
    }

    // Control de navegación entre pantallas
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Principal) }

    when (currentScreen) {
        is Screen.Principal -> {
            PantallaPrincipal(
                onPlanificarClick = { currentScreen = Screen.Planificacion },
                onHistorialClick = { currentScreen = Screen.Historial },
                onVideoAnalisisClick = { currentScreen = Screen.VideoAnalisis },
                userId = firestoreState.userId
            )
        }

        is Screen.Planificacion -> {
            // FIX: Se cambió el parámetro a 'onVolver' para coincidir con la definición de PlanificacionScreen
            PlanificacionScreen(
                onVolverClick = { currentScreen = Screen.Principal }
            )
        }

        is Screen.Historial -> {
            // FIX: Se cambió el parámetro a 'onVolver' para coincidir con la definición de HistorialScreen
            HistorialScreen(
                onVolverClick = { currentScreen = Screen.Principal }
            )
        }

        is Screen.VideoAnalisis -> {
            // FIX: Se cambió el parámetro a 'onVolver' para coincidir con la definición de VideoAnalisisScreen
            VideoAnalisisScreen(
                onVolverClick = { currentScreen = Screen.Principal }
            )
        }
        is Screen.Camara -> {
            // FIX: Se cambió el parámetro a 'onVolver' para coincidir con la definición de CamaraScreen
            CamaraScreen(
                onVolverClick = { currentScreen = Screen.Principal }
            )
        }

        is Screen.Cartera -> {
            // Llama a CarteraScreen con el parámetro 'onVolver'
            CarteraScreen(
                onVolver = { currentScreen = Screen.Principal }
            )
        }

        is Screen.Video -> {
            // Llama a VideoScreen con el parámetro 'onVolver'
            VideoScreen(
                onVolver = { currentScreen = Screen.Principal }
            )
        }
    }
}
