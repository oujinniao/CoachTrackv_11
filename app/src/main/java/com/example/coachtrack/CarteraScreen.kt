package com.example.coachtrack

import android.app.Application
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun CarteraScreen(
    onVolver: () -> Unit,
    onAbrirFichaAlumno: (Alumnos) -> Unit
) {
    val context = LocalContext.current
    val application = context.applicationContext as Application
    val viewModel: CarteraViewModel = viewModel(
        factory = androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.getInstance(application)
    )

    val alumnos by viewModel.alumnos.collectAsState(initial = emptyList())
    val scope = rememberCoroutineScope()

    // Snackbar animado
    var showSnackbar by remember { mutableStateOf(false) }
    var snackbarMessage by remember { mutableStateOf("") }
    var snackbarColor by remember { mutableStateOf(Color(0xFF4CAF50)) }

    // Diálogo de confirmación
    var alumnoAEliminar by remember { mutableStateOf<AlumnoEntity?>(null) }

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
            FloatingActionButton(onClick = {
                viewModel.agregarAlumnoDemo()
                snackbarMessage = "Alumno de ejemplo agregado ✅"
                snackbarColor = Color(0xFF4CAF50)
                showSnackbar = true
                scope.launch {
                    delay(2000)
                    showSnackbar = false
                }
            }) {
                Icon(Icons.Default.Add, contentDescription = "Agregar Alumno")
            }
        }
    ) { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            if (alumnos.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("No hay alumnos registrados")
                    Text(
                        "Agrega alumnos con el botón +",
                        color = Color.Gray,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(alumnos, key = { it.id }) { alumno ->

                        // Estado local del alumno visible
                        var visible by remember { mutableStateOf(true) }

                        // Escala animada con rebote
                        val scale by animateFloatAsState(
                            targetValue = if (visible) 1f else 0.6f,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessLow
                            ),
                            label = "bounceScale"
                        )

                        AnimatedVisibility(
                            visible = visible,
                            enter = fadeIn(tween(300)),
                            exit = fadeOut(tween(400)) + slideOutHorizontally(
                                targetOffsetX = { it / 2 },
                                animationSpec = tween(500, easing = LinearOutSlowInEasing)
                            )
                        ) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .scale(scale)
                                    .clickable {
                                        onAbrirFichaAlumno(
                                            Alumnos(
                                                id = alumno.id.toString(),
                                                nombre = alumno.nombre,
                                                nivelActual = alumno.nivelActual,
                                                clasesPactadas = alumno.clasesPactadas,
                                                clasesCursadas = alumno.clasesCursadas,
                                                estadoPago = alumno.estadoPago
                                            )
                                        )
                                    },
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .padding(12.dp)
                                        .fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(Modifier.weight(1f)) {
                                        Text(alumno.nombre, fontWeight = FontWeight.Bold)
                                        Text(
                                            "Nivel: ${alumno.nivelActual}",
                                            color = Color.Gray,
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                        Text(
                                            "Pago: ${alumno.estadoPago}",
                                            color = when (alumno.estadoPago) {
                                                EstadoPago.ADELANTADO -> Color(0xFF2E7D32)
                                                EstadoPago.PENDIENTE -> Color(0xFFFFA000)
                                                EstadoPago.DEUDA -> Color(0xFFC62828)
                                            },
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    Row {
                                        IconButton(onClick = {
                                            onAbrirFichaAlumno(
                                                Alumnos(
                                                    id = alumno.id.toString(),
                                                    nombre = alumno.nombre,
                                                    nivelActual = alumno.nivelActual,
                                                    clasesPactadas = alumno.clasesPactadas,
                                                    clasesCursadas = alumno.clasesCursadas,
                                                    estadoPago = alumno.estadoPago
                                                )
                                            )
                                        }) {
                                            Icon(Icons.Default.Edit, contentDescription = "Editar")
                                        }

                                        IconButton(onClick = {
                                            alumnoAEliminar = alumno
                                        }) {
                                            Icon(
                                                Icons.Default.Delete,
                                                contentDescription = "Eliminar alumno",
                                                tint = Color.Red
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Diálogo de confirmación de eliminación
                        if (alumnoAEliminar == alumno) {
                            AlertDialog(
                                onDismissRequest = { alumnoAEliminar = null },
                                confirmButton = {
                                    TextButton(onClick = {
                                        scope.launch {
                                            // 🔹 Animación de salida con rebote
                                            visible = false
                                            delay(450)
                                            viewModel.eliminarAlumno(alumno)
                                            snackbarMessage = "Alumno ${alumno.nombre} eliminado ❌"
                                            snackbarColor = Color(0xFFE53935)
                                            showSnackbar = true
                                            alumnoAEliminar = null
                                            delay(2500)
                                            showSnackbar = false
                                        }
                                    }) {
                                        Text("Eliminar", color = Color.Red)
                                    }
                                },
                                dismissButton = {
                                    TextButton(onClick = { alumnoAEliminar = null }) {
                                        Text("Cancelar")
                                    }
                                },
                                title = { Text("¿Eliminar alumno?") },
                                text = { Text("Esta acción no se puede deshacer.") }
                            )
                        }
                    }
                }
            }

            // Snackbar animado con deslizamiento
            AnimatedVisibility(
                visible = showSnackbar,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 32.dp)
            ) {
                Surface(
                    color = snackbarColor,
                    shadowElevation = 10.dp,
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text(
                        text = snackbarMessage,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp)
                    )
                }
            }
        }
    }
}
