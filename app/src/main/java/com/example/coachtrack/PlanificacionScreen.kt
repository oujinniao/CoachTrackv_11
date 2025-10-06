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

@Composable
fun PlanificacionScreen(
    onVolverClick: () -> Unit
) {
    // Estado de los ejercicios añadidos a la sesión
    val ejerciciosSesion = remember { mutableStateListOf<Plantilla>() }

    val onRemoveEjercicio: (Plantilla) -> Unit = { plantilla ->
        ejerciciosSesion.remove(plantilla)
    }

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
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(PLANTILLAS_MOCK, key = { it.id }) { plantilla ->
                    PlantillaCard(plantilla) { ejerciciosSesion.add(plantilla) }
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
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBarPlanificacion(onVolverClick: () -> Unit) {
    TopAppBar(
        title = { Text("Planificar Sesión") },
        navigationIcon = {
            IconButton(onClick = onVolverClick) {
                Text("ATRÁS")
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

@Composable
fun PlantillaCard(plantilla: Plantilla, onAdd: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onAdd() }
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(plantilla.nombre, style = MaterialTheme.typography.bodyMedium)
            Text("${plantilla.duracionMinutos} min - ${plantilla.enfoque}", style = MaterialTheme.typography.bodySmall)
        }
    }
}
