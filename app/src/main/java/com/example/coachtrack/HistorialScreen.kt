package com.example.coachtrack

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class Sesion(
    val id: Int,
    val fecha: String,
    val descripcion: String,
    val duracion: Int
)

val SESIONES_MOCK = listOf(
    Sesion(1, "2025-09-01", "Trabajo de saque y bolea", 45),
    Sesion(2, "2025-09-03", "Revés cruzado y desplazamientos", 60),
    Sesion(3, "2025-09-05", "Juego de pies y control de volea", 50)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistorialScreen(onVolverClick: () -> Unit) {
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
            items(SESIONES_MOCK) { sesion ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                ) {
                    Column(Modifier.padding(12.dp)) {
                        Text(sesion.fecha, style = MaterialTheme.typography.titleMedium)
                        Text(sesion.descripcion)
                        Text("Duración: ${sesion.duracion} min", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}
