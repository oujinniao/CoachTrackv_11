package com.example.coachtrack

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel


@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AppNavigation(
    onLogout: () -> Unit  // 👈 nuevo parámetro
) {
    var currentScreen by remember { mutableStateOf<AppScreen>(AppScreen.Splash) }

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
                userId = "demo_user",   // luego lo cambiamos por el uid real
                onCerrarSesion = {
                    // Ahora el botón "Cerrar sesión" realmente cierra la sesión
                    onLogout()
                }
            )
            AppScreen.Gestion -> GestionMenuScreen(
                onVolverClick = { currentScreen = AppScreen.Principal },
                onGestionAlumnosClick = { currentScreen = AppScreen.Cartera },
                onHistorialAlumnosClick = { currentScreen = AppScreen.Historial },
                onGestionProfesoresClick = { currentScreen = AppScreen.GestionProfesores },
                onPagosClick = {currentScreen=AppScreen.Pagos }
            )

            // ✅ NUEVO CASO DE NAVEGACIÓN AÑADIDO
            AppScreen.GestionProfesores -> GestionProfesoresScreen(
                onVolverClick = { currentScreen = AppScreen.Gestion } // Vuelve al menú de Gestión
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
                onAbrirFichaAlumno = { /* futuro */ }
            )
            AppScreen.Pagos -> PagosScreen(
                //viewModel= androidx.lifecycle.compose.viewModel(),
                onVolver = { currentScreen = AppScreen.Gestion }
            )


            else -> CoachTrackSplashScreen(
                onSplashFinished = { currentScreen = AppScreen.Principal }
            )
        }
    }
}
