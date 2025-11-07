package com.example.coachtrack

import androidx.compose.runtime.*

@Composable
fun AppNavigation() {
    // ✅ VERSIÓN SIN FIREBASEMANAGER
    var currentScreen by remember { mutableStateOf<AppScreen>(AppScreen.Principal) }

    when (currentScreen) {
        AppScreen.Principal -> PantallaPrincipal(
            onPlanificarClick = { currentScreen = AppScreen.Planificacion },
            onHistorialClick = { currentScreen = AppScreen.Historial },
            onVideoAnalisisClick = { currentScreen = AppScreen.VideoAnalisis },
            onCarteraClick = { currentScreen = AppScreen.Cartera },
            userId = "demo_user" // ✅ SIN firebaseManager parameter
        )

        AppScreen.Planificacion -> PlanificacionScreen(
            onVolverClick = { currentScreen = AppScreen.Principal }
        )

        AppScreen.Historial -> HistorialScreen(
            onVolverClick = { currentScreen = AppScreen.Principal }
        )

        AppScreen.VideoAnalisis -> VideoAnalisisScreen(
            onVolverClick = { currentScreen = AppScreen.Principal }
        )

        AppScreen.Cartera -> CarteraScreen(
            onVolver = { currentScreen = AppScreen.Principal },
            onAbrirFichaAlumno = { /* temporalmente vacío */ }
        )

        else -> PantallaPrincipal(
            onPlanificarClick = { currentScreen = AppScreen.Planificacion },
            onHistorialClick = { currentScreen = AppScreen.Historial },
            onVideoAnalisisClick = { currentScreen = AppScreen.VideoAnalisis },
            onCarteraClick = { currentScreen = AppScreen.Cartera },
            userId = "demo_user" // ✅ SIN firebaseManager parameter
        )
    }
}