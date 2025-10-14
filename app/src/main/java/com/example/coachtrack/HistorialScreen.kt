package com.example.coachtrack

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistorialScreen(onVolverClick: () -> Unit) {
    val sesiones = remember { mutableStateListOf<Sesion>().apply {
        addAll(SesionRepository.obtenerSesiones())
    }}

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Historial de Sesiones") },
                navigationIcon = {
                    IconButton(onClick = onVolverClick) {
                        Text("Atrás", fontSize = 14.sp)
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            if (sesiones.isEmpty()) {
                item {
                    Text(
                        "No hay sesiones registradas aún.",
                        modifier = Modifier.fillMaxWidth(),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            } else {
                items(sesiones) { sesion ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                    ) {
                        Column(Modifier.padding(12.dp)) {
                            Text(
                                sesion.titulo,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text("Fecha: ${sesion.fecha}")
                            Text("Duración: ${sesion.duracionMin} min")
                            Text("Estado: ${sesion.estado}")
                        }
                    }
                }
            }
        }
    }
}
