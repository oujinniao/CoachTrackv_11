package com.example.coachtrack

import android.app.Application
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onVolverClick: () -> Unit,
    onPlanificacionClick: () -> Unit,
    onCarteraClick: () -> Unit,
    onAbrirFichaAlumno: (AlumnoEntity) -> Unit,
    onGestionClick: () -> Unit
) {
    val context = LocalContext.current

    val vm: DashboardViewModel = viewModel(
        factory = androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
            .getInstance(context.applicationContext as Application)
    )

    val state by vm.uiState.collectAsState()
//
    LaunchedEffect(state.total) {
        println("DASHBOARD -> total=${state.total}, filtro=${state.filtro}")
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dashboard") },
                navigationIcon = {
                    IconButton(onClick = onVolverClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    TextButton(onClick = onGestionClick) { Text("Gestión") }
                }
            )
        }
    ) { pv ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(pv)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {

            item {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = onPlanificacionClick,
                        modifier = Modifier.weight(1f)
                    ) { Text("Planificar") }

                    OutlinedButton(
                        onClick = onCarteraClick,
                        modifier = Modifier.weight(1f)
                    ) { Text("Cartera") }
                }
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        KpiCard(
                            title = "Total",
                            value = state.total,
                            selected = state.filtro == DashboardFiltro.TODOS,
                            onClick = { vm.setFiltro(DashboardFiltro.TODOS) },
                            modifier = Modifier.weight(1f)
                        )
                        KpiCard(
                            title = "Al día",
                            value = state.alDia,
                            selected = state.filtro == DashboardFiltro.AL_DIA,
                            onClick = { vm.setFiltro(DashboardFiltro.AL_DIA) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        KpiCard(
                            title = "Pendiente",
                            value = state.pendientes,
                            selected = state.filtro == DashboardFiltro.PENDIENTE,
                            onClick = { vm.setFiltro(DashboardFiltro.PENDIENTE) },
                            modifier = Modifier.weight(1f)
                        )
                        KpiCard(
                            title = "Deuda",
                            value = state.deuda,
                            selected = state.filtro == DashboardFiltro.DEUDA,
                            onClick = { vm.setFiltro(DashboardFiltro.DEUDA) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            item {
                Text(
                    text = "Alumnos (${state.alumnos.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            if (state.alumnos.isEmpty()) {
                item { Text("No hay alumnos en este filtro.") }
            } else {
                items(state.alumnos, key = { it.localId }) { alumno ->
                    AlumnoDashboardCard(
                        alumno = alumno,
                        onClick = { onAbrirFichaAlumno(alumno) }
                    )
                }
            }
        }
    }
}

@Composable
private fun KpiCard(
    title: String,
    value: Int,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = if (selected) {
        CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    } else {
        CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    }

    Card(
        modifier = modifier
            .height(78.dp)
            .clickable { onClick() },
        colors = colors
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(title, style = MaterialTheme.typography.labelLarge)
            Text(
                value.toString(),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun AlumnoDashboardCard(
    alumno: AlumnoEntity,
    onClick: () -> Unit
) {
    val cursadas = alumno.clasesCursadas
    val pactadas = alumno.clasesPactadas
    val restantes = (pactadas - cursadas).coerceAtLeast(0)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column(Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(alumno.nombre, fontWeight = FontWeight.Bold)
                    Text(
                        "Clases: $cursadas / $pactadas  • Restan: $restantes",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                AssistChip(
                    onClick = {},
                    label = { Text(alumno.estadoPago) }
                )
            }
        }
    }
}
