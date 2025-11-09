package com.example.coachtrack

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.*

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AppNavigation() {

    // 🌀 Empezamos desde el Splash animado
    var currentScreen by remember { mutableStateOf<AppScreen>(AppScreen.Splash) }

    // 🎬 Animación entre pantallas
    AnimatedContent(
        targetState = currentScreen,
        transitionSpec = {
            fadeIn(animationSpec = androidx.compose.animation.core.tween(600)) togetherWith
                    fadeOut(animationSpec = androidx.compose.animation.core.tween(400))
        },
        label = "pantalla-transition"
    ) { screen ->

        when (screen) {

            // 🌊 SplashScreen animado
            AppScreen.Splash -> SplashScreen(
                onSplashFinished = { currentScreen = AppScreen.Principal }
            )

            // 🏠 Pantalla principal con botones
            AppScreen.Principal -> PantallaPrincipal(
                onPlanificarClick = { currentScreen = AppScreen.Planificacion },
                onHistorialClick = { currentScreen = AppScreen.Historial },
                onVideoAnalisisClick = { currentScreen = AppScreen.VideoAnalisis },
                onCarteraClick = { currentScreen = AppScreen.Cartera },
                userId = "demo_user",
                onCerrarSesion = {
                    // 🔙 Al cerrar sesión, vuelve al Splash animado
                    currentScreen = AppScreen.Splash
                }
            )

            // 📋 PLANIFICACIÓN
            AppScreen.Planificacion -> PlanificacionScreen(
                onVolverClick = { currentScreen = AppScreen.Principal }
            )

            // 🧾 HISTORIAL DE SESIONES
            AppScreen.Historial -> HistorialScreen(
                onVolverClick = { currentScreen = AppScreen.Principal }
            )

            // 🎥 ANÁLISIS EN VIDEO
            AppScreen.VideoAnalisis -> VideoAnalisisScreen(
                onVolverClick = { currentScreen = AppScreen.Principal }
            )

            // 💰 CARTERA DE ALUMNOS (con su Dashboard interno)
            AppScreen.Cartera -> CarteraScreen(
                onVolver = { currentScreen = AppScreen.Principal },
                onAbrirFichaAlumno = { /* temporal */ }
            )

            // 🚫 fallback
            else -> SplashScreen(
                onSplashFinished = { currentScreen = AppScreen.Principal }
            )
        }
    }
}
