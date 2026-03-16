package com.example.coachtrack

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FichaAlumnoScreen(
    localId: Long,
    onVolver: () -> Unit,
    onNuevaSesionClick: (Alumnos) -> Unit,
    viewModel: FichaAlumnoViewModel = viewModel()
) {
    var showNuevaSesionDialog by rememberSaveable { mutableStateOf(false) }
    var showNuevaTacticaDialog by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(localId) {
        if (localId != 0L) viewModel.cargarAlumno(localId)
    }

    val alumnoNullable by viewModel.alumno.collectAsState()

    val alumnoState: Alumnos = alumnoNullable ?: Alumnos(
        localId = localId,
        nombre = "Cargando..."
    )

    if (localId != 0L && alumnoNullable == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    var selectedTab by rememberSaveable { mutableStateOf(0) }

    val sesiones: List<SesionEntity> by viewModel.sesionViewModel.sesionesDelAlumno.collectAsState(
        initial = emptyList()
    )
    val tacticas: List<TacticaEntity> by viewModel.tacticaViewModel.tacticasDelAlumno.collectAsState(
        initial = emptyList()
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ficha de ${alumnoState.nombre}") },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (alumnoState.localId != 0L) {
                        showNuevaSesionDialog = true
                    }
                },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Nueva sesión")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Text(
                text = alumnoState.nombre,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            val tabs = listOf("Resumen", "Sesiones", "Tácticas")
            TabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title) }
                    )
                }
            }

            when (selectedTab) {
                0 -> TabResumen(
                    alumno = alumnoState,
                    onGuardarNotas = { nuevasNotas ->
                        viewModel.actualizarNotas(nuevasNotas)
                    },
                    onModificarClases = { nuevasClases ->
                        viewModel.actualizarClasesCursadas( nuevasClases)
                    }
                )
                1 -> TabSesiones(sesiones = sesiones)
                2 -> TabTacticas(
                    tacticas = tacticas,
                    onNuevaTacticaClick = { showNuevaTacticaDialog = true }
                )
            }
        }
    }

    if (showNuevaSesionDialog && alumnoState.localId != 0L) {
        DialogNuevaSesion(
            alumnoId = alumnoState.localId,
            alumnoNombre = alumnoState.nombre,
            sesionViewModel = viewModel.sesionViewModel,
            onDismiss = { showNuevaSesionDialog = false }
        )
    }

    if (showNuevaTacticaDialog && alumnoState.localId != 0L) {
        DialogNuevaTactica(
            alumnoNombre = alumnoState.nombre,
            tacticaViewModel = viewModel.tacticaViewModel,
            onDismiss = { showNuevaTacticaDialog = false }
        )
    }
}

@Composable
fun TabResumen(
    alumno: Alumnos,
    onGuardarNotas: (String) -> Unit,
    onModificarClases: (Int) -> Unit
) {
    var notas by rememberSaveable(alumno.localId) { mutableStateOf(alumno.notasEntrenador) }
    var editandoNotas by rememberSaveable(alumno.localId) { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("Nivel Actual: ${alumno.nivelActual}", style = MaterialTheme.typography.titleMedium)
                    Text("Objetivo: ${alumno.objetivo ?: "—"}", style = MaterialTheme.typography.bodyMedium)
                    HorizontalDivider(Modifier.padding(vertical = 8.dp))
                    Text("Teléfono: ${alumno.datosPersonales.telefono}", style = MaterialTheme.typography.bodySmall)
                    Text("Dirección: ${alumno.datosPersonales.direccion}", style = MaterialTheme.typography.bodySmall)
                    Text("Estado de Pago: ${alumno.estadoPago.name}", style = MaterialTheme.typography.bodySmall)
                }
            }
        }

        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Clases Pactadas: ${alumno.clasesPactadas}", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(8.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Clases Cursadas:", style = MaterialTheme.typography.titleLarge)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = {
                                    if (alumno.clasesCursadas > 0) {
                                        onModificarClases(alumno.clasesCursadas - 1)
                                    }
                                },
                                enabled = alumno.clasesCursadas > 0
                            ) {
                                Icon(Icons.Default.Remove, contentDescription = "Restar Clase")
                            }
                            Text(
                                "${alumno.clasesCursadas}",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                            IconButton(
                                onClick = { onModificarClases(alumno.clasesCursadas + 1) }
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Sumar Clase")
                            }
                        }
                    }
                }
            }
        }

        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 80.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Notas del Entrenador", style = MaterialTheme.typography.titleMedium)
                        IconButton(
                            onClick = {
                                editandoNotas = !editandoNotas
                                if (!editandoNotas) onGuardarNotas(notas)
                            }
                        ) {
                            Icon(
                                imageVector = if (editandoNotas) Icons.Default.Save else Icons.Default.Edit,
                                contentDescription = if (editandoNotas) "Guardar Notas" else "Editar Notas"
                            )
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    if (editandoNotas) {
                        OutlinedTextField(
                            value = notas,
                            onValueChange = { notas = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp),
                            label = { Text("Escribe tus notas aquí") }
                        )
                    } else {
                        Text(
                            text = notas.ifEmpty { "No hay notas aún." },
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (notas.isEmpty())
                                MaterialTheme.colorScheme.onSurfaceVariant
                            else
                                MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TabSesiones(sesiones: List<SesionEntity>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (sesiones.isEmpty()) {
            item {
                Box(Modifier.fillParentMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        "No hay sesiones registradas todavía.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            item {
                Text(
                    "Historial de Sesiones (${sesiones.size})",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            items(sesiones, key = { it.id }) { sesion ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Row(modifier = Modifier.padding(12.dp)) {
                        Icon(Icons.Default.List, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Column {
                            Text(sesion.fecha, fontWeight = FontWeight.Bold)
                            Text("Duración: ${sesion.duracion} min", style = MaterialTheme.typography.bodySmall)
                            Text("Ejercicios: ${sesion.ejercicios}", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TabTacticas(
    tacticas: List<TacticaEntity>,
    onNuevaTacticaClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Button(
            onClick = onNuevaTacticaClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary
            )
        ) {
            Icon(Icons.Default.Add, contentDescription = "Nueva Táctica")
            Spacer(Modifier.width(8.dp))
            Text("Añadir Nueva Táctica")
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.weight(1f)
        ) {
            if (tacticas.isEmpty()) {
                item {
                    Box(Modifier.fillParentMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            "No hay tácticas registradas todavía.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                item {
                    Text(
                        "Tácticas Registradas (${tacticas.size})",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                items(tacticas, key = { it.localId }) { tactica ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                tactica.titulo,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleSmall
                            )
                            Text("Nivel: ${tactica.nivel}", style = MaterialTheme.typography.bodySmall)
                            Spacer(Modifier.height(4.dp))
                            Text(tactica.descripcion, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        }
    }
}