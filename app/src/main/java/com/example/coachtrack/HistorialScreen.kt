package com.example.coachtrack

import androidx.activity.compose.BackHandler
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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlin.jvm.java

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistorialScreen(onVolverClick: () -> Unit) {

    BackHandler { onVolverClick() }

    val context = LocalContext.current
    val app = context.applicationContext as CoachTrackApplication

    val sesionViewModel: SesionViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(SesionViewModel::class.java)) {
                    @Suppress("UNCHECKED_CAST")
                    return SesionViewModel(
                        application = app,
                        repository = app.container.sesionRepository
                    ) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    )

    val sesiones by sesionViewModel.sesionesGenerales.collectAsState()

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
            val sesionesAgrupadas = sesiones.groupBy { it.alumnoNombre }

            val coloresGrupo = listOf(
                Color(0xFFE3F2FD), Color(0xFFE8F5E9), Color(0xFFFFF3E0),
                Color(0xFFF3E5F5), Color(0xFFFFEBEE)
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(pv)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                sesionesAgrupadas.entries.toList().forEachIndexed { index, entry ->
                    val alumno = entry.key
                    val listaSesiones = entry.value
                    val colorFondo = coloresGrupo[index % coloresGrupo.size]

                    item(key = "header_$alumno") {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(colorFondo.copy(alpha = 0.7f))
                                .padding(12.dp)
                        ) {
                            Text(
                                text = "Alumno: $alumno",
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

                    items(
                        items = listaSesiones.sortedByDescending { it.fecha },
                        key = { sesion -> sesion.id }
                    ) { sesion ->
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
                                        "Estado: Completada"
                                    else
                                        "Estado: Pendiente",
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
