package com.example.coachtrack

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Importamos la función para obtener los datos de Data.kt
import com.example.coachtrack.getMockPlantillas

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanificacionScreen(
    onVolverClick: () -> Unit
) {
    // Llama directamente a la función de Data.kt para obtener la lista
    val plantillasDisponibles = getMockPlantillas()

    val ejerciciosSesion = remember { mutableStateListOf<Plantilla>() }
    val onRemoveEjercicio: (Plantilla) -> Unit = { ejerciciosSesion.remove(it) }

    var plantillaSeleccionada by remember { mutableStateOf<Plantilla?>(null) }

    var mostrarCartera by remember { mutableStateOf(false) }
    var mostrarVideo by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { TopAppBarPlanificacion(onVolverClick) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {

            Text("Planificando para:", style = MaterialTheme.typography.titleMedium)
            OutlinedTextField(
                value = "Juan Pérez (Objetivo: Revés)",
                onValueChange = {},
                label = { Text("Alumno Seleccionado") },
                readOnly = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text("Sesión Actual (${ejerciciosSesion.size} Ejercicios)", style = MaterialTheme.typography.titleMedium)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 200.dp)
                    .padding(bottom = 16.dp)
            ) {
                if (ejerciciosSesion.isEmpty()) {
                    Text(
                        "¡Añade tu primer ejercicio de la lista de plantillas abajo!",
                        color = MaterialTheme.colorScheme.error
                    )
                } else {
                    LazyColumn {
                        items(ejerciciosSesion, key = { it.id }) { plantilla ->
                            EjercicioAñadidoCard(plantilla, onRemoveEjercicio)
                        }
                    }
                }
            }

            Text("Plantillas Rápidas Disponibles:", style = MaterialTheme.typography.titleMedium)

            // Lógica de navegación del detalle (manteniendo el esqueleto)
            if (plantillaSeleccionada != null) {
                // Aquí iría PlantillaDetailScreen
                Text("Mostrando detalle de: ${plantillaSeleccionada!!.nombre}", color = MaterialTheme.colorScheme.primary)
                Button(onClick = { ejerciciosSesion.add(plantillaSeleccionada!!); plantillaSeleccionada = null }) {
                    Text("Añadir y Volver")
                }
                Button(onClick = { plantillaSeleccionada = null }) {
                    Text("Volver a la Lista")
                }
            } else {
                LazyColumn(modifier = Modifier.weight(1f)) {
                    // Usa la lista cargada con la función de Data.kt
                    items(plantillasDisponibles, key = { it.id }) { plantilla ->
                        PlantillaCard(
                            plantilla = plantilla,
                            onAdd = { ejerciciosSesion.add(plantilla) }, // Acción de añadir directo
                            onDetailClick = { plantillaSeleccionada = it } // Acción de ver detalle
                        )
                    }
                }
            }

            Button(
                onClick = onVolverClick,
                enabled = ejerciciosSesion.isNotEmpty(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .padding(vertical = 4.dp)
            ) {
                Text("GUARDAR Y VOLVER")
            }

            Spacer(modifier = Modifier.height(8.dp))

            /* Botón CARTERA */
            Button(
                onClick = { mostrarCartera = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cartera")
            }

            Spacer(modifier = Modifier.height(8.dp))

            /* Botón VIDEO */
            Button(
                onClick = { mostrarVideo = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Video")
            }

            Spacer(modifier = Modifier.height(8.dp))

            /* NAVEGACIÓN - CORRECCIÓN CLAVE */
            when {
                mostrarCartera -> CarteraScreen(onVolver = { mostrarCartera = false })
                mostrarVideo -> VideoScreen(onVolver = { mostrarVideo = false })
                // ELIMINAMOS EL DEFAULT (else) PARA QUE NO RENDERICE LA CAMARA ENCIMA
            }
        }
    }
}

/* ---------- Resto de funciones ---------- */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBarPlanificacion(onVolverClick: () -> Unit) {
    TopAppBar(
        title = { Text("Planificar Sesión") },
        navigationIcon = {
            IconButton(onClick = onVolverClick) {
                Text("Atrás", fontSize = 14.sp)
            }
        }
    )
}

@Composable
fun EjercicioAñadidoCard(plantilla: Plantilla, onRemove: (Plantilla) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(plantilla.nombre, style = MaterialTheme.typography.bodyMedium)
                Text("${plantilla.duracionMinutos} min - ${plantilla.enfoque}", style = MaterialTheme.typography.bodySmall)
            }
            IconButton(onClick = { onRemove(plantilla) }) {
                Icon(Icons.Default.Delete, contentDescription = "Eliminar")
            }
        }
    }
}

// CORRECCIÓN DE PLANTILLACARD CON DETALLE Y AÑADIR
@Composable
fun PlantillaCard(plantilla: Plantilla, onAdd: () -> Unit, onDetailClick: (Plantilla) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onDetailClick(plantilla) } // Clic principal abre detalle
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(plantilla.nombre, style = MaterialTheme.typography.bodyMedium)
                Text(
                    "${plantilla.duracionMinutos} min - ${plantilla.enfoque}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            // Botón para añadir el ejercicio (no activa el detalle)
            Button(onClick = onAdd) {
                Text("Añadir")
            }
        }
    }
}

