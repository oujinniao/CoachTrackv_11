package com.example.coachtrack

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FichaAlumnoScreen(
    alumnoInicial: Alumnos,
    onVolver: () -> Unit,
    onNuevaSesionClick: (Alumnos) -> Unit,
    viewModel: FichaAlumnoViewModel = viewModel()
) {
    // 🔹 Actualiza datos personales al abrir
    LaunchedEffect(alumnoInicial) {
        viewModel.actualizarDatosPersonales(alumnoInicial.datosPersonales)
    }

    // 🔹 Estado observado desde el ViewModel
    val alumnoState by viewModel.alumno.observeAsState(alumnoInicial)

    // 🔹 Mantenemos la ficha estable ante recomposición o pérdida de foco
    var alumno by rememberSaveable { mutableStateOf(alumnoState) }
    var selectedTab by rememberSaveable { mutableStateOf(0) }
    var notas by rememberSaveable { mutableStateOf(alumno.notasEntrenador) }
    var editandoNotas by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ficha de ${alumno.nombre}") },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onNuevaSesionClick(alumno) },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Edit, contentDescription = "Nueva sesión")
            }
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // ---------------- ENCABEZADO ----------------
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFAFAFA))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(alumno.nivelActual, fontWeight = FontWeight.Bold, color = Color.Gray)
                    Spacer(Modifier.height(8.dp))
                    CircularProgressIndicator(
                        progress = (alumno.progreso / 100f).coerceIn(0f, 1f),
                        strokeWidth = 8.dp,
                        modifier = Modifier.size(80.dp)
                    )
                    Spacer(Modifier.height(8.dp))
                    Text("${alumno.progreso}% completado", fontSize = 18.sp)
                    Text("Clases ${alumno.clasesCursadas} / ${alumno.clasesPactadas}")

                    Spacer(Modifier.height(8.dp))
                    Text("Ajustar clases pactadas:", fontWeight = FontWeight.Bold)

                    var clasesPactadas by rememberSaveable { mutableStateOf(alumno.clasesPactadas) }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        IconButton(onClick = { if (clasesPactadas > 0) clasesPactadas-- }) {
                            Icon(Icons.Default.Remove, contentDescription = "Restar")
                        }
                        Text(
                            text = "$clasesPactadas",
                            fontSize = 20.sp,
                            modifier = Modifier.width(40.dp),
                            textAlign = TextAlign.Center
                        )
                        IconButton(onClick = { clasesPactadas++ }) {
                            Icon(Icons.Default.Add, contentDescription = "Sumar")
                        }
                    }

                    Spacer(Modifier.height(8.dp))
                    Button(onClick = {
                        alumno = alumno.copy(clasesPactadas = clasesPactadas)
                        viewModel.actualizarClasesPactadas(clasesPactadas)
                        viewModel.guardarAlumno()
                    }) {
                        Icon(Icons.Default.Save, contentDescription = "Guardar pactadas")
                        Spacer(Modifier.width(4.dp))
                        Text("Guardar Pactadas")
                    }

                    Spacer(Modifier.height(6.dp))
                    Text(
                        "Estado de pago: ${alumno.estadoPago}",
                        color = when (alumno.estadoPago) {
                            EstadoPago.ADELANTADO -> Color(0xFF2E7D32)
                            EstadoPago.PENDIENTE -> Color(0xFFFFA000)
                            EstadoPago.DEUDA -> Color(0xFFC62828)
                        },
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // ---------------- TABS ----------------
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
                0 -> {
                    // ---------------- TAB RESUMEN ----------------
                    Column(
                        Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text("Edad: ${alumno.datosPersonales.edad}")
                        Text("Altura: ${alumno.datosPersonales.altura} cm")
                        Text("Peso: ${alumno.datosPersonales.peso} kg")
                        Text("Dirección: ${alumno.datosPersonales.direccion ?: ""}")
                        Text("Teléfono: ${alumno.datosPersonales.telefono ?: ""}")
                        Text("Email: ${alumno.datosPersonales.email ?: ""}")

                        Spacer(Modifier.height(12.dp))
                        Text("Nivel actual:", fontWeight = FontWeight.Bold)

                        var nivelActual by rememberSaveable { mutableStateOf(alumno.nivelActual) }

                        NivelActualSelector(
                            nivelActual = nivelActual,
                            onNivelChange = { nuevoNivel ->
                                nivelActual = nuevoNivel
                                alumno = alumno.copy(nivelActual = nuevoNivel)
                                viewModel.guardarAlumno()
                            }
                        )

                        Spacer(Modifier.height(16.dp))
                        Text(
                            "Objetivo: ${alumno.objetivo ?: "Sin objetivo definido"}",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(Modifier.height(12.dp))

                        // --- Notas del entrenador ---
                        Text("Notas del entrenador:", fontWeight = FontWeight.Bold)
                        if (editandoNotas) {
                            OutlinedTextField(
                                value = notas,
                                onValueChange = { notas = it },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(120.dp)
                            )
                            Spacer(Modifier.height(8.dp))
                            Button(onClick = {
                                alumno = alumno.copy(notasEntrenador = notas)
                                viewModel.guardarAlumno()
                                editandoNotas = false
                            }) {
                                Icon(Icons.Default.Save, contentDescription = "Guardar")
                                Spacer(Modifier.width(4.dp))
                                Text("Guardar Notas")
                            }
                        } else {
                            Text(
                                notas.ifEmpty { "Sin notas registradas." },
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Spacer(Modifier.height(8.dp))
                            Button(onClick = { editandoNotas = true }) {
                                Icon(Icons.Default.Edit, contentDescription = "Editar")
                                Spacer(Modifier.width(4.dp))
                                Text("Editar Notas")
                            }
                        }
                    }
                }

                1 -> {
                    // ---------------- TAB SESIONES ----------------
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No hay sesiones registradas todavía.")
                    }
                }

                2 -> {
                    // ---------------- TAB TÁCTICAS ----------------
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No hay tácticas registradas.")
                    }
                }
            }
        }
    }
}
