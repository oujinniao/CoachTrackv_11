package com.example.coachtrack

import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistorialScreen(onVolverClick: () -> Unit) {
    val context = LocalContext.current
    val sesionViewModel: SesionViewModel = viewModel(
        factory = androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
            .getInstance(context.applicationContext as Application)
    )

    val sesiones by sesionViewModel.sesiones.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Historial de Sesiones") },
                navigationIcon = {
                    IconButton(onClick = onVolverClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { pv ->
        if (sesiones.isEmpty()) {
            // 🔸 Caso sin sesiones
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(pv),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("No hay sesiones registradas")
                Text(
                    "Las sesiones aparecerán aquí cuando las crees",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        } else {
            // 🔹 Agrupar sesiones por nombre de alumno
            val sesionesAgrupadas = sesiones.groupBy { it.alumnoNombre }

            // Paleta de colores suaves alternados
            val coloresGrupo = listOf(
                Color(0xFFE3F2FD), // Azul claro
                Color(0xFFE8F5E9), // Verde claro
                Color(0xFFFFF3E0), // Naranja claro
                Color(0xFFF3E5F5), // Violeta claro
                Color(0xFFFFEBEE)  // Rosa claro
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(pv)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                sesionesAgrupadas.entries.forEachIndexed { index, (alumno, listaSesiones) ->
                    val colorFondo = coloresGrupo[index % coloresGrupo.size]

                    item {
                        // 🔹 Encabezado del alumno con fondo suave
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(colorFondo.copy(alpha = 0.7f))
                                .padding(12.dp)
                        ) {
                            Text(
                                text = "👤 $alumno",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary
                            )
                            Divider(
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                                thickness = 1.dp,
                                modifier = Modifier.padding(vertical = 6.dp)
                            )
                        }
                    }

                    // 🔹 Sesiones del alumno (ordenadas por fecha descendente)
                    items(listaSesiones.sortedByDescending { it.fecha }, key = { it.id }) { sesion ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 4.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = colorFondo.copy(alpha = 0.4f)
                            )
                        ) {
                            Column(Modifier.padding(12.dp)) {
                                Text(
                                    text = "Fecha: ${sesion.fecha}",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = Color(0xFF333333)
                                )
                                Text(
                                    text = "Duración: ${sesion.duracion} min",
                                    style = MaterialTheme.typography.bodySmall
                                )
                                Text(
                                    text = "Ejercicios: ${sesion.ejercicios}",
                                    style = MaterialTheme.typography.bodySmall
                                )

                                if (sesion.notas.isNotBlank()) {
                                    Spacer(Modifier.height(4.dp))
                                    Text(
                                        text = "Notas: ${sesion.notas}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.Gray
                                    )
                                }

                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = if (sesion.completada)
                                        "Estado: Completada ✅"
                                    else
                                        "Estado: Pendiente ⏳",
                                    color = if (sesion.completada)
                                        Color(0xFF2E7D32)
                                    else
                                        Color(0xFFFFA000),
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
