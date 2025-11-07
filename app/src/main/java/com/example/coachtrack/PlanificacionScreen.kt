package com.example.coachtrack

import android.app.Application
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel

import kotlin.jvm.java


// -------------------- FACTORY UNIFICADO --------------------
// Nota: Es mejor mover esta clase a un archivo separado (AppViewModelFactory.kt)
class AppViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return when {
            modelClass.isAssignableFrom(CarteraViewModel::class.java) ->
                CarteraViewModel(application) as T
            modelClass.isAssignableFrom(SesionViewModel::class.java) ->
                SesionViewModel(application) as T
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

// -------------------- ESTADOS DE NAVEGACIÓN --------------------
enum class PlanificacionState {
    PRINCIPAL,
    CARTERA,
    VIDEO
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanificacionScreen(onVolverClick: () -> Unit) {
    var currentState by remember { mutableStateOf(PlanificacionState.PRINCIPAL) }
    val ejerciciosSesion = remember { mutableStateListOf<Plantilla>() }
    var alumnoSeleccionado by remember { mutableStateOf<Alumnos?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val context = LocalContext.current
    val application = context.applicationContext as Application

    // 🔹 Inicialización del Factory unificado
    val appFactory = remember { AppViewModelFactory(application) }

    // 🔹 ViewModel de alumnos
    // Usamos el Factory unificado
    val carteraViewModel: CarteraViewModel = viewModel(factory = appFactory)

    // 🔹 ViewModel de sesiones
    // Usamos el Factory unificado. ¡El error de referencia no resuelta se soluciona con la importación!
    val sesionViewModel: SesionViewModel = viewModel(factory = appFactory)

    // Observamos alumnos desde Room
    val alumnosRoom by carteraViewModel.alumnos.collectAsState(initial = emptyList())

    // Mapeamos AlumnoEntity -> modelo Alumnos
    // 🚨 Corrección de código incompleto (la línea que terminaba en 'val alumnos = alumno')
    val alumnos: List<Alumnos> = alumnosRoom.map { entity ->
        // Necesitas definir cómo mapear aquí. Esto es un ejemplo:
        Alumnos(
            id = entity.id.toString(),
            nombre = entity.nombre,
            nivelActual = entity.nivelActual,
            clasesPactadas = entity.clasesPactadas
            // ... añade el resto de propiedades
        )
    }

    // -------------------- ESTRUCTURA DE LA UI --------------------
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("CoachTrack - Planificación") },
                navigationIcon = {
                    IconButton(onClick = onVolverClick) {
                        Icon(Icons.Filled.ArrowDropDown, contentDescription = "Volver")
                    }
                }
            )
        },
        floatingActionButton = {
            if (currentState == PlanificacionState.PRINCIPAL) {
                FloatingActionButton(onClick = { /* Lógica para añadir sesión */ }) {
                    Icon(Icons.Filled.Add, contentDescription = "Añadir Sesión")
                }
            }
        }
    ) { paddingValues ->
        // Contenido principal de la pantalla
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Aquí iría la lógica de navegación basada en currentState
            when (currentState) {
                PlanificacionState.PRINCIPAL -> {
                    // Contenido de la pantalla principal
                    Text("Pantalla Principal de Planificación", style = MaterialTheme.typography.headlineSmall)
                    Spacer(modifier = Modifier.height(16.dp))

                    // Botón de ejemplo para navegar a la cartera
                    Button(onClick = { currentState = PlanificacionState.CARTERA }) {
                        Text("Ver Cartera de Alumnos")
                    }

                    // Lista de Sesiones (Ejemplo de cómo usar sesionViewModel)
                    // val sesiones by sesionViewModel.sesiones.collectAsState(initial = emptyList())
                    // LazyColumn(content = { items(sesiones) { sesion -> /* ... */ } })
                }

                PlanificacionState.CARTERA -> {
                    // Contenido de la pantalla de cartera
                    Text("Cartera de Alumnos (${alumnos.size})", style = MaterialTheme.typography.headlineSmall)

                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(alumnos) { alumno ->
                            // Componente visual para cada alumno (Ejemplo)
                            Card(modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable { alumnoSeleccionado = alumno }
                            ) {
                                Text(alumno.nombre, modifier = Modifier.padding(8.dp))
                            }
                        }
                    }
                }

                PlanificacionState.VIDEO -> {
                    // Contenido de la pantalla de video
                    Text("Gestión de Videos", style = MaterialTheme.typography.headlineSmall)
                }
            }
        }
    }
}