import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.coachtrack.AppNavigation
import com.example.coachtrack.LoginScreen
import com.example.coachtrack.RegisterScreen
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

@Composable
fun RootNavigation(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val auth = Firebase.auth

    // Durante el desarrollo, siempre partimos en login
    val startDestination = "login"

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

        composable("main") {
            AppNavigation(
                onLogout = {
                    // Cerrar sesión en Firebase
                    auth.signOut()
                    // Volver al login y limpiar el back stack
                    navController.navigate("login") {
                        popUpTo("main") { inclusive = true }
                    }
                }
            )
        }
    }
}
