package com.example.coachtrack

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import androidx.compose.ui.platform.LocalContext
import android.app.Application
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun RootNavigation(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val auth = Firebase.auth

    val context = LocalContext.current
    val application = context.applicationContext as Application

    val carteraViewModel: CarteraViewModel = viewModel(
        factory = androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
            .getInstance(application)
    )

    NavHost(
        navController = navController,
        startDestination = "splash",
        modifier = modifier
    ) {

        // 1) SPLASH PRIMERO
        composable("splash") {
            CoachTrackSplashScreen(
                onSplashFinished = {
                    val destino = if (auth.currentUser != null) {
                        // Ya hay usuario (demo o con email)
                        "main"
                    } else {
                        // No hay usuario → ir a login
                        "login"
                    }

                    navController.navigate(destino) {
                        popUpTo("splash") { inclusive = true }
                    }
                }
            )
        }

        // 2) LOGIN
        composable("login") {
            LoginScreen(
                carteraViewModel = carteraViewModel,
                onLoginSuccess = {
                    navController.navigate("main") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onRegisterClick = {
                    navController.navigate("register")
                }
            )

        }

        // 3) REGISTER
        composable("register") {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate("main") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onBackToLogin = {
                    navController.popBackStack()
                }
            )
        }

        // 4) MAIN (tu navegación interna con Splash anterior, Principal, Gestión, etc.)
        composable("main") {
            AppNavigation(
                // 🎯 3. PASAR LA DEPENDENCIA A APPNAGIGATION (que la necesitará internamente)
                carteraViewModel = carteraViewModel, // ¡Nueva dependencia requerida!
                onLogout = {
                    Firebase.auth.signOut()
                    navController.navigate("login") {
                        popUpTo("main") { inclusive = true }
                    }
                }
            )
        }
    }
}