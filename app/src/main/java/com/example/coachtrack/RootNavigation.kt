package com.example.coachtrack

import RegisterScreen
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

@Composable
fun RootNavigation(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val auth = Firebase.auth

    // Si ya hay usuario (email o anónimo) → ir directo al main
    val startDestination = if (auth.currentUser == null) "login" else "main"

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable("login") {
            LoginScreen(
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

        // Tu navegación actual
        composable("main") {
            AppNavigation()   // 👈 deja tu AppNavigation tal como está
        }
    }
}
