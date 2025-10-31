package com.example.coachtrack

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoAnalisisScreen(onVolverClick: () -> Unit) {
    val context = LocalContext.current

    var comentario by remember { mutableStateOf("") }
    var mostrarDialogo by remember { mutableStateOf(false) }
    var videoUri by remember { mutableStateOf<Uri?>(null) } // Guarda el video elegido

    // Selector de video del sistema (galería o archivos)
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        videoUri = uri
    }

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
            // 🔹 Vista del video o placeholder
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .background(Color(0xFFE0E0E0), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (videoUri != null) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.PlayCircle,
                            contentDescription = "Video cargado",
                            tint = Color(0xFF1565C0),
                            modifier = Modifier.size(80.dp)
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "Video seleccionado:\n${videoUri.toString()}",
                            fontSize = 12.sp,
                            color = Color(0xFF1565C0),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                    }
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.Videocam,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(80.dp)
                        )
                        Spacer(Modifier.height(8.dp))
                        Button(
                            onClick = { launcher.launch("video/*") },
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

            // 🔹 Botón Compartir Progreso
            Button(
                onClick = { mostrarDialogo = true },
                enabled = videoUri != null && comentario.isNotBlank(),
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E88E5))
            ) {
                Icon(Icons.Default.Share, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Compartir Progreso")
            }

            Spacer(Modifier.height(16.dp))
            Text(
                "Muestra tus clases, tu estilo y la evolución de tus alumnos.\n" +
                        "Convierte tu trabajo en tu mejor carta de presentación.",
                fontSize = 14.sp,
                color = Color.DarkGray,
                textAlign = TextAlign.Center
            )
        }
    }

    // 🔹 Diálogo de opciones de compartir real
    if (mostrarDialogo) {
        AlertDialog(
            onDismissRequest = { mostrarDialogo = false },
            confirmButton = {
                TextButton(onClick = { mostrarDialogo = false }) { Text("Cerrar") }
            },
            title = { Text("Compartir en...") },
            text = {
                Column {
                    ShareOption(
                        nombre = "Instagram",
                        icon = Icons.Default.Photo,
                        onClick = {
                            mostrarDialogo = false
                            abrirInstagram(context, "coachtrack.app")
                        }
                    )
                    ShareOption(
                        nombre = "WhatsApp",
                        icon = Icons.Default.Send,
                        onClick = {
                            mostrarDialogo = false
                            abrirWhatsApp(
                                context,
                                "+56912345678",
                                "🎾 Mira este video de entrenamiento hecho con CoachTrack!"
                            )
                        }
                    )
                    ShareOption(
                        nombre = "YouTube",
                        icon = Icons.Default.Videocam,
                        onClick = {
                            mostrarDialogo = false
                            compartirTexto(
                                context,
                                "Sube tu progreso a YouTube y etiqueta a @CoachTrack 🎾"
                            )
                        }
                    )
                    ShareOption(
                        nombre = "Telegram",
                        icon = Icons.Default.Message,
                        onClick = {
                            mostrarDialogo = false
                            compartirTexto(
                                context,
                                "Comparte tu video con tus grupos en Telegram 💪"
                            )
                        }
                    )
                }
            }
        )
    }
}

@Composable
fun ShareOption(
    nombre: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.width(8.dp))
        Text(nombre, fontSize = 16.sp, fontWeight = FontWeight.Medium)
    }
}

// ---------------- FUNCIONES AUXILIARES ----------------

fun compartirTexto(context: Context, mensaje: String) {
    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, mensaje)
        type = "text/plain"
    }
    val shareIntent = Intent.createChooser(sendIntent, "Compartir vía")
    context.startActivity(shareIntent)
}

fun abrirInstagram(context: Context, usuario: String? = null) {
    val uri = if (usuario != null) {
        Uri.parse("http://instagram.com/_u/$usuario")
    } else {
        Uri.parse("http://instagram.com/")
    }
    val intent = Intent(Intent.ACTION_VIEW, uri).apply {
        setPackage("com.instagram.android")
    }
    try {
        context.startActivity(intent)
    } catch (e: Exception) {
        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("http://instagram.com")))
    }
}

fun abrirWhatsApp(context: Context, telefono: String, mensaje: String) {
    val url = "https://api.whatsapp.com/send?phone=$telefono&text=${Uri.encode(mensaje)}"
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    context.startActivity(intent)
}
