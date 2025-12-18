package com.example.coachtrack

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AppNavigation(
    carteraViewModel: CarteraViewModel,
    onLogout: () -> Unit
) {
    // currentScreen puede ser un objeto (e.g., AppScreen.Cartera) o una data class con datos (e.g., AppScreen.FichaAlumno(123L))
    var currentScreen by remember { mutableStateOf<AppScreen>(AppScreen.Principal) }

    AnimatedContent(
        targetState = currentScreen,
        transitionSpec = {
            fadeIn(animationSpec = androidx.compose.animation.core.tween(600)) togetherWith
                    fadeOut(animationSpec = androidx.compose.animation.core.tween(400))
        },
        label = "pantalla-transition"
    ) { screen ->

        when (screen) {

            AppScreen.Splash -> CoachTrackSplashScreen(
                onSplashFinished = { currentScreen = AppScreen.Principal }
            )

            AppScreen.Principal -> PantallaPrincipal(
                onPlanificarClick = { currentScreen = AppScreen.Planificacion },
                onGestionClick = { currentScreen = AppScreen.Gestion },
                onVideoAnalisisClick = { currentScreen = AppScreen.VideoAnalisis },
                onDashboardClick = { currentScreen = AppScreen.Dashboard },
                userId = "demo_user",
                onCerrarSesion = {
                    onLogout()
                }
            )
            AppScreen.Gestion -> GestionMenuScreen(
                onVolverClick = { currentScreen = AppScreen.Principal },
                onGestionAlumnosClick = { currentScreen = AppScreen.Cartera },
                onHistorialAlumnosClick = { currentScreen = AppScreen.Historial },
                onGestionProfesoresClick = { currentScreen = AppScreen.GestionProfesores },
                onPagosClick = { currentScreen = AppScreen.Pagos }
            )

            AppScreen.GestionProfesores -> GestionProfesoresScreen(
                onVolverClick = { currentScreen = AppScreen.Gestion }
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
                onVolver = { currentScreen = AppScreen.Gestion },
               onAbrirFichaAlumno = { alumnoEntity ->
                    currentScreen = AppScreen.FichaAlumno(alumnoEntity.localId)
                }
            )

            AppScreen.Pagos -> PagosScreen(
                onVolver = { currentScreen = AppScreen.Gestion }
            )
            AppScreen.Dashboard -> DashboardScreen(
                onVolverClick = { currentScreen = AppScreen.Principal },
                onPlanificacionClick = { currentScreen = AppScreen.Planificacion },
                onCarteraClick = { currentScreen = AppScreen.Cartera },
                onAbrirFichaAlumno = { alumno ->
                    currentScreen = AppScreen.FichaAlumno(alumno.localId)
                },
                onGestionClick = { currentScreen = AppScreen.Gestion }
            )

            is AppScreen.FichaAlumno -> FichaAlumnoScreen(
                localId = screen.localId, // <- Ahora resuelve correctamente
                onVolver = {
                    currentScreen = AppScreen.Cartera // Volver a la Cartera
                },
                onNuevaSesionClick = { /* futuro: ir a la pantalla de sesión */ }
            )

            // Manejo de otros estados que no necesitan argumentos
            AppScreen.Video -> Box {}
            AppScreen.Camara -> Box {}
            AppScreen.Inicio -> Box {}

            // Caso por defecto
            else -> CoachTrackSplashScreen(
                onSplashFinished = { currentScreen = AppScreen.Principal }
            )
        }
    }
}