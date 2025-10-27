package com.example.coachtrack

import HistorialScreen
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

// TODAS las importaciones de pantallas deben estar aquí.

import com.example.coachtrack.PantallaPrincipal
import com.example.coachtrack.VideoAnalisisScreen
import com.example.coachtrack.PlanificacionScreen
import com.example.coachtrack.CamaraScreen
import com.example.coachtrack.CarteraScreen
import com.example.coachtrack.VideoScreen
import com.example.coachtrack.FichaAlumnoScreen
import com.example.coachtrack.MiniPlanificacionScreen
import com.example.coachtrack.SplashScreen // Asumiendo que quieres el SplashScreen animado

@Composable
fun AppNavigation(firebaseManager: FakeFirebaseManager) {
    val firestoreState by firebaseManager.firestoreState

    // 🔹 1. Si no está autenticado → mostrar pantalla de inicio (Bienvenida)
    if (!firestoreState.isAuthenticated) {
        // Se revierte a la llamada de la función Composable SplashScreen
        SplashScreen(
            onSplashFinished = {
                firebaseManager.loginDemo() // Simula el login después del splash
            }
        )
        return
    }

    // 🔹 2. Si ya está autenticado → continuar con la navegación normal
    // ✅ USO DE LA RUTA COMPLETA PARA EVITAR AMBIGÜEDAD (com.example.coachtrack.Screen)
    var currentScreen by remember { mutableStateOf<com.example.coachtrack.Screen>(Screen.Principal) }

    when (currentScreen) {
        // ✅ USO DE LA RUTA COMPLETA EN CADA "is"
        is com.example.coachtrack.Screen.Principal -> {
            PantallaPrincipal(
                onPlanificarClick = { currentScreen = Screen.Planificacion },
                onHistorialClick = { currentScreen = Screen.Historial },
                onVideoAnalisisClick = { currentScreen = Screen.VideoAnalisis },
                userId = firestoreState.userId,
                firebaseManager = firebaseManager
            )
        }

        is com.example.coachtrack.Screen.Planificacion -> {
            PlanificacionScreen(
                onVolverClick = { currentScreen = Screen.Principal }
            )
        }

        is com.example.coachtrack.Screen.Historial -> {
            HistorialScreen(
                onVolverClick = { currentScreen = Screen.Principal }
            )
        }

        is com.example.coachtrack.Screen.VideoAnalisis -> {
            VideoAnalisisScreen(
                onVolverClick = { currentScreen = Screen.Principal }
            )
        }
        is com.example.coachtrack.Screen.Camara -> {
            CamaraScreen(
                onVolverClick = { currentScreen = Screen.Principal }
            )
        }

        is com.example.coachtrack.Screen.Cartera -> {
            CarteraScreen(
                onVolver = { currentScreen = Screen.Principal },
                onAbrirFichaAlumno = { alumno ->
                    currentScreen = Screen.FichaAlumno(alumno)
                }
            )
        }

        is com.example.coachtrack.Screen.Video -> {
            VideoScreen(
                onVolver = { currentScreen = Screen.Principal }
            )
        }

        is com.example.coachtrack.Screen.FichaAlumno -> {
            val alumno = (currentScreen as Screen.FichaAlumno).alumno
            // Se usa el nombre de función de la pantalla.
            FichaAlumnoScreen(
                alumnoInicial = alumno,
                onVolver = { currentScreen = Screen.Cartera },
                onNuevaSesionClick = { alumnoSeleccionado ->
                    currentScreen = Screen.MiniPlanificacion(alumnoSeleccionado)
                }
            )
        }

        is com.example.coachtrack.Screen.MiniPlanificacion -> {
            val alumno = (currentScreen as Screen.MiniPlanificacion).alumno
            // Se usa el nombre de función de la pantalla.
            MiniPlanificacionScreen(
                alumno = alumno,
                onVolver = { currentScreen = Screen.FichaAlumno(alumno) }
            )
        }

        // -------------------- SPLASH DE BIENVENIDA --------------------
        is com.example.coachtrack.Screen.Inicio -> {
            SplashScreen(
                onSplashFinished = {
                    currentScreen = Screen.Principal
                }
            )
        }
    }
}