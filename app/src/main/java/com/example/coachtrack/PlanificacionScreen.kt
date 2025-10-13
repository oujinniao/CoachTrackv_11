package com.example.coachtrack

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

// -------------------- ESTADOS DE NAVEGACIÓN --------------------
enum class PlanificacionState {
    PRINCIPAL,
    CARTERA,
    VIDEO,
    FICHA_ALUMNO,  // 🔹 Nuevo estado para mostrar FichaAlumnoScreen
    FICHA_TECNICA,
    MINI_PLANIFICACION
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanificacionScreen(onVolverClick: () -> Unit) {
    var currentState by remember { mutableStateOf(PlanificacionState.PRINCIPAL) }
    val ejerciciosSesion = remember { mutableStateListOf<Plantilla>() }
    var plantillaSeleccionada by remember { mutableStateOf<Plantilla?>(null) }
    var alumnoSeleccionado by remember { mutableStateOf<Alumnos?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // -------------------- LÓGICA DE GUARDADO --------------------
    val onGuardarSesion: () -> Unit = {
        if (alumnoSeleccionado != null && ejerciciosSesion.isNotEmpty()) {
            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm")
            val fechaGuardado = LocalDateTime.now().format(formatter)

            val nuevoSessionId = generarNuevoSessionId(getMockSesionesGuardadas())
            val nuevaSesion = SesionDeClase(
                sessionId = nuevoSessionId,
                alumnoNombre = alumnoSeleccionado!!.nombre,
                fechaCreacion = fechaGuardado,
                duracionTotalMinutos = ejerciciosSesion.sumOf { it.duracionMinutos },
                ejercicios = ejerciciosSesion.toList().toMutableList()
            )

            getMockSesionesGuardadas().add(0, nuevaSesion)

            val nombreAlumno = alumnoSeleccionado!!.nombre
            scope.launch {
                snackbarHostState.showSnackbar(
                    message = "Sesión guardada con éxito para $nombreAlumno!",
                    duration = SnackbarDuration.Short
                )
            }

            ejerciciosSesion.clear()
            alumnoSeleccionado = null
            currentState = PlanificacionState.PRINCIPAL
            onVolverClick()
        } else {
            scope.launch {
                snackbarHostState.showSnackbar(
                    message = "Debe seleccionar un alumno y al menos un ejercicio.",
                    duration = SnackbarDuration.Short
                )
            }
        }
    }

    // -------------------- INTERFAZ PRINCIPAL --------------------
    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Planificación de Sesión") },
                    navigationIcon = {
                        IconButton(onClick = onVolverClick) {
                            Icon(Icons.Default.List, contentDescription = "Volver al Menú")
                        }
                    }
                )
            },
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { pv ->
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(pv)
                    .padding(horizontal = 16.dp)
            ) {
                // ---- SELECCIÓN DE ALUMNO ----
                AlumnoSelector(
                    alumnoSeleccionado = alumnoSeleccionado,
                    alumnos = remember { getMockAlumnos() },
                    onAlumnoSelected = { alumnoSeleccionado = it }
                )

                // ---- EJERCICIOS ----
                Text(
                    "Ejercicios de la Sesión (${ejerciciosSesion.size}):",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                )

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(ejerciciosSesion, key = { it.instanceId }) { ejercicio ->
                        EjercicioCard(
                            plantilla = ejercicio,
                            onRemove = { ejerciciosSesion.remove(ejercicio) }
                        )
                    }

                    item {
                        Divider(Modifier.padding(vertical = 8.dp))
                        Text(
                            "Plantillas Disponibles:",
                            style = MaterialTheme.typography.titleSmall
                        )
                    }

                    items(PLANTILLAS_MOCK, key = { it.id }) { plantilla ->
                        PlantillaCard(
                            plantilla = plantilla,
                            onAdd = {
                                plantillaSeleccionada = plantilla
                                currentState = PlanificacionState.FICHA_TECNICA
                            }
                        )
                    }
                }

                // ---- BOTONES SECUNDARIOS ----
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = { currentState = PlanificacionState.CARTERA },
                        modifier = Modifier.weight(1f).padding(end = 8.dp)
                    ) {
                        Icon(Icons.Default.List, contentDescription = "Cartera")
                        Spacer(Modifier.width(4.dp))
                        Text("Cartera")
                    }

                    Button(
                        onClick = { currentState = PlanificacionState.VIDEO },
                        modifier = Modifier.weight(1f).padding(start = 8.dp)
                    ) {
                        Icon(Icons.Default.Videocam, contentDescription = "Video")
                        Spacer(Modifier.width(4.dp))
                        Text("Video")
                    }
                }

                // ---- BOTÓN GUARDAR ----
                Button(
                    onClick = { onGuardarSesion() },
                    enabled = alumnoSeleccionado != null && ejerciciosSesion.isNotEmpty(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                        .height(50.dp)
                ) {
                    Text("GUARDAR Y VOLVER")
                }
            }
        }

        // -------------------- SUBPANTALLAS --------------------
        when (currentState) {
            PlanificacionState.CARTERA -> CarteraScreen(
                onVolver = { currentState = PlanificacionState.PRINCIPAL },
                onAbrirFichaAlumno = { alumno ->
                    alumnoSeleccionado = alumno
                    currentState = PlanificacionState.FICHA_ALUMNO
                }
            )

            PlanificacionState.VIDEO -> VideoScreen(
                onVolver = { currentState = PlanificacionState.PRINCIPAL }
            )

            PlanificacionState.FICHA_ALUMNO -> {
                alumnoSeleccionado?.let { alumno ->
                    FichaAlumnoScreen(
                        alumnoInicial = alumno,
                        onVolver = { currentState = PlanificacionState.CARTERA },
                        onNuevaSesionClick = {
                            alumnoSeleccionado = it
                            currentState = PlanificacionState.MINI_PLANIFICACION
                        }
                    )
                }
            }
            PlanificacionState.MINI_PLANIFICACION -> {
                alumnoSeleccionado?.let { alumno ->
                    MiniPlanificacionScreen(
                        alumno = alumno,
                        onVolver = { currentState = PlanificacionState.FICHA_ALUMNO }
                    )
                }
            }

            PlanificacionState.FICHA_TECNICA -> {
                plantillaSeleccionada?.let { plantilla ->
                    val nuevaInstancia = plantilla.copy(instanceId = UUID.randomUUID().toString())
                    PlantillaDetailScreen(
                        plantilla = nuevaInstancia,
                        onAdd = {
                            ejerciciosSesion.add(nuevaInstancia)
                            currentState = PlanificacionState.PRINCIPAL
                            plantillaSeleccionada = null
                        },
                        onVolver = { currentState = PlanificacionState.PRINCIPAL }
                    )
                }
            }

            PlanificacionState.PRINCIPAL -> { /* Pantalla base */ }
        }
    }
}

// -------------------- COMPONENTES AUXILIARES --------------------

@Composable
fun AlumnoSelector(
    alumnoSeleccionado: Alumnos?,
    alumnos: List<Alumnos>,
    onAlumnoSelected: (Alumnos) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
            .clickable { expanded = true }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.List, contentDescription = "Alumno")
            Spacer(Modifier.width(8.dp))
            Text(
                alumnoSeleccionado?.nombre ?: "Seleccionar Alumno...",
                style = MaterialTheme.typography.titleMedium
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth(0.9f)
        ) {
            alumnos.forEach { alumno ->
                DropdownMenuItem(
                    text = { Text(alumno.nombre) },
                    onClick = {
                        onAlumnoSelected(alumno)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun EjercicioCard(
    plantilla: Plantilla,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(plantilla.nombre, style = MaterialTheme.typography.titleSmall)
                Text(
                    "Duración: ${plantilla.duracionMinutos} min | Enfoque: ${plantilla.enfoque}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            IconButton(onClick = onRemove) {
                Icon(Icons.Default.Delete, contentDescription = "Eliminar Ejercicio", tint = Color.Red)
            }
        }
    }
}

@Composable
fun PlantillaCard(
    plantilla: Plantilla,
    onAdd: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(plantilla.nombre, style = MaterialTheme.typography.titleSmall)
                Text(
                    "Duración: ${plantilla.duracionMinutos} min | Enfoque: ${plantilla.enfoque}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Button(onClick = onAdd) {
                Icon(Icons.Default.Add, contentDescription = "Añadir a sesión")
            }
        }
    }
}
