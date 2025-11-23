package com.example.coachtrack


import android.app.Application
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.coachtrack.FiltroChip
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarteraScreen(
    onVolver: () -> Unit,
    onAbrirFichaAlumno: (AlumnoEntity) -> Unit
) {
    //---------------BACKHANDLER PARA ESTA PANTALLA-----------------------
    BackHandler(onBack = onVolver)


    val context = LocalContext.current
    val carteraViewModel: CarteraViewModel = viewModel(
        factory = androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
            .getInstance(context.applicationContext as Application)
    )

    val alumnos by carteraViewModel.alumnos.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var filtro by remember { mutableStateOf("Todos") }

    // ========= FILTRO DE ALUMNOS =========
    val alumnosFiltrados = when (filtro) {
        "Al día" -> alumnos.filter { it.estadoPago == EstadoPago.ADELANTADO.name }
        "Pendiente" -> alumnos.filter { it.estadoPago == EstadoPago.PENDIENTE.name }
        "Deuda" -> alumnos.filter { it.estadoPago == EstadoPago.DEUDA.name }
        else -> alumnos
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cartera de Alumnos") },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    TextButton(onClick = { carteraViewModel.eliminarTodos() }) {
                        Text("🧹", color = Color.Red)
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { carteraViewModel.abrirDialogoAgregar() },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar Alumno")
            }
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {

            // ========= RESUMEN ==========
            val total = alumnos.size
            val alDia = alumnos.count { it.estadoPago == EstadoPago.ADELANTADO.name }
            val pendientes = alumnos.count { it.estadoPago == EstadoPago.PENDIENTE.name }
            val deudas = alumnos.count { it.estadoPago == EstadoPago.DEUDA.name }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF3F3F3))
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("📊 Resumen Financiero", fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(4.dp))
                    Text("Total de alumnos: $total")
                    Text("🟢 Al día: $alDia")
                    Text("🟡 Pendientes: $pendientes")
                    Text("🔴 En deuda: $deudas")
                }
            }

            // ========= FILTRO UI =========
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                FiltroChip("Todos", filtro) { filtro = it }
                FiltroChip("Al día", filtro) { filtro = it }
                FiltroChip("Pendiente", filtro) { filtro = it }
                FiltroChip("Deuda", filtro) { filtro = it }
            }

            // ========= LISTA =========
            if (alumnosFiltrados.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No hay alumnos en esta categoría")
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(alumnosFiltrados, key = { it.id }) { alumno ->
                        var eliminado by remember { mutableStateOf(false) }

                        val scaleAnim by animateFloatAsState(
                            targetValue = if (eliminado) 0f else 1f,
                            animationSpec = spring(),
                            finishedListener = {
                                if (eliminado) {
                                    scope.launch { carteraViewModel.eliminarAlumno(alumno) }
                                }
                            }
                        )

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .scale(scaleAnim)
                                .clickable { onAbrirFichaAlumno(alumno) },
                            colors = CardDefaults.cardColors(
                                containerColor = when (alumno.estadoPago) {
                                    EstadoPago.ADELANTADO.name -> Color(0xFFDFF8E1)
                                    EstadoPago.PENDIENTE.name -> Color(0xFFFFF8E1)
                                    EstadoPago.DEUDA.name -> Color(0xFFFFEBEE)
                                    else -> Color.White
                                }
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(Modifier.weight(1f)) {
                                    Text(
                                        alumno.nombre,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        "Nivel: ${alumno.nivelActual} | ${alumno.clasesCursadas}/${alumno.clasesPactadas} clases",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                    Text(
                                        "Pago: ${alumno.estadoPago}",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }

                                IconButton(onClick = {
                                    carteraViewModel.abrirDialogoAgregar(alumno)
                                }) {
                                    Icon(Icons.Default.Edit, contentDescription = "Editar")
                                }

                                IconButton(onClick = { eliminado = true }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color.Red)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // ========= DIÁLOGO =========
    if (carteraViewModel.mostrarDialogoAgregar) {
        DialogAgregarAlumno(
            alumnoExistente = carteraViewModel.alumnoEnEdicion,
            onDismiss = { carteraViewModel.cerrarDialogoAgregar() },
            onGuardar = { alumno ->
                carteraViewModel.agregarOActualizarAlumno(alumno) { exito, actualizado ->
                    scope.launch {
                        when {
                            !exito -> snackbarHostState.showSnackbar("⚠️ El alumno ya existe en tu cartera")
                            actualizado -> snackbarHostState.showSnackbar("✅ Alumno actualizado correctamente")
                            else -> snackbarHostState.showSnackbar("✅ Alumno agregado correctamente")
                        }
                    }
                }
                carteraViewModel.cerrarDialogoAgregar()
            }
        )
    }
}

@Composable
fun FiltroChip(
    texto: String,
    seleccionado: String,
    onClick: (String) -> Unit
) {
    val isSelected = texto == seleccionado

    Surface(
        shape = CircleShape,
        color = if (isSelected) MaterialTheme.colorScheme.primary else Color.LightGray,
        modifier = Modifier.clickable { onClick(texto) }
    ) {
        Text(
            text = texto,
            color = if (isSelected) Color.White else Color.Black,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
