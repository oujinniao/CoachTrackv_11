package com.example.coachtrack

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MiniPlanificacionScreen(
    alumno: Alumnos,
    onVolver: () -> Unit
) {
    val ejerciciosSeleccionados = remember { mutableStateListOf<Plantilla>() }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nueva sesión para ${alumno.nombre}") },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->

        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            Text(
                "Selecciona ejercicios de la biblioteca (${ejerciciosSeleccionados.size} elegidos):",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(PLANTILLAS_MOCK, key = { it.id }) { plantilla ->
                    val seleccionada = ejerciciosSeleccionados.contains(plantilla)
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (seleccionada)
                                MaterialTheme.colorScheme.secondaryContainer
                            else
                                MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(Modifier.weight(1f)) {
                                Text(plantilla.nombre, style = MaterialTheme.typography.titleSmall)
                                Text(
                                    "Duración: ${plantilla.duracionMinutos} min | Enfoque: ${plantilla.enfoque}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                            Button(onClick = {
                                if (seleccionada) ejerciciosSeleccionados.remove(plantilla)
                                else ejerciciosSeleccionados.add(plantilla)
                            }) {
                                Icon(
                                    if (seleccionada) Icons.Default.Check else Icons.Default.Add,
                                    contentDescription = "Seleccionar"
                                )
                                Spacer(Modifier.width(4.dp))
                                Text(if (seleccionada) "Quitar" else "Agregar")
                            }
                        }
                    }
                }
            }

            Divider(Modifier.padding(vertical = 8.dp))

            // Cálculo total de tiempo
            val totalMinutos = ejerciciosSeleccionados.sumOf { it.duracionMinutos }
            Text("Duración total estimada: $totalMinutos minutos")

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = {
                    if (ejerciciosSeleccionados.isEmpty()) {
                        scope.launch {
                            snackbarHostState.showSnackbar("Selecciona al menos un ejercicio.")
                        }
                        return@Button
                    }

                    val nuevaSesion = SesionDeClase(
                        sessionId = generarNuevoSessionId(getMockSesionesGuardadas()),
                        userId = "PROTOTIPO_DEMO",
                        fechaCreacion = LocalDateTime.now().format(
                            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
                        ),
                        alumnoNombre = alumno.nombre,
                        duracionTotalMinutos = totalMinutos,
                        ejercicios = ejerciciosSeleccionados.toMutableList()
                    )

                    // Guardamos en el mock
                    getMockSesionesGuardadas().add(0, nuevaSesion)

                    scope.launch {
                        snackbarHostState.showSnackbar("✅ Sesión guardada para ${alumno.nombre}")
                    }

                    onVolver()
                },
                enabled = ejerciciosSeleccionados.isNotEmpty(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("💾 Guardar Sesión")
            }
        }
    }
}
