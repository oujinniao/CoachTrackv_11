package com.example.coachtrack

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistorialScreen(
    onVolverClick: () -> Unit
) {
    val vm: HistorialViewModel = viewModel()
    val state by vm.uiState.collectAsState()

    BackHandler { onVolverClick() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Historial") },
                navigationIcon = {
                    IconButton(onClick = onVolverClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { pv ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(pv)
                .padding(horizontal = 16.dp)
        ) {
            OutlinedTextField(
                value = state.query,
                onValueChange = vm::setQuery,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                label = { Text("Buscar alumno") },
                singleLine = true
            )

            Spacer(Modifier.height(12.dp))

            Text(
                text = "Alumnos (${state.alumnosFiltrados.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(8.dp))

            if (state.alumnosFiltrados.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No hay alumnos que coincidan con la búsqueda.")
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(state.alumnosFiltrados, key = { it.localId }) { alumno ->
                        val seleccionado = state.alumnoSeleccionado?.localId == alumno.localId
                        AlumnoHistorialItem(
                            alumno = alumno,
                            seleccionado = seleccionado,
                            sesiones = if (seleccionado) state.sesionesDelSeleccionado else emptyList(),
                            onClick = { vm.seleccionarAlumno(alumno.localId) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AlumnoHistorialItem(
    alumno: AlumnoEntity,
    seleccionado: Boolean,
    sesiones: List<SesionEntity>,
    onClick: () -> Unit
) {
    val chipColors = when (alumno.estadoPago) {
        EstadoPago.ADELANTADO.name -> AssistChipDefaults.assistChipColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            labelColor = MaterialTheme.colorScheme.onSecondaryContainer
        )
        EstadoPago.PENDIENTE.name -> AssistChipDefaults.assistChipColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
            labelColor = MaterialTheme.colorScheme.onTertiaryContainer
        )
        EstadoPago.DEUDA.name -> AssistChipDefaults.assistChipColors(
            containerColor = MaterialTheme.colorScheme.errorContainer,
            labelColor = MaterialTheme.colorScheme.onErrorContainer
        )
        else -> AssistChipDefaults.assistChipColors()
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = if (seleccionado)
            CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        else
            CardDefaults.cardColors()
    ) {
        Column(Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(alumno.nombre, fontWeight = FontWeight.Bold)
                    Text(
                        text = "Nivel: ${alumno.nivelActual} • Objetivo: ${alumno.objetivo}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                AssistChip(
                    onClick = {},
                    label = { Text(alumno.estadoPago) },
                    colors = chipColors
                )
            }

            if (seleccionado) {
                Spacer(Modifier.height(10.dp))
                HorizontalDivider()
                Spacer(Modifier.height(10.dp))

                Text(
                    text = "Sesiones (${sesiones.size})",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Medium
                )

                Spacer(Modifier.height(8.dp))

                if (sesiones.isEmpty()) {
                    Text(
                        "Este alumno aún no tiene sesiones registradas.",
                        style = MaterialTheme.typography.bodySmall
                    )
                } else {
                    sesiones.take(6).forEach { s ->
                        SesionRow(s)
                        Spacer(Modifier.height(6.dp))
                    }
                    if (sesiones.size > 6) {
                        Text(
                            "Mostrando las 6 sesiones más recientes de ${sesiones.size}.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SesionRow(s: SesionEntity) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(Modifier.padding(10.dp)) {
            Text(s.fecha, fontWeight = FontWeight.Medium)
            Text(
                text = "Duración: ${s.duracion} min • Ejercicios: ${s.ejercicios}",
                style = MaterialTheme.typography.bodySmall
            )
            if (s.notas.isNotBlank()) {
                Text(
                    text = "Notas: ${s.notas}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}