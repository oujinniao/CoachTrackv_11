package com.example.coachtrack

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun VideoScreen(onVolver: () -> Unit = {}) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Pantalla de Video (en desarrollo)")
        Button(
            onClick = onVolver,
            modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp)
        ) {
            Text("Volver")
        }
    }
}
