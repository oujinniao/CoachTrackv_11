package com.example.coachtrack

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.*
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AppNavigation(
    carteraViewModel: CarteraViewModel,
    onLogout: () -> Unit
) {
    var currentScreen by remember { mutableStateOf<AppScreen>(AppScreen.Principal) }
    val backStack = remember { mutableStateListOf<AppScreen>() }

    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    fun navigate(to: AppScreen) {
        backStack.add(currentScreen)
        currentScreen = to
    }

    fun goBack(fallback: AppScreen = AppScreen.Principal) {
        currentScreen =
            if (backStack.isNotEmpty()) backStack.removeAt(backStack.lastIndex)
            else fallback
    }

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
                userId = userId,
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
                onVolverClick = { goBack() }
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
                onVolver = { goBack() },
                onNuevaSesionClick = { /* futuro */ }
            )

            AppScreen.Video -> goBack()
            AppScreen.Camara -> goBack()
            AppScreen.Inicio -> goBack()

            AppScreen.Splash -> CoachTrackSplashScreen(
                onSplashFinished = { currentScreen = AppScreen.Principal }
            )
        }
    }
}