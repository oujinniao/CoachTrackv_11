package com.example.coachtrack

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun PantallaPrincipal(
    onPlanificarClick: () -> Unit,
    onHistorialClick: () -> Unit,
    onVideoAnalisisClick: () -> Unit,
    userId: String
) {
    Scaffold(
        topBar = { TopAppBarPrincipal() }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Prof. Alejandro González", style = MaterialTheme.typography.titleLarge)
                    Text("Academia Central de Tenis", style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("ID Usuario: $userId", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.secondary)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            BotonFuncionalidad("PLANIFICAR SESIÓN", "Crea tu clase en 2 clics", onPlanificarClick)
            Spacer(modifier = Modifier.height(16.dp))

            BotonFuncionalidad("HISTORIAL DE ALUMNOS", "Consulta informes de progreso individual", onHistorialClick)
            Spacer(modifier = Modifier.height(16.dp))

            BotonFuncionalidad("VIDEO ANÁLISIS RÁPIDO", "Graba y envía feedback visual", onVideoAnalisisClick)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBarPrincipal() {
    TopAppBar(title = { Text("CoachTrack 🎾") })
}

@Composable
fun BotonFuncionalidad(texto: String, descripcion: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
    ) {
        Column(horizontalAlignment = Alignment.Start, modifier = Modifier.fillMaxWidth()) {
            Text(texto, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(descripcion, style = MaterialTheme.typography.bodySmall)
        }
    }
}
