package com.example.coachtrack

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoAnalisisScreen(
    onVolverClick: () -> Unit,
    perfilViewModel: PerfilProfesorViewModel = viewModel()
) {
    val perfil by perfilViewModel.perfil.collectAsState()

    BackHandler { onVolverClick() }

    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var videoUri by remember { mutableStateOf<Uri?>(null) }
    var titulo by remember { mutableStateOf("") }
    var comentario by remember { mutableStateOf("") }
    var mostrarDialogo by remember { mutableStateOf(false) }
    var modoDemo by remember { mutableStateOf(false) }
    var mensajeSeleccionado by remember { mutableStateOf("") }

    val firmaProfesor = "👤 ${perfil.nombreProfesor} – ${perfil.academia} 🎾"

    val videoTempFile = remember {
        File.createTempFile("video_temp_", ".mp4", context.cacheDir).also {
            it.deleteOnExit()
        }
    }
    val videoUriForCamara = remember {
        FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            videoTempFile
        )
    }

    val launcherArchivo = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            videoUri = uri
            scope.launch { snackbarHostState.showSnackbar("✅ Video cargado desde galería") }
        }
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
                onClick = {
                    if (videoUri != null && comentario.isNotBlank()) {
                        mostrarDialogo = true
                    } else {
                        scope.launch {
                            if (videoUri == null) {
                                snackbarHostState.showSnackbar("📹 Primero carga o graba un video")
                            } else {
                                snackbarHostState.showSnackbar("📝 Agrega un comentario para compartir")
                            }
                        }
                    }
                },
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
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
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
                            text = "✅ Video listo para compartir",
                            color = Color(0xFF1565C0),
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "Toca el botón flotante para compartir ↗",
                            fontSize = 12.sp,
                            color = Color.Gray
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
                        Text(
                            "Selecciona o graba un video",
                            color = Color.Gray,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
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
                label = { Text("Título del video (opcional)") },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Ej: Progreso en el drive...") }
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = comentario,
                onValueChange = { comentario = it },
                label = { Text("Comentario o descripción breve *") },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Ej: Gran progreso en la técnica hoy...") }
            )

            Spacer(Modifier.height(12.dp))

            Text("Plantillas de mensaje:", style = MaterialTheme.typography.labelMedium)
            DropdownMenuDemo(
                mensajesPredeterminados,
                onSeleccion = { mensajeSeleccionado = it; comentario = it }
            )

            Spacer(Modifier.height(20.dp))

            if (titulo.isNotBlank() || comentario.isNotBlank()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
                ) {
                    Column(Modifier.padding(12.dp)) {
                        if (titulo.isNotBlank()) {
                            Text(
                                titulo,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1565C0)
                            )
                        }
                        Text(comentario, color = Color.DarkGray)
                        Spacer(Modifier.height(6.dp))
                        Text(
                            firmaProfesor,
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Switch(
                    checked = modoDemo,
                    onCheckedChange = {
                        modoDemo = it
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                if (it) "🔶 Modo demostración activado"
                                else "🔹 Modo normal"
                            )
                        }
                    }
                )
                Spacer(Modifier.width(8.dp))
                Text("Modo demostración", color = Color.Gray)
            }

            Spacer(Modifier.height(20.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD))
            ) {
                Column(Modifier.padding(12.dp)) {
                    Text("📋 Estado para compartir:", fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(4.dp))
                    Text("• Video: ${if (videoUri != null) "✅ Listo" else "❌ Faltante"}")
                    Text("• Comentario: ${if (comentario.isNotBlank()) "✅ Listo" else "❌ Faltante"}")
                    Text("• Modo: ${if (modoDemo) "🔶 Demostración" else "🔹 Publicación real"}")
                }
            }

            Spacer(Modifier.height(24.dp))
            SeccionProBloqueada()
            Spacer(Modifier.height(80.dp))
        }
    }

    if (mostrarDialogo) {
        if (modoDemo) {
            scope.launch {
                snackbarHostState.showSnackbar(
                    "🎮 MODO DEMO: Simulando envío a redes sociales...\n" +
                            "Video: ${videoUri?.toString()?.take(20)}...\n" +
                            "Mensaje: ${comentario.take(30)}..."
                )
            }
            mostrarDialogo = false
        } else {
            DialogCompartir(
                context = context,
                onCerrar = { mostrarDialogo = false },
                scope = scope,
                snackbarHostState = snackbarHostState,
                videoUri = videoUri,
                titulo = titulo,
                comentario = comentario,
                firmaProfesor = firmaProfesor
            )
        }
    }
}

@Composable
fun SeccionProBloqueada() {
    var mostrarDialogoUpgrade by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .blur(3.dp)
        ) {
            Text(
                "📁 Galería de Progreso",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color(0xFF1565C0)
            )
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                repeat(3) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(80.dp)
                            .background(Color(0xFFBBDEFB), RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.PlayCircle,
                            contentDescription = null,
                            tint = Color(0xFF1565C0),
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
            Text(
                "📊 Clase 1 vs Clase Final",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color(0xFF1565C0)
            )
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(100.dp)
                        .background(Color(0xFFE8F5E9), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Clase 1", color = Color(0xFF2E7D32), fontWeight = FontWeight.Medium)
                }
                Icon(
                    Icons.Default.CompareArrows,
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(100.dp)
                        .background(Color(0xFFFFF9C4), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Clase Final", color = Color(0xFFF57F17), fontWeight = FontWeight.Medium)
                }
            }
        }

        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.3f),
                            Color.White.copy(alpha = 0.85f)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .border(1.dp, Color(0xFFFFD700), RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFDE7)),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("🔒", fontSize = 36.sp)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Funciones PRO",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color(0xFF5D4037)
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "📁 Galería completa por alumno\n" +
                                "📊 Comparación Clase 1 vs Final\n" +
                                "☁️ Backup en la nube\n" +
                                "🤖 Análisis de progreso con IA",
                        fontSize = 13.sp,
                        color = Color(0xFF5D4037),
                        textAlign = TextAlign.Center,
                        lineHeight = 22.sp
                    )
                    Spacer(Modifier.height(16.dp))
                    Button(
                        onClick = { mostrarDialogoUpgrade = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFFB300)
                        ),
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            "Quiero CoachTrack PRO",
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }

    if (mostrarDialogoUpgrade) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoUpgrade = false },
            icon = { Text("⭐", fontSize = 32.sp) },
            title = {
                Text(
                    "CoachTrack PRO",
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Column {
                    Text(
                        "Lleva tu coaching al siguiente nivel con herramientas profesionales:",
                        color = Color.DarkGray,
                        fontSize = 14.sp
                    )
                    Spacer(Modifier.height(12.dp))
                    listOf(
                        "📁 Galería ilimitada de videos por alumno",
                        "📊 Comparación visual Clase 1 vs Clase Final",
                        "☁️ Backup automático en la nube",
                        "🤖 Análisis de progreso con Inteligencia Artificial",
                        "📤 Compartir directo a Instagram y Telegram",
                        "👥 Gestión ilimitada de alumnos"
                    ).forEach { feature ->
                        Text(
                            feature,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(vertical = 3.dp)
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                    Text(
                        "🚀 Próximamente disponible",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1565C0),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { mostrarDialogoUpgrade = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFB300))
                ) {
                    Text("¡Me interesa!", fontWeight = FontWeight.Bold, color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoUpgrade = false }) {
                    Text("Más tarde")
                }
            }
        )
    }
}

@Composable
fun DropdownMenuDemo(mensajes: List<String>, onSeleccion: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Box(Modifier.fillMaxWidth()) {
        OutlinedButton(
            onClick = { expanded = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Message, contentDescription = null)
            Spacer(Modifier.width(6.dp))
            Text("Seleccionar plantilla")
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            mensajes.forEach { msg ->
                DropdownMenuItem(
                    text = { Text(msg, fontSize = 13.sp) },
                    onClick = {
                        onSeleccion(msg)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun ShareOption(nombre: String, icon: ImageVector, onClick: () -> Unit) {
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

@Composable
fun DialogCompartir(
    context: Context,
    onCerrar: () -> Unit,
    scope: CoroutineScope,
    snackbarHostState: SnackbarHostState,
    videoUri: Uri?,
    titulo: String,
    comentario: String,
    firmaProfesor: String
) {
    val mensajeBase = if (titulo.isNotBlank()) "$titulo\n\n$comentario\n\n$firmaProfesor"
    else "$comentario\n\n$firmaProfesor"

    AlertDialog(
        onDismissRequest = onCerrar,
        confirmButton = {
            TextButton(onClick = onCerrar) { Text("Cerrar") }
        },
        title = { Text("Compartir Video en...") },
        text = {
            Column {
                ShareOption("Instagram", Icons.Default.Photo) {
                    compartirVideoOTexto(context, videoUri, mensajeBase, "instagram")
                    scope.launch { snackbarHostState.showSnackbar("📸 Compartiendo en Instagram...") }
                    onCerrar()
                }
                ShareOption("WhatsApp", Icons.Default.Send) {
                    compartirVideoOTexto(context, videoUri, mensajeBase, "whatsapp")
                    scope.launch { snackbarHostState.showSnackbar("💬 Compartiendo en WhatsApp...") }
                    onCerrar()
                }
                ShareOption("Telegram", Icons.Default.Message) {
                    compartirVideoOTexto(context, videoUri, mensajeBase, "telegram")
                    scope.launch { snackbarHostState.showSnackbar("📢 Compartiendo en Telegram...") }
                    onCerrar()
                }
                ShareOption("Otra App", Icons.Default.Share) {
                    compartirVideoOTexto(context, videoUri, mensajeBase, "general")
                    scope.launch { snackbarHostState.showSnackbar("📤 Eligiendo app para compartir...") }
                    onCerrar()
                }
            }
        }
    )
}

fun compartirVideoOTexto(context: Context, videoUri: Uri?, mensaje: String, plataforma: String = "general") {
    try {
        if (videoUri != null) {
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "video/*"
                putExtra(Intent.EXTRA_STREAM, videoUri)
                putExtra(Intent.EXTRA_TEXT, mensaje)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                when (plataforma) {
                    "whatsapp"  -> setPackage("com.whatsapp")
                    "instagram" -> setPackage("com.instagram.android")
                    "telegram"  -> setPackage("org.telegram.messenger")
                }
            }
            context.startActivity(intent)
        } else {
            compartirTexto(context, mensaje)
        }
    } catch (e: Exception) {
        compartirTexto(context, "$mensaje\n\n❌ No se pudo adjuntar el video")
    }
}

fun compartirTexto(context: Context, mensaje: String) {
    val sendIntent = Intent(Intent.ACTION_SEND).apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, mensaje)
        type = "text/plain"
    }
    context.startActivity(Intent.createChooser(sendIntent, "Compartir vía"))
}

fun abrirInstagram(context: Context, usuario: String? = null) {
    val uri = if (usuario != null) Uri.parse("http://instagram.com/_u/$usuario")
    else Uri.parse("http://instagram.com/")
    val intent = Intent(Intent.ACTION_VIEW, uri).apply { setPackage("com.instagram.android") }
    try {
        context.startActivity(intent)
    } catch (e: Exception) {
        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("http://instagram.com")))
    }
}