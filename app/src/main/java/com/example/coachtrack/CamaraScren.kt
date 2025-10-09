package com.example.coachtrack

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CamaraScreen(onVolverClick: () -> Unit = {}) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Pantalla de Cámara (en desarrollo)")
        Button(
            onClick = onVolverClick,
            modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp)
        ) {
            Text("Volver")
        }
    }
}
