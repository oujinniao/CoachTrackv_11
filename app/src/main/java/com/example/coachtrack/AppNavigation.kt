package com.example.coachtrack

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AppNavigation(firebaseManager: FakeFirebaseManager) {
    val firestoreState by firebaseManager.firestoreState

    // 🔹 1. Si no está autenticado → mostrar pantalla de inicio (Bienvenida)
    if (!firestoreState.isAuthenticated) {
        SplashScreen(
            onSplashFinished = {
                firebaseManager.loginDemo() // Simula el login después del splash
            }
        )
        return
    }

    // 🔹 2. Si ya está autenticado → continuar con la navegación normal
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Principal) }

    when (currentScreen) {

        // -------------------- PANTALLA PRINCIPAL --------------------
        is Screen.Principal -> {
            PantallaPrincipal(
                onPlanificarClick = { currentScreen = Screen.Planificacion },
                onHistorialClick = { currentScreen = Screen.Historial },
                onVideoAnalisisClick = { currentScreen = Screen.VideoAnalisis },
                userId = firestoreState.userId,
                firebaseManager = firebaseManager // necesario para logout
            )
        }

        // -------------------- PLANIFICACIÓN --------------------
        is Screen.Planificacion -> {
            PlanificacionScreen(
                onVolverClick = { currentScreen = Screen.Principal }
            )
        }

        // -------------------- HISTORIAL --------------------
        is Screen.Historial -> {
            HistorialScreen(
                onVolverClick = { currentScreen = Screen.Principal }
            )
        }

        // -------------------- VIDEO ANÁLISIS --------------------
        is Screen.VideoAnalisis -> {
            VideoAnalisisScreen(
                onVolverClick = { currentScreen = Screen.Principal }
            )
        }

        // -------------------- CÁMARA --------------------
        is Screen.Camara -> {
            CamaraScreen(
                onVolverClick = { currentScreen = Screen.Principal }
            )
        }

        // -------------------- CARTERA --------------------
        is Screen.Cartera -> {
            CarteraScreen(
                onVolver = { currentScreen = Screen.Principal },
                onAbrirFichaAlumno = { alumno ->
                    currentScreen = Screen.FichaAlumno(alumno)
                }
            )
        }

        // -------------------- VIDEO --------------------
        is Screen.Video -> {
            VideoScreen(
                onVolver = { currentScreen = Screen.Principal }
            )
        }

        // -------------------- FICHA DEL ALUMNO --------------------
        is Screen.FichaAlumno -> {
            val alumno = (currentScreen as Screen.FichaAlumno).alumno
            FichaAlumnoScreen(
                alumnoInicial = alumno,
                onVolver = { currentScreen = Screen.Cartera },
                onNuevaSesionClick = { alumnoSeleccionado ->
                    currentScreen = Screen.MiniPlanificacion(alumnoSeleccionado)
                }
            )
        }

        // -------------------- MINI PLANIFICACIÓN --------------------
        is Screen.MiniPlanificacion -> {
            val alumno = (currentScreen as Screen.MiniPlanificacion).alumno
            MiniPlanificacionScreen(
                alumno = alumno,
                onVolver = { currentScreen = Screen.FichaAlumno(alumno) }
            )
        }

        // -------------------- SPLASH DE BIENVENIDA --------------------
        is Screen.Inicio -> {
            SplashScreen(
                onSplashFinished = {
                    currentScreen = Screen.Principal
                }
            )
        }
    }
}
