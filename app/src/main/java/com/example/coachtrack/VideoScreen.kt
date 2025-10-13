package com.example.coachtrack

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Draw
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoScreen(onVolver: () -> Unit = {}) {
    // Estados para simular la funcionalidad
    var velocidad by remember { mutableStateOf(1.0f) }
    var modoTrazado by remember { mutableStateOf(false) }
    var feedbackText by remember { mutableStateOf("Hola [Nombre Alumno], esta es mi nota de análisis...") }
    var isRecording by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Laboratorio de Video Análisis") },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Text("Atrás", fontSize = 14.sp)
                    }
                }
            )
        }
    ) { pv ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(pv)
        ) {
            // 1. ÁREA DE VISUALIZACIÓN DE VIDEO
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                // Simulación del video
                if (isRecording) {
                    Text("🔴 GRABANDO...", color = Color.White, fontSize = 24.sp)
                } else {
                    Icon(
                        Icons.Default.PlayArrow,
                        contentDescription = "Simulación de Video",
                        modifier = Modifier.size(64.dp),
                        tint = Color.White.copy(alpha = 0.8f)
                    )
                    Text(
                        "Video cargado o listo para grabar",
                        color = Color.White,
                        modifier = Modifier.align(Alignment.BottomCenter).padding(8.dp)
                    )
                }

                // Indicador de velocidad
                if (velocidad < 1.0f) {
                    Text(
                        "Velocidad: ${velocidad}x (Cámara Lenta)",
                        color = Color.Yellow,
                        modifier = Modifier.align(Alignment.TopStart).padding(8.dp)
                    )
                }

                // Indicador de trazado
                if (modoTrazado) {
                    Text(
                        "Modo: TRAZADO ACTIVO",
                        color = Color.Red,
                        modifier = Modifier.align(Alignment.TopEnd).padding(8.dp)
                    )
                }
            }

            // 2. CONTROLES Y HERRAMIENTAS PREMIUM
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Botón de Grabación/Inicio
                Button(
                    onClick = { isRecording = !isRecording },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isRecording) Color.Red else MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(Icons.Default.CameraAlt, contentDescription = "Grabar")
                    Spacer(Modifier.width(4.dp))
                    Text(if (isRecording) "DETENER" else "GRABAR")
                }

                // Control de Velocidad (Simulado)
                SpeedControlChip(
                    label = "Normal",
                    isSelected = velocidad == 1.0f,
                    onClick = { velocidad = 1.0f }
                )
                SpeedControlChip(
                    label = "0.5x",
                    isSelected = velocidad == 0.5f,
                    onClick = { velocidad = 0.5f }
                )
                SpeedControlChip(
                    label = "0.25x",
                    isSelected = velocidad == 0.25f,
                    onClick = { velocidad = 0.25f }
                )

                // Botón de Trazado
                IconButton(
                    onClick = { modoTrazado = !modoTrazado }
                ) {
                    Icon(
                        Icons.Default.Draw,
                        contentDescription = "Modo Trazado",
                        tint = if (modoTrazado) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // 3. SECCIÓN DE FEEDBACK
            Column(modifier = Modifier.padding(16.dp).weight(1f)) {
                Text("Feedback Rápido del Entrenador:", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = feedbackText,
                    onValueChange = { feedbackText = it },
                    label = { Text("Notas de corrección (Voz o Texto)") },
                    placeholder = { Text("Escribe tu análisis o pulsa el micrófono (simulado)") },
                    modifier = Modifier.fillMaxWidth().heightIn(min = 80.dp)
                )

                Spacer(Modifier.height(16.dp))

                // Botón de Enviar (Simulación de WhatsApp)
                Button(
                    onClick = {
                        // Simulación de guardar y enviar
                        println("--- ANÁLISIS ENVIADO ---")
                        println("Velocidad: $velocidad, Trazado: $modoTrazado")
                        println("Feedback: $feedbackText")
                        feedbackText = "Análisis enviado con éxito a Juan Pérez."
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) {
                    Text("GUARDAR ANÁLISIS Y COMPARTIR (WSP)")
                }
            }
        }
    }
}

// Componente auxiliar para los botones de velocidad
@Composable
fun SpeedControlChip(label: String, isSelected: Boolean, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
        ),
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
        )
    }
}
