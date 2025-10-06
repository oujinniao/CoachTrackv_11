package com.example.coachtrack

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoAnalisisScreen(onVolverClick: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Video Análisis") },
                navigationIcon = {
                    IconButton(onClick = onVolverClick) {
                        Text("ATRÁS")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Selecciona un video para analizar", style = MaterialTheme.typography.titleMedium)

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Video: Saque de entrenamiento", style = MaterialTheme.typography.titleMedium)
                        Text("Duración: 3:24")
                    }
                    IconButton(onClick = { /* Aquí iría la acción de reproducir */ }) {
                        Icon(Icons.Default.PlayArrow, contentDescription = "Reproducir")
                    }
                }
            }
        }
    }
}
