package com.example.coachtrack

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoAnalisisScreen(onVolverClick: () -> Unit) {
    var comentario by remember { mutableStateOf("") }
    var mostrarDialogo by remember { mutableStateOf(false) }
    var videoCargado by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Marketing / Portafolio") },
                navigationIcon = {
                    IconButton(onClick = onVolverClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { mostrarDialogo = true },
                containerColor = Color(0xFF1E88E5)
            ) {
                Icon(Icons.Default.Share, contentDescription = "Compartir")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 🔹 Simulación de carga de video
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .background(Color(0xFFE0E0E0), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (videoCargado) {
                    Icon(
                        Icons.Default.PlayCircle,
                        contentDescription = "Video cargado",
                        tint = Color(0xFF1565C0),
                        modifier = Modifier.size(80.dp)
                    )
                    Text(
                        "Video cargado correctamente",
                        color = Color(0xFF1565C0),
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.align(Alignment.BottomCenter).padding(12.dp)
                    )
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Videocam, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(80.dp))
                        Spacer(Modifier.height(8.dp))
                        Button(
                            onClick = { videoCargado = true },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2))
                        ) {
                            Icon(Icons.Default.Upload, contentDescription = null)
                            Spacer(Modifier.width(6.dp))
                            Text("Subir video")
                        }
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            // 🔹 Campo de comentario
            OutlinedTextField(
                value = comentario,
                onValueChange = { comentario = it },
                label = { Text("Comentario o descripción breve") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(24.dp))

            // 🔹 Botón compartir
            Button(
                onClick = { mostrarDialogo = true },
                enabled = videoCargado && comentario.isNotBlank(),
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E88E5))
            ) {
                Icon(Icons.Default.Share, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Compartir Progreso")
            }

            Spacer(Modifier.height(16.dp))
            Text(
                "Muestra tus clases, tu estilo y la evolución de tus alumnos.\nConvierte tu trabajo en tu mejor carta de presentación.",
                fontSize = 14.sp,
                color = Color.DarkGray,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }

    // 🔹 Diálogo de opciones de compartir
    if (mostrarDialogo) {
        AlertDialog(
            onDismissRequest = { mostrarDialogo = false },
            confirmButton = {
                TextButton(onClick = { mostrarDialogo = false }) {
                    Text("Cerrar")
                }
            },
            title = { Text("Compartir en...") },
            text = {
                Column {
                    ShareOption("Instagram", Icons.Default.Photo)
                    ShareOption("WhatsApp", Icons.Default.Send)
                    ShareOption("YouTube", Icons.Default.Videocam)
                    ShareOption("Telegram", Icons.Default.Message)
                }
            }
        )
    }
}

@Composable
fun ShareOption(nombre: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.width(8.dp))
        Text(nombre, fontSize = 16.sp, fontWeight = FontWeight.Medium)
    }
}
