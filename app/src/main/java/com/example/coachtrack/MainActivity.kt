package com.example.coachtrack

import com.google.firebase.firestore.FirebaseFirestore
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.coachtrack.ui.theme.CoachTrackTheme


// Definimos las rutas (pantallas) que usaremos
sealed class Screen(val route: String) {
    object Principal : Screen("principal")
    object Planificacion : Screen("planificacion")
    object Historial : Screen("historial")
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CoachTrackTheme {
                AppNavigation()
            }
        }
    }
}

/**
 * Función central de navegación y punto de entrada que inicializa Firebase.
 */
@Composable
fun AppNavigation(
    firebaseManager: FirebaseManager = viewModel()
) {
    // Observamos el estado de FirebaseManager
    val firestoreState by firebaseManager.firestoreState

    // 1. Mostrar pantalla de carga o error mientras Firebase se inicializa/autentica
    if (!firestoreState.isAuthenticated || firebaseManager.db == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (firestoreState.errorMessage != null) {
                Text(
                    "ERROR DE CONEXIÓN: ${firestoreState.errorMessage}",
                    color = MaterialTheme.colorScheme.error,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            } else {
                CircularProgressIndicator()
                Text("Cargando CoachTrack...", modifier = Modifier.padding(top = 80.dp))
            }
        }
        return
    }

    // 2. Si Firebase está listo, gestionar la navegación
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Principal) }

    when (currentScreen) {
        is Screen.Principal -> {
            PantallaPrincipal(
                onPlanificarClick = { currentScreen = Screen.Planificacion },
                // Muestra el ID de usuario para cumplir con las reglas de seguridad
                userId = firestoreState.userId
            )
        }

        is Screen.Planificacion -> {
            PlanificacionScreen(
                onVolverClick = { currentScreen = Screen.Principal },
                // Pasamos las referencias de Firebase
                db = firebaseManager.db!!,
                userId = firestoreState.userId,
                appId = firebaseManager.appId
            )
        }
        is Screen.Historial -> {
            // Pantalla futura
            Text("Historial de Alumnos")
        }
    }
}


/**
 * PANTALLA PRINCIPAL: Ahora recibe el ID del usuario para mostrarlo.
 */
@Composable
fun PantallaPrincipal(
    onPlanificarClick: () -> Unit,
    userId: String // ID del usuario autenticado
) {
    Scaffold(
        topBar = { TopAppBarPrincipal() }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Sección de información del profesor
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Prof. Alejandro González", style = MaterialTheme.typography.titleLarge)
                    Text(text = "Academia Central de Tenis", style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "ID Usuario: $userId", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.secondary)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Funcionalidades Clave",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Start)
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Botón 1: PLANIFICAR SESIÓN
            BotonFuncionalidad(
                texto = "PLANIFICAR SESIÓN",
                descripcion = "Crea tu clase en 2 clics",
                onClick = onPlanificarClick
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Botón 2: HISTORIAL DE ALUMNOS
            BotonFuncionalidad(
                texto = "HISTORIAL DE ALUMNOS",
                descripcion = "Consulta informes de progreso individual",
                onClick = { /* TODO */ }
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Botón 3: VIDEO ANÁLISIS RÁPIDO
            BotonFuncionalidad(
                texto = "VIDEO ANÁLISIS RÁPIDO",
                descripcion = "Graba y envía feedback visual",
                onClick = { /* TODO */ }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBarPrincipal() {
    TopAppBar(
        title = { Text("CoachTrack 🎾") },
    )
}

@Composable
fun BotonFuncionalidad(texto: String, descripcion: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp),
        contentPadding = PaddingValues(16.dp)
    ) {
        Column(horizontalAlignment = Alignment.Start, modifier = Modifier.fillMaxWidth()) {
            Text(text = texto, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(text = descripcion, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PantallaPrincipalPreview() {
    CoachTrackTheme {
        // Debemos pasar un ID de usuario en el preview
        PantallaPrincipal(onPlanificarClick = {}, userId = "preview-user-id-12345")
    }
}