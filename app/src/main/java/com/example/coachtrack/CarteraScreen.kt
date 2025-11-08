package com.example.coachtrack

import android.app.Application
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun CarteraScreen(
    onVolver: () -> Unit,
    onAbrirFichaAlumno: (AlumnoEntity) -> Unit
) {
    val context = LocalContext.current
    val carteraViewModel: CarteraViewModel = viewModel(
        factory = androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
            .getInstance(context.applicationContext as Application)
    )

    val alumnos by carteraViewModel.alumnos.collectAsState()
    val scope = rememberCoroutineScope()

    // Estados del formulario
    var mostrarFormulario by remember { mutableStateOf(false) }
    var editandoAlumno by remember { mutableStateOf<AlumnoEntity?>(null) }
    var nombre by remember { mutableStateOf("") }
    var nivel by remember { mutableStateOf("Inicial") }
    var objetivo by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var direccion by remember { mutableStateOf("") }
    var estadoPago by remember { mutableStateOf(EstadoPago.PENDIENTE) }

    // Estado para confirmar eliminaciones
    var alumnoAEliminar by remember { mutableStateOf<AlumnoEntity?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cartera de Alumnos") },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = { carteraViewModel.eliminarTodos() }) {
                        Icon(Icons.Default.DeleteSweep, contentDescription = "Limpiar base de datos")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    // Reset de formulario
                    editandoAlumno = null
                    nombre = ""
                    nivel = "Inicial"
                    objetivo = ""
                    telefono = ""
                    direccion = ""
                    estadoPago = EstadoPago.PENDIENTE
                    mostrarFormulario = !mostrarFormulario
                },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar Alumno")
            }
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // 🔹 FORMULARIO
            AnimatedVisibility(
                visible = mostrarFormulario,
                enter = slideInVertically(initialOffsetY = { it / 2 }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it / 2 }) + fadeOut()
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            if (editandoAlumno != null) "Editar Alumno" else "Nuevo Alumno",
                            fontWeight = FontWeight.Bold
                        )

                        OutlinedTextField(
                            value = nombre,
                            onValueChange = { nombre = it },
                            label = { Text("Nombre del alumno") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        // 🔸 Nivel desplegable
                        var expanded by remember { mutableStateOf(false) }
                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = { expanded = !expanded }
                        ) {
                            OutlinedTextField(
                                value = nivel,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Nivel") },
                                modifier = Modifier.menuAnchor().fillMaxWidth()
                            )
                            ExposedDropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                listOf("Inicial", "Intermedio", "Avanzado", "Competitivo").forEach { opcion ->
                                    DropdownMenuItem(
                                        text = { Text(opcion) },
                                        onClick = {
                                            nivel = opcion
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }

                        OutlinedTextField(
                            value = objetivo,
                            onValueChange = { objetivo = it },
                            label = { Text("Objetivo") },
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
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedButton(
                                onClick = { mostrarFormulario = false },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Cancelar")
                            }
                            Button(
                                onClick = {
                                    if (nombre.isNotBlank()) {
                                        val alumno = AlumnoEntity(
                                            id = editandoAlumno?.id ?: 0,
                                            nombre = nombre,
                                            nivelActual = nivel,
                                            objetivo = objetivo,
                                            telefono = telefono,
                                            direccion = direccion,
                                            estadoPago = estadoPago,
                                            clasesPactadas = 5,
                                            clasesCursadas = 0,
                                            notasEntrenador = ""
                                        )
                                        scope.launch {
                                            if (editandoAlumno == null)
                                                carteraViewModel.agregarAlumnoManual(alumno)
                                            else
                                                carteraViewModel.actualizarAlumno(alumno)

                                            mostrarFormulario = false
                                        }
                                    }
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(if (editandoAlumno != null) "Actualizar" else "Guardar")
                            }
                        }
                    }
                }
            }

            // 🔹 LISTA DE ALUMNOS
            if (alumnos.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No hay alumnos registrados.")
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(alumnos, key = { it.id }) { alumno ->
                        var visible by remember { mutableStateOf(true) }

                        AnimatedVisibility(
                            visible = visible,
                            enter = fadeIn(animationSpec = tween(200)),
                            exit = slideOutHorizontally(
                                targetOffsetX = { -it },
                                animationSpec = tween(300)
                            ) + fadeOut(animationSpec = tween(150))
                        ) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onAbrirFichaAlumno(alumno) },
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD))
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(alumno.nombre, fontWeight = FontWeight.Bold)
                                        Text("Nivel: ${alumno.nivelActual}")
                                        Text("Pago: ${alumno.estadoPago}")
                                    }

                                    Row {
                                        IconButton(onClick = {
                                            // Modo edición
                                            editandoAlumno = alumno
                                            nombre = alumno.nombre
                                            nivel = alumno.nivelActual
                                            objetivo = alumno.objetivo
                                            telefono = alumno.telefono
                                            direccion = alumno.direccion
                                            mostrarFormulario = true
                                        }) {
                                            Icon(
                                                Icons.Default.Edit,
                                                contentDescription = "Editar alumno",
                                                tint = Color(0xFF1976D2)
                                            )
                                        }

                                        IconButton(onClick = {
                                            alumnoAEliminar = alumno
                                        }) {
                                            Icon(
                                                Icons.Default.Delete,
                                                contentDescription = "Eliminar alumno",
                                                tint = Color(0xFFD32F2F)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 🔸 Diálogo de confirmación de eliminación
        if (alumnoAEliminar != null) {
            AlertDialog(
                onDismissRequest = { alumnoAEliminar = null },
                title = { Text("Eliminar alumno") },
                text = { Text("¿Seguro que deseas eliminar a ${alumnoAEliminar!!.nombre}?") },
                confirmButton = {
                    TextButton(onClick = {
                        scope.launch {
                            carteraViewModel.eliminarAlumno(alumnoAEliminar!!)
                            alumnoAEliminar = null
                        }
                    }) {
                        Text("Eliminar", color = Color.Red)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { alumnoAEliminar = null }) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }
}
