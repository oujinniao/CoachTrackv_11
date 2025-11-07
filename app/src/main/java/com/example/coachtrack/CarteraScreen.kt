package com.example.coachtrack

import android.app.Application
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun CarteraScreen(
    onVolver: () -> Unit,
    onAbrirFichaAlumno: (Alumnos) -> Unit
) {
    var filtro by remember { mutableStateOf("") }
    var showDashboard by remember { mutableStateOf(false) }
    var showList by remember { mutableStateOf(false) }
    var showAddDialog by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // ViewModel
    val context = LocalContext.current
    val viewModel: CarteraViewModel = viewModel(
        factory = androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
            .getInstance(context.applicationContext as Application)
    )

    val alumnosRoom by viewModel.alumnos.collectAsState()

    // Animaciones al cargar
    LaunchedEffect(Unit) {
        delay(300)
        showDashboard = true
        delay(300)
        showList = true
    }

    // Filtros y métricas
    val alumnosFiltrados = alumnosRoom.filter { it.nombre.contains(filtro, ignoreCase = true) }
    val totalAlumnos = alumnosRoom.size
    val alDia = alumnosRoom.count { it.estadoPago == EstadoPago.ADELANTADO }
    val pendientes = alumnosRoom.count { it.estadoPago == EstadoPago.PENDIENTE }
    val enDeuda = alumnosRoom.count { it.estadoPago == EstadoPago.DEUDA }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cartera de Alumnos") },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar Alumno")
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            // ---------------- BUSCADOR ----------------
            OutlinedTextField(
                value = filtro,
                onValueChange = { filtro = it },
                label = { Text("Buscar alumno...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )

            // ---------------- DASHBOARD ----------------
            AnimatedVisibility(
                visible = showDashboard,
                enter = fadeIn(animationSpec = tween(700)) +
                        scaleIn(initialScale = 0.7f, animationSpec = spring(dampingRatio = 0.5f)),
                exit = fadeOut()
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Resumen general", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        DashboardCard("Total", totalAlumnos.toString(), Color(0xFF1565C0), Modifier.weight(1f))
                        Spacer(modifier = Modifier.width(8.dp))
                        DashboardCard("Al día", alDia.toString(), Color(0xFF2E7D32), Modifier.weight(1f))
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        DashboardCard("Pendientes", pendientes.toString(), Color(0xFFFFA000), Modifier.weight(1f))
                        Spacer(modifier = Modifier.width(8.dp))
                        DashboardCard("Deuda", enDeuda.toString(), Color(0xFFC62828), Modifier.weight(1f))
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // ---------------- LISTA DE ALUMNOS ----------------
            AnimatedVisibility(
                visible = showList,
                enter = fadeIn(animationSpec = tween(600)) +
                        slideInVertically(initialOffsetY = { it / 2 }),
                exit = fadeOut()
            ) {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    items(alumnosFiltrados, key = { it.id }) { entity ->
                        // Convertir AlumnoEntity → Alumnos
                        val alumno = Alumnos(
                            id = entity.id.toString(),
                            nombre = entity.nombre,
                            nivelActual = entity.nivelActual,
                            objetivo = entity.objetivo,
                            clasesPactadas = entity.clasesPactadas,
                            clasesCursadas = entity.clasesCursadas,
                            estadoPago = entity.estadoPago,
                            datosPersonales = DatosPersonales(
                                edad = entity.edad,
                                telefono = entity.telefono,
                                direccion = entity.direccion
                            ),
                            notasEntrenador = entity.notasEntrenador
                        )

                        AlumnoCard(
                            alumno = alumno,
                            onAbrirFichaAlumno = { onAbrirFichaAlumno(alumno) }
                        )
                    }
                }
            }

            // ---------------- BOTÓN VOLVER ----------------
            Button(
                onClick = onVolver,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
            ) {
                Text("Volver")
            }
        }

        // ---------------- DIALOGO: AGREGAR NUEVO ALUMNO ----------------
        if (showAddDialog) {
            Dialog(onDismissRequest = { showAddDialog = false }) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    var nombre by remember { mutableStateOf("") }
                    var nivelActual by remember { mutableStateOf("") }
                    var objetivo by remember { mutableStateOf("") }
                    var edad by remember { mutableStateOf("") }
                    var telefono by remember { mutableStateOf("") }
                    var direccion by remember { mutableStateOf("") }

                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("Agregar nuevo alumno", style = MaterialTheme.typography.titleLarge)

                        OutlinedTextField(
                            value = nombre,
                            onValueChange = { nombre = it },
                            label = { Text("Nombre completo") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Campo desplegable para nivel actual
                        NivelActualSelector(
                            nivelActual = nivelActual,
                            onNivelChange = { nuevoNivel -> nivelActual = nuevoNivel }
                        )

                        OutlinedTextField(
                            value = objetivo,
                            onValueChange = { objetivo = it },
                            label = { Text("Objetivo principal") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Divider()

                        OutlinedTextField(
                            value = edad,
                            onValueChange = { edad = it },
                            label = { Text("Edad") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = telefono,
                            onValueChange = { telefono = it },
                            label = { Text("Teléfono") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = direccion,
                            onValueChange = { direccion = it },
                            label = { Text("Dirección") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Row(
                            horizontalArrangement = Arrangement.End,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            TextButton(onClick = { showAddDialog = false }) {
                                Text("Cancelar")
                            }
                            Button(onClick = {
                                if (nombre.isNotBlank()) {
                                    viewModel.agregarAlumno(
                                        AlumnoEntity(
                                            nombre = nombre,
                                            nivelActual = nivelActual,
                                            objetivo = objetivo,
                                            clasesPactadas = 0,
                                            clasesCursadas = 0,
                                            estadoPago = EstadoPago.PENDIENTE,
                                            edad = edad.toIntOrNull() ?: 0,
                                            telefono = telefono,
                                            direccion = direccion,
                                            notasEntrenador = ""
                                        )
                                    )
                                    showAddDialog = false
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar("Alumno agregado correctamente")
                                    }
                                }
                            }) {
                                Text("Guardar")
                            }
                        }
                    }
                }
            }
        }
    }
}

// ---------------- CARD RESUMEN ----------------
@Composable
fun DashboardCard(title: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.08f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value, color = color, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
            Text(title, style = MaterialTheme.typography.bodySmall, color = color.copy(alpha = 0.8f))
        }
    }
}

// ---------------- CARD DE ALUMNO ----------------
@Composable
fun AlumnoCard(alumno: Alumnos, onAbrirFichaAlumno: (Alumnos) -> Unit) {
    val progresoAnimado by animateFloatAsState(
        targetValue = if (alumno.clasesPactadas > 0)
            alumno.clasesCursadas.toFloat() / alumno.clasesPactadas.toFloat()
        else 0f,
        animationSpec = spring(dampingRatio = 0.7f)
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { onAbrirFichaAlumno(alumno) }
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = alumno.nombre,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            LinearProgressIndicator(
                progress = progresoAnimado,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(CircleShape)
                    .padding(top = 4.dp),
                color = MaterialTheme.colorScheme.primary
            )
            Text("Clases: ${alumno.clasesCursadas}/${alumno.clasesPactadas}", style = MaterialTheme.typography.bodySmall)
            Text("Estado de pago: ${alumno.estadoPago.name}", style = MaterialTheme.typography.bodySmall)
        }
    }
}