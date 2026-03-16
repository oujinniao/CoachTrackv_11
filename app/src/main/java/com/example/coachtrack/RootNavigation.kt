package com.example.coachtrack

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

object RootRoutes {
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val MAIN = "main"
}

@Composable
fun RootNavigation(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val auth = Firebase.auth
    val carteraViewModel: CarteraViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = RootRoutes.SPLASH,
        modifier = modifier
    ) {

        composable(RootRoutes.SPLASH) {
            CoachTrackSplashScreen(
                onSplashFinished = {
                    val destino = if (auth.currentUser != null) RootRoutes.MAIN
                    else RootRoutes.LOGIN

                    navController.navigate(destino) {
                        popUpTo(RootRoutes.SPLASH) { inclusive = true }
                    }
                }
            )
        }

        composable(RootRoutes.LOGIN) {
            LoginScreen(
                carteraViewModel = carteraViewModel,
                onLoginSuccess = {
                    navController.navigate(RootRoutes.MAIN) {
                        popUpTo(RootRoutes.LOGIN) { inclusive = true }
                    }
                },
                onRegisterClick = {
                    navController.navigate(RootRoutes.REGISTER)
                }
            )
        }

        composable(RootRoutes.REGISTER) {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(RootRoutes.MAIN) {
                        popUpTo(RootRoutes.LOGIN) { inclusive = true }
                    }
                },
                onBackToLogin = {
                    navController.popBackStack()
                }
            )
        }

        composable(RootRoutes.MAIN) {
            AppNavigation(
                carteraViewModel = carteraViewModel,
                onLogout = {
                    Firebase.auth.signOut()
                    navController.navigate(RootRoutes.LOGIN) {
                        popUpTo(RootRoutes.MAIN) { inclusive = true }
                    }
                }
            )
        }
    }
}