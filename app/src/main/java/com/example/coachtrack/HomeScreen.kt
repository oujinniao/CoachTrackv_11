package com.example.coachtrack

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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onPlanificacionClick: () -> Unit,
    onVideoClick: () -> Unit
) {
    // Carga de datos
    val sesionesGuardadas = getMockSesionesGuardadas()
    val alumnos = remember { getMockAlumnos() }

    // Cálculo de deuda
    val alumnosConDeuda = alumnos.filter { it.estadoPago == EstadoPago.DEUDA }
    val alumnosPendientes = alumnos.filter { it.estadoPago == EstadoPago.PENDIENTE }
    val totalAlertas = alumnosConDeuda.size + alumnosPendientes.size
    val colorAlerta = if (alumnosConDeuda.isNotEmpty()) Color(0xFFE53935) else Color(0xFFFFC107)

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
            // 1. Botón de Acción Principal (Enfoque en la eficiencia)
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

            // 2. Alerta de Cartera (El dinero es prioridad)
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onPlanificacionClick) // Alerta clic lleva a planificación/cartera
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

            // 3. Botón de Video - ¡RENOMBRADO!
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
                    // Nuevo nombre para diferenciar su función de acceso rápido
                    Text("INICIAR GRABACIÓN Y ANÁLISIS", style = MaterialTheme.typography.titleMedium)
                }
            }

            // 4. Historial de Sesiones Recientes
            item {
                Text(
                    "Últimas Sesiones Guardadas:",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            items(sesionesGuardadas.take(3), key = { it.sessionId }) { sesion ->
                SesionGuardadaCard(sesion)
            }

            item {
                if (sesionesGuardadas.size > 3) {
                    Text(
                        "Ver todo el historial...",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp, bottom = 16.dp)
                            .clickable { /* Simulación de navegación a historial completo */ },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
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
                    "Fecha: ${sesion.fechaCreacion.format(DateTimeFormatter.ofPattern("dd MMM, hh:mm a", Locale("es")))}",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    "Duración: ${sesion.duracionTotalMinutos} min | ${sesion.ejercicios.size} ejercicios",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(Icons.Default.ArrowForward, contentDescription = "Ver Detalle")
        }
    }
}
