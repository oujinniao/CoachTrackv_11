package com.example.coachtrack

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanificacionScreen(onVolverClick: () -> Unit) {

    val context = LocalContext.current
    val carteraViewModel: CarteraViewModel = viewModel()
    val sesionRepository = remember {
        val app = context.applicationContext as CoachTrackApplication
        SesionRepository(app.container.db.sesionDao())
    }
    val sesionViewModel: SesionViewModel = viewModel(
        factory = object : androidx.lifecycle.ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                if (modelClass.isAssignableFrom(SesionViewModel::class.java)) {
                    return SesionViewModel(
                        context.applicationContext as android.app.Application,
                        sesionRepository
                    ) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    )

    val alumnosRoom by carteraViewModel.alumnos.collectAsState(initial = emptyList())

    val alumnos: List<Alumnos> = alumnosRoom.map { entity ->
        Alumnos(
            localId = entity.localId,
            nombre = entity.nombre,
            nivelActual = entity.nivelActual,
            objetivo = entity.objetivo,
            clasesPactadas = entity.clasesPactadas,
            clasesCursadas = entity.clasesCursadas,
            estadoPago = EstadoPago.valueOf(entity.estadoPago),
            datosPersonales = DatosPersonales(
                edad = entity.edad,
                telefono = entity.telefono,
                direccion = entity.direccion
            )
        )
    }

    val plantillas = PLANTILLAS_MOCK
    val scope = rememberCoroutineScope()
    var alumnoSeleccionado by remember { mutableStateOf<Alumnos?>(null) }
    var query by remember { mutableStateOf("") }
    var plantillaSeleccionada by remember { mutableStateOf<Plantilla?>(null) }
    var showSnackbar by remember { mutableStateOf(false) }
    var snackbarMessage by remember { mutableStateOf("") }

    BackHandler(enabled = true) {
        when {
            plantillaSeleccionada != null -> plantillaSeleccionada = null
            alumnoSeleccionado != null -> alumnoSeleccionado = null
            else -> onVolverClick()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Planificar Sesión") },
                navigationIcon = {
                    IconButton(onClick = onVolverClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->

        if (plantillaSeleccionada != null) {
            PlantillaDetailScreen(
                plantilla = plantillaSeleccionada!!,
                onAdd = {
                    sesionViewModel.agregarPlantilla(plantillaSeleccionada!!)
                    plantillaSeleccionada = null
                },
                onVolver = { plantillaSeleccionada = null }
            )
            return@Scaffold
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                if (alumnoSeleccionado == null) {

                    OutlinedTextField(
                        value = query,
                        onValueChange = { query = it },
                        label = { Text("Buscar alumno") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(8.dp))
                    Text("Selecciona un alumno:", fontWeight = FontWeight.Bold)

                    val alumnosFiltrados = alumnos.filter {
                        it.nombre.contains(query, ignoreCase = true)
                    }

                    LazyColumn {
                        items(alumnosFiltrados) { alumno ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clickable { alumnoSeleccionado = alumno },
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                            ) {
                                Column(Modifier.padding(12.dp)) {
                                    Text(alumno.nombre, style = MaterialTheme.typography.titleMedium)
                                    Text(
                                        "Nivel: ${alumno.nivelActual}",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                        }
                    }

                } else {

                    Text(
                        "Alumno: ${alumnoSeleccionado?.nombre}",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(Modifier.height(8.dp))
                    Text("Selecciona plantillas:", fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))

                    LazyColumn(Modifier.weight(1f)) {
                        items(plantillas) { plantilla ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                                )
                            ) {
                                Row(
                                    Modifier
                                        .padding(12.dp)
                                        .fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(Modifier.weight(1f)) {
                                        Text(plantilla.nombre, fontWeight = FontWeight.Medium)
                                        Text(
                                            plantilla.enfoque,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    IconButton(onClick = { plantillaSeleccionada = plantilla }) {
                                        Icon(Icons.Default.Add, contentDescription = "Ver detalle")
                                    }
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(8.dp))

                    if (sesionViewModel.sesionActual.isNotEmpty()) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Column(Modifier.padding(12.dp)) {
                                Text(
                                    "Plantillas añadidas (${sesionViewModel.sesionActual.size}):",
                                    fontWeight = FontWeight.Bold
                                )
                                sesionViewModel.sesionActual.forEach { p ->
                                    Text("• ${p.nombre} (${p.duracionMinutos} min)")
                                }
                                val total = sesionViewModel.sesionActual.sumOf { it.duracionMinutos }
                                Spacer(Modifier.height(6.dp))
                                Text(
                                    "Duración total: $total minutos",
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        OutlinedButton(
                            onClick = {
                                alumnoSeleccionado = null
                                sesionViewModel.limpiarSesionActual()
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Cancelar")
                        }

                        Spacer(Modifier.width(8.dp))

                        Button(
                            onClick = {
                                scope.launch {
                                    alumnoSeleccionado?.let { alumno ->
                                        val newSessionId = sesionViewModel.generarNuevoSessionId()

                                        val sesion = SesionDeClase(
                                            sessionId = newSessionId,
                                            fechaCreacion = java.time.LocalDateTime.now()
                                                .format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                                            alumnoNombre = alumno.nombre,
                                            duracionTotalMinutos = sesionViewModel.sesionActual.sumOf { it.duracionMinutos },
                                            ejercicios = sesionViewModel.sesionActual.toMutableList()
                                        )

                                        val entidad = sesion.toEntity(
                                            alumnoId = alumno.localId,
                                            alumnoNombre = alumno.nombre
                                        )

                                        sesionViewModel.guardarNuevaSesion(entidad)

                                        snackbarMessage = "Sesión guardada para ${alumno.nombre}"
                                        showSnackbar = true
                                        delay(2500)
                                        showSnackbar = false

                                        sesionViewModel.limpiarSesionActual()
                                        alumnoSeleccionado = null
                                    }
                                }
                            },
                            modifier = Modifier.weight(1f),
                            enabled = sesionViewModel.sesionActual.isNotEmpty()
                        ) {
                            Text("Guardar sesión")
                        }
                    }
                }
            }

            AnimatedVisibility(
                visible = showSnackbar,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 32.dp)
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shadowElevation = 10.dp,
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text(
                        text = snackbarMessage,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp)
                    )
                }
            }
        }
    }
}