package com.example.coachtrack

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FichaAlumnoScreen(
    alumnoInicial: Alumnos,
    onVolver: () -> Unit,
    onNuevaSesionClick: (Alumnos) -> Unit
) {
    var alumno by remember { mutableStateOf(alumnoInicial) }
    var selectedTab by remember { mutableStateOf(0) }
    var notas by remember { mutableStateOf(alumno.notasEntrenador) }
    var editandoNotas by remember { mutableStateOf(false) }

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
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Edit, contentDescription = "Nueva Sesión")
            }

        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // ------------------ Encabezado con progreso ------------------
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
                    Text(alumno.nivel, fontWeight = FontWeight.Bold, color = Color.Gray)
                    Spacer(Modifier.height(8.dp))
                    CircularProgressIndicator(
                        progress = alumno.progreso / 100f,
                        strokeWidth = 8.dp,
                        modifier = Modifier.size(80.dp)
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "${alumno.progreso}% completado",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "Clases ${alumno.clasesCursadas} / ${alumno.clasesPactadas}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.DarkGray
                    )
                    Spacer(Modifier.height(4.dp))
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

            // ------------------ Tabs ------------------
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
                    // ------------------ TAB 1: RESUMEN ------------------
                    Column(
                        Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        Text(
                            "Objetivo: ${alumno.objetivo ?: "Sin objetivo definido"}",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(Modifier.height(12.dp))

                        Text("Notas del entrenador:", fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(4.dp))

                        if (editandoNotas) {
                            OutlinedTextField(
                                value = notas,
                                onValueChange = { notas = it },
                                modifier = Modifier.fillMaxWidth().height(120.dp)
                            )
                            Spacer(Modifier.height(8.dp))
                            Button(onClick = {
                                alumno = alumno.copy(notasEntrenador = notas)
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
                    // ------------------ TAB 2: SESIONES ------------------
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (alumno.sesiones.isEmpty()) {
                            item {
                                Text(
                                    "No hay sesiones registradas aún.",
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        } else {
                            items(alumno.sesiones) { sesion ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
                                ) {
                                    Column(Modifier.padding(12.dp)) {
                                        Text(
                                            sesion.titulo,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text("Fecha: ${sesion.fecha}")
                                        Text("Duración: ${sesion.duracionMin} min")
                                        Text("Estado: ${sesion.estado}")
                                    }
                                }
                            }
                        }
                    }
                }

                2 -> {
                    // ------------------ TAB 3: TÁCTICAS ------------------
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (alumno.tacticas.isEmpty()) {
                            item {
                                Text(
                                    "No hay tácticas registradas.",
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        } else {
                            items(alumno.tacticas) { tactica ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD))
                                ) {
                                    Column(Modifier.padding(12.dp)) {
                                        Text(
                                            tactica.titulo,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(tactica.descripcion)
                                        Text("Nivel: ${tactica.nivel}", color = Color.Gray)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
