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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.firebase.firestore.FirebaseFirestore

/**
 * PANTALLA DE PLANIFICACIÓN: Permite crear una sesión rápidamente usando plantillas.
 */
@Composable
fun PlanificacionScreen(
    onVolverClick: () -> Unit,
    db: FirebaseFirestore,
    userId: String,
    appId: String
// Función para volver a la pantalla principal
) {
    // Lista mutable que guarda los ejercicios de la sesión
    val ejerciciosSesion = remember { mutableStateListOf<Plantilla>() }

    // Función para eliminar un ejercicio de la lista
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

            // Sección 1: Selector de Alumno (Fijo por ahora)
            Text(
                text = "Planificando para:",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 16.dp)
            )
            OutlinedTextField(
                value = "Juan Pérez (Objetivo: Revés)",
                onValueChange = { /* No hay lógica aún */ },
                label = { Text("Alumno Seleccionado") },
                readOnly = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            // ---------------------------------------------------------------------
            // SECCIÓN DE LISTA DE EJERCICIOS AÑADIDOS (Sesión Actual)
            // Usamos LazyColumn para que sea deslizable si hay muchos ejercicios.
            // ---------------------------------------------------------------------
            Text(
                text = "Sesión Actual (${ejerciciosSesion.size} Ejercicios)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Contenedor para la lista de ejercicios añadidos
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 200.dp)
                    .padding(bottom = 16.dp)
            ) {
                if (ejerciciosSesion.isEmpty()) {
                    Text("¡Añade tu primer ejercicio de la lista de plantillas abajo!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error)
                } else {
                    LazyColumn {
                        items(ejerciciosSesion, key = { it.id }) { plantilla ->
                            // Ahora pasamos la función para remover
                            EjercicioAñadidoCard(
                                plantilla = plantilla,
                                onRemove = onRemoveEjercicio
                            )
                        }
                    }
                }
            }


            // ---------------------------------------------------------------------
            // SECCIÓN DE PLANTILLAS DISPONIBLES (El "2do Clic")
            // ---------------------------------------------------------------------
            Text(
                text = "Plantillas Rápidas Disponibles:",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Start).padding(bottom = 8.dp)
            )

            // Lista de plantillas base
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(PLANTILLAS_MOCK, key = { it.id }) { plantilla ->
                    PlantillaCard(
                        plantilla = plantilla,
                        onPlantillaClick = { ejerciciosSesion.add(plantilla) }
                    )
                }
            }

            // Botón de guardar sesión
            Button(
                onClick = onVolverClick,
                enabled = ejerciciosSesion.isNotEmpty(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .padding(vertical = 4.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("GUARDAR Y VOLVER", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}

// Barra superior de la pantalla de Planificación
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

// Componente para mostrar un ejercicio que YA está en la sesión
@Composable
fun EjercicioAñadidoCard(plantilla: Plantilla, onRemove: (Plantilla) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Columna principal de texto
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${plantilla.nombre} (${plantilla.duracionMinutos} min)",
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Enfoque: ${plantilla.enfoque}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.width(8.dp))

            // Botón de eliminar (Icono de papelera)
            IconButton(onClick = { onRemove(plantilla) }) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "Eliminar ejercicio",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

// Componente para mostrar una plantilla disponible (La lista inferior)
@Composable
fun PlantillaCard(plantilla: Plantilla, onPlantillaClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(onClick = onPlantillaClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(plantilla.nombre, fontWeight = FontWeight.SemiBold)
                Text("Enfoque: ${plantilla.enfoque}", style = MaterialTheme.typography.bodySmall)
            }
            Text("${plantilla.duracionMinutos} min", color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.Bold)
        }
    }
}