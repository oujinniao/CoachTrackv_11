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
import androidx.core.content.FileProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.io.path.createTempFile

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoAnalisisScreen(onVolverClick: () -> Unit) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var videoUri by remember { mutableStateOf<Uri?>(null) }
    var titulo by remember { mutableStateOf("") }
    var comentario by remember { mutableStateOf("") }
    var mostrarDialogo by remember { mutableStateOf(false) }
    var modoDemo by remember { mutableStateOf(false) }
    var mensajeSeleccionado by remember { mutableStateOf("") }

    // 🧩 Archivo temporal para grabar video real
    val videoTempFile = remember {
        val file = createTempFile("video_temp_", ".mp4").toFile()
        file.deleteOnExit()
        file
    }
    val videoUriForCamara = remember {
        FileProvider.getUriForFile(
            context,
            context.packageName + ".provider",
            videoTempFile
        )
    }

    // 📱 Lanzadores
    val launcherArchivo = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) videoUri = uri
    }

    val launcherCamara = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CaptureVideo()
    ) { success ->
        if (success) {
            videoUri = videoUriForCamara
            scope.launch { snackbarHostState.showSnackbar("🎥 Video grabado exitosamente") }
        } else {
            scope.launch { snackbarHostState.showSnackbar("⚠️ No se pudo grabar el video") }
        }
    }

    val mensajesPredeterminados = listOf(
        "🎾 Seguimos avanzando con técnica y pasión 💪 #CoachTrack",
        "🔥 Gran trabajo en cancha hoy, seguimos mejorando cada día.",
        "💫 Entrenamiento personalizado para resultados reales. #TenisPro",
        "🏅 Progreso constante — la práctica hace al maestro. #CoachTrack"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("🎥 Portafolio de Clases") },
                navigationIcon = {
                    IconButton(onClick = onVolverClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { mostrarDialogo = true },
                containerColor = Color(0xFF1E88E5)
            ) { Icon(Icons.Default.Share, contentDescription = "Compartir") }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // 🎞 Vista del video
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
                            text = "Video listo para compartir",
                            color = Color(0xFF1565C0)
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
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = { launcherArchivo.launch("video/*") },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2))
                            ) {
                                Icon(Icons.Default.Upload, contentDescription = null)
                                Spacer(Modifier.width(6.dp))
                                Text("Subir video")
                            }
                            Button(
                                onClick = { launcherCamara.launch(videoUriForCamara) },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF43A047))
                            ) {
                                Icon(Icons.Default.Videocam, contentDescription = null)
                                Spacer(Modifier.width(6.dp))
                                Text("Grabar video")
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            OutlinedTextField(
                value = titulo,
                onValueChange = { titulo = it },
                label = { Text("Título del video") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = comentario,
                onValueChange = { comentario = it },
                label = { Text("Comentario o descripción breve") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))
            Text("Plantillas de mensaje:", style = MaterialTheme.typography.labelMedium)
            DropdownMenuDemo(
                mensajesPredeterminados,
                onSeleccion = { mensajeSeleccionado = it; comentario = it }
            )

            Spacer(Modifier.height(20.dp))

            // Vista previa
            if (titulo.isNotBlank() || comentario.isNotBlank()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
                ) {
                    Column(Modifier.padding(12.dp)) {
                        Text(titulo, fontWeight = FontWeight.Bold, color = Color(0xFF1565C0))
                        Text(comentario, color = Color.DarkGray)
                        Spacer(Modifier.height(6.dp))
                        Text(
                            "👤 Prof. Alejandro González – Academia Central 🎾",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Switch(checked = modoDemo, onCheckedChange = { modoDemo = it })
                Spacer(Modifier.width(8.dp))
                Text("Modo demostrativo (no publica)", color = Color.Gray)
            }

            Spacer(Modifier.height(16.dp))
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
        }
    }

    // 🔹 Diálogo de compartir redes
    if (mostrarDialogo && !modoDemo) {
        DialogCompartir(context, onCerrar = { mostrarDialogo = false }, scope, snackbarHostState)
    } else if (mostrarDialogo && modoDemo) {
        scope.launch { snackbarHostState.showSnackbar("🎾 Modo demo: no se publicó ningún contenido.") }
        mostrarDialogo = false
    }
}

@Composable
fun DropdownMenuDemo(mensajes: List<String>, onSeleccion: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Box(Modifier.fillMaxWidth()) {
        OutlinedButton(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth()) {
            Icon(Icons.Default.Message, contentDescription = null)
            Spacer(Modifier.width(6.dp))
            Text("Seleccionar plantilla")
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            mensajes.forEach { msg ->
                DropdownMenuItem(text = { Text(msg, fontSize = 13.sp) }, onClick = {
                    onSeleccion(msg)
                    expanded = false
                })
            }
        }
    }
}

@Composable
fun DialogCompartir(
    context: Context,
    onCerrar: () -> Unit,
    scope: CoroutineScope,
    snackbarHostState: SnackbarHostState
) {
    AlertDialog(
        onDismissRequest = onCerrar,
        confirmButton = { TextButton(onClick = onCerrar) { Text("Cerrar") } },
        title = { Text("Compartir en...") },
        text = {
            Column {
                ShareOption("Instagram", Icons.Default.Photo) {
                    abrirInstagram(context, "coachtrack.app")
                    scope.launch { snackbarHostState.showSnackbar("📸 Compartido en Instagram") }
                    onCerrar()
                }
                ShareOption("WhatsApp", Icons.Default.Send) {
                    abrirWhatsApp(context, "+56912345678", "🎾 Mira este video de entrenamiento hecho con CoachTrack!")
                    scope.launch { snackbarHostState.showSnackbar("💬 Enviado por WhatsApp") }
                    onCerrar()
                }
                ShareOption("Telegram", Icons.Default.Message) {
                    compartirTexto(context, "Comparte tu video con tus grupos en Telegram 💪")
                    scope.launch { snackbarHostState.showSnackbar("📢 Compartido en Telegram") }
                    onCerrar()
                }
                ShareOption("YouTube", Icons.Default.Videocam) {
                    compartirTexto(context, "Sube tu progreso a YouTube y etiqueta a @CoachTrack 🎾")
                    scope.launch { snackbarHostState.showSnackbar("▶️ Publicado en YouTube") }
                    onCerrar()
                }
            }
        }
    )
}

@Composable
fun ShareOption(nombre: String, icon: ImageVector, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable { onClick() }.padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.width(8.dp))
        Text(nombre, fontSize = 16.sp, fontWeight = FontWeight.Medium)
    }
}

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
    val uri = if (usuario != null) Uri.parse("http://instagram.com/_u/$usuario")
    else Uri.parse("http://instagram.com/")
    val intent = Intent(Intent.ACTION_VIEW, uri).apply { setPackage("com.instagram.android") }
    try { context.startActivity(intent) }
    catch (e: Exception) { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("http://instagram.com"))) }
}

fun abrirWhatsApp(context: Context, telefono: String, mensaje: String) {
    val url = "https://api.whatsapp.com/send?phone=$telefono&text=${Uri.encode(mensaje)}"
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    context.startActivity(intent)
}
