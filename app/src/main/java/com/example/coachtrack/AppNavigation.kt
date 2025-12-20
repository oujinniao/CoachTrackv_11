package com.example.coachtrack

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.*

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AppNavigation(
    carteraViewModel: CarteraViewModel,
    onLogout: () -> Unit
) {
    var currentScreen by remember { mutableStateOf<AppScreen>(AppScreen.Principal) }
    val backStack = remember { mutableStateListOf<AppScreen>() }

    fun navigate(to: AppScreen) {
        backStack.add(currentScreen)
        currentScreen = to
    }

    fun goBack(fallback: AppScreen = AppScreen.Principal) {
        currentScreen = if (backStack.isNotEmpty()) backStack.removeAt(backStack.lastIndex) else fallback
    }

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
                onPlanificarClick = { navigate(AppScreen.Planificacion) },
                onGestionClick = { navigate(AppScreen.Gestion) },
                onVideoAnalisisClick = { navigate(AppScreen.VideoAnalisis) },
                onDashboardClick = { navigate(AppScreen.Dashboard) },
                userId = "demo_user",
                onCerrarSesion = { onLogout() }
            )

            AppScreen.Gestion -> GestionMenuScreen(
                onVolverClick = { goBack(AppScreen.Principal) },
                onGestionAlumnosClick = { navigate(AppScreen.Cartera) },
                onHistorialAlumnosClick = { navigate(AppScreen.Historial) },
                onGestionProfesoresClick = { navigate(AppScreen.GestionProfesores) },
                onPagosClick = { navigate(AppScreen.Pagos) }
            )

            AppScreen.GestionProfesores -> GestionProfesoresScreen(
                onVolverClick = { goBack(AppScreen.Gestion) }
            )

            AppScreen.Planificacion -> PlanificacionScreen(
                onVolverClick = { goBack(AppScreen.Principal) }
            )

            AppScreen.Historial -> HistorialScreen(
                onVolverClick = { goBack(AppScreen.Gestion) }
            )

            AppScreen.VideoAnalisis -> VideoAnalisisScreen(
                onVolverClick = { goBack(AppScreen.Principal) }
            )

            AppScreen.Cartera -> CarteraScreen(
                onVolver = { goBack(AppScreen.Gestion) },
                onAbrirFichaAlumno = { alumnoEntity ->
                    navigate(AppScreen.FichaAlumno(alumnoEntity.localId))
                }
            )

            AppScreen.Pagos -> PagosScreen(
                onVolver = { goBack(AppScreen.Gestion) }
            )

            AppScreen.Dashboard -> DashboardScreen(
                onVolverClick = { goBack(AppScreen.Principal) },
                onPlanificacionClick = { navigate(AppScreen.Planificacion) },
                onCarteraClick = { navigate(AppScreen.Cartera) },
                onAbrirFichaAlumno = { alumno ->
                    navigate(AppScreen.FichaAlumno(alumno.localId))
                },
                onGestionClick = { navigate(AppScreen.Gestion) }
            )

            is AppScreen.FichaAlumno -> FichaAlumnoScreen(
                localId = screen.localId,
                onVolver = { goBack(AppScreen.Cartera) },
                onNuevaSesionClick = { /* futuro */ }
            )

            AppScreen.Video -> Box {}
            AppScreen.Camara -> Box {}
            AppScreen.Inicio -> Box {}

            else -> CoachTrackSplashScreen(
                onSplashFinished = { currentScreen = AppScreen.Principal }
            )
        }
    }
}
