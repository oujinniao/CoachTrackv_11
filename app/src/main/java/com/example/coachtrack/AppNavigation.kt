package com.example.coachtrack

import androidx.activity.compose.BackHandler
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
        currentScreen =
            if (backStack.isNotEmpty()) backStack.removeAt(backStack.lastIndex)
            else fallback
    }

    // ✅ Back físico SIEMPRE usa tu stack.
    BackHandler(enabled = backStack.isNotEmpty()) {
        goBack()
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

            AppScreen.Principal -> PantallaPrincipal(
                onPlanificarClick = { navigate(AppScreen.Planificacion) },
                onGestionClick = { navigate(AppScreen.Gestion) },
                onVideoAnalisisClick = { navigate(AppScreen.VideoAnalisis) },
                onDashboardClick = { navigate(AppScreen.Dashboard) },
                userId = "demo_user",
                onCerrarSesion = { onLogout() }
            )

            AppScreen.Gestion -> GestionMenuScreen(
                onVolverClick = { goBack() },
                onGestionAlumnosClick = { navigate(AppScreen.Cartera) },
                onHistorialAlumnosClick = { navigate(AppScreen.Historial) },
                onGestionProfesoresClick = { navigate(AppScreen.GestionProfesores) },
                onPagosClick = { navigate(AppScreen.Pagos) }
            )

            AppScreen.GestionProfesores -> GestionProfesoresScreen(
                onVolverClick = { goBack() }
            )

            AppScreen.Planificacion -> PlanificacionScreen(
                onVolverClick = { goBack() } // ✅ vuelve a Dashboard si venías de Dashboard
            )

            AppScreen.Historial -> HistorialScreen(
                onVolverClick = { goBack() }
            )

            AppScreen.VideoAnalisis -> VideoAnalisisScreen(
                onVolverClick = { goBack() }
            )

            AppScreen.Cartera -> CarteraScreen(
                onVolver = { goBack() },
                onAbrirFichaAlumno = { alumno ->
                    navigate(AppScreen.FichaAlumno(alumno.localId))
                }
            )

            AppScreen.Pagos -> PagosScreen(
                onVolver = { goBack() }
            )

            AppScreen.Dashboard -> DashboardScreen(
                onVolverClick = { goBack() },
                onPlanificacionClick = { navigate(AppScreen.Planificacion) },
                onCarteraClick = { navigate(AppScreen.Cartera) },
                onAbrirFichaAlumno = { alumno ->
                    navigate(AppScreen.FichaAlumno(alumno.localId))
                },
                onGestionClick = { navigate(AppScreen.Gestion) }
            )

            is AppScreen.FichaAlumno -> FichaAlumnoScreen(
                localId = screen.localId,
                onVolver = { goBack() }, // ✅ vuelve a Dashboard o Cartera según desde dónde entraste
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
