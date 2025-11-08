package com.example.coachtrack

import android.app.Application
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onPlanificacionClick: () -> Unit,
    onVideoClick: () -> Unit,
    onCarteraClick: () -> Unit // Agregar este parámetro para navegar a cartera
) {
    // ViewModel para datos reales
    val context = LocalContext.current
    val viewModel: CarteraViewModel = viewModel(
        factory = androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
            .getInstance(context.applicationContext as Application)
    )

    val alumnos by viewModel.alumnos.collectAsState()

    // Cálculo de deuda con datos reales
    val alumnosConDeuda = alumnos.filter { it.estadoPago == EstadoPago.DEUDA }
    val alumnosPendientes = alumnos.filter { it.estadoPago == EstadoPago.PENDIENTE }
    val totalAlertas = alumnosConDeuda.size + alumnosPendientes.size
    val colorAlerta = if (alumnosConDeuda.isNotEmpty()) Color(0xFFE53935) else Color(0xFFFFC107)

    // Sesiones de ejemplo temporal (puedes reemplazar con datos reales después)
    val sesionesEjemplo = remember {
        listOf(
            SesionDeClase(
                sessionId = "s1",
                fechaCreacion = "2025-01-15 10:00",
                alumnoNombre = "Ejemplo Alumno",
                duracionTotalMinutos = 60,
                ejercicios = mutableListOf()
            )
        )
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("CoachTrack - Tablero de Control") }) }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            // 1. Botón de Acción Principal
            item {
                Button(
                    onClick = onPlanificacionClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .padding(bottom = 16.dp),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("PLANIFICAR NUEVA SESIÓN", style = MaterialTheme.typography.titleMedium)
                }
            }

            // 2. Alerta de Cartera (ahora navega a cartera)
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onCarteraClick) // Ahora va a cartera
                        .padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (totalAlertas > 0) colorAlerta.copy(alpha = 0.1f) else MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Warning,
                            contentDescription = "Alerta de Pago",
                            tint = if (totalAlertas > 0) colorAlerta else MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("ESTADO DE PAGOS", style = MaterialTheme.typography.titleSmall)
                            if (totalAlertas > 0) {
                                Text(
                                    "🚨 ${alumnosConDeuda.size} DEUDA + ${alumnosPendientes.size} PENDIENTES. Revisar Cartera.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = colorAlerta
                                )
                            } else {
                                Text(
                                    "Todos los alumnos al día. ¡Excelente gestión!",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }
                        }
                    }
                }
            }

            // 3. Botón de Video
            item {
                Button(
                    onClick = onVideoClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    Icon(Icons.Default.CameraAlt, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("INICIAR GRABACIÓN Y ANÁLISIS", style = MaterialTheme.typography.titleMedium)
                }
            }

            // 4. Resumen de Alumnos
            item {
                Text(
                    "Resumen de Alumnos:",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Total de alumnos: ${alumnos.size}")
                        Text("Al día: ${alumnos.count { it.estadoPago == EstadoPago.ADELANTADO }}")
                        Text("Pendientes: ${alumnosPendientes.size}")
                        Text("En deuda: ${alumnosConDeuda.size}")
                    }
                }
            }

            // 5. Sesiones Recientes (ejemplo temporal)
            item {
                Text(
                    "Últimas Sesiones:",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            items(sesionesEjemplo, key = { it.sessionId }) { sesion ->
                SesionGuardadaCard(sesion)
            }
        }
    }
}

@Composable
fun SesionGuardadaCard(sesion: SesionDeClase) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Sesión con ${sesion.alumnoNombre}",
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    "Fecha: ${sesion.fechaCreacion}",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    "Duración: ${sesion.duracionTotalMinutos} min",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(Icons.Default.ArrowForward, contentDescription = "Ver Detalle")
        }
    }
}