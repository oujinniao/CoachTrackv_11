package com.example.coachtrack

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.coachtrack.CamaraScreen
import com.example.coachtrack.CarteraScreen
import com.example.coachtrack.VideoScreen


// Importamos las pantallas
import com.example.coachtrack.PantallaPrincipal
import com.example.coachtrack.PlanificacionScreen
import com.example.coachtrack.HistorialScreen
import com.example.coachtrack.VideoAnalisisScreen

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
            PlanificacionScreen(
                onVolverClick = { currentScreen = Screen.Principal }
            )
        }

        is Screen.Historial -> {
            HistorialScreen(
                onVolverClick = { currentScreen = Screen.Principal }
            )
        }

        is Screen.VideoAnalisis -> {
            VideoAnalisisScreen(
                onVolverClick = { currentScreen = Screen.Principal }
            )

        }
        is Screen.Camara -> {
            CamaraScreen(
                onVolverClick = { currentScreen = Screen.Principal }
            )
        }

        is Screen.Cartera -> {
            CarteraScreen(
                onVolver = { currentScreen = Screen.Principal }
            )
        }

        is Screen.Video -> {
            VideoScreen(
                onVolver = { currentScreen = Screen.Principal }
            )
        }
    }
}
