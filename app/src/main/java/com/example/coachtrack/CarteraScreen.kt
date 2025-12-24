package com.example.coachtrack

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.coachtrack.AlumnoListItem
import com.example.coachtrack.MAX_ALUMNOS_FREE
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

// Definimos el límite de alumnos para la versión FREE aquí para la UI
private const val MAX_ALUMNOS_FREE = 20

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarteraScreen(
    onVolver: () -> Unit,
    onAbrirFichaAlumno: (AlumnoEntity) -> Unit
) {
    val TAG = "CarteraScreen"

    //BackHandler(onBack = onVolver)
    val auth = Firebase.auth
    var authState by remember { mutableStateOf(auth.currentUser) }

    // 💡 Gestión de AuthStateListener con DisposableEffect (Corregido)
    val authListener = remember {
        FirebaseAuth.AuthStateListener { firebaseAuth ->
            authState = firebaseAuth.currentUser
            if (authState == null) {
                Log.d(TAG, "Usuario desconectado, AuthState actualizado.")
            }
        }
    }

    DisposableEffect(auth) {
        auth.addAuthStateListener(authListener)
        onDispose {
            auth.removeAuthStateListener(authListener)
        }
    }

    when {
        authState == null -> {
            // ------- SIN USUARIO AUTENTICADO (Diagnóstico) -------
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Error de autenticación.", color = MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = onVolver) {
                        Text("Volver al Menú Principal")
                    }
                }
            }
        }

        else -> {
            // ------- USUARIO AUTENTICADO (Lógica Híbrida/Freemium) -------
            val carteraViewModel: CarteraViewModel = viewModel()

            // 1. Recolección de Estados Críticos
            val alumnos by carteraViewModel.alumnos.collectAsState()
            val isLoading by carteraViewModel.isLoading.collectAsState()
            val errorMessage by carteraViewModel.error.collectAsState()

            val isProUser = carteraViewModel.isProUser // Control Freemium
            val limiteAlcanzado = !isProUser && alumnos.size >= MAX_ALUMNOS_FREE

            val snackbarHostState = remember { SnackbarHostState() }
            val scope = rememberCoroutineScope()

            // 2. Disparar Sincronización Inicial (Solo una vez después del login)
            LaunchedEffect(Unit) {
                carteraViewModel.iniciarSincronizacionInicial()
            }

            // 3. Mostrar errores del ViewModel como Snackbar
            LaunchedEffect(errorMessage) {
                if (errorMessage != null) {
                    snackbarHostState.showSnackbar(
                        message = errorMessage!!,
                        actionLabel = "OK",
                        duration = SnackbarDuration.Long
                    )
                    carteraViewModel.limpiarError()
                }
            }


            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text("Cartera de Alumnos")
                                // El badge ahora muestra el estado PRO o FREE
                                Badge(
                                    containerColor = if (isProUser) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.tertiaryContainer,
                                    contentColor = if (isProUser) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onTertiaryContainer
                                ) {
                                    Text(if (isProUser) "PRO" else "FREE")
                                }
                            }
                        },
                        navigationIcon = {
                            IconButton(onClick = onVolver) {
                                Icon(Icons.Filled.ArrowBack, contentDescription = "Volver al menú")
                            }
                        },
                        actions = {
                            Text(
                                text = "👤 ${authState!!.uid.take(6)}...",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.Gray,
                                modifier = Modifier.padding(end = 8.dp)
                            )
                        }
                    )
                },
                snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
                floatingActionButton = {
                    FloatingActionButton(
                        onClick = { carteraViewModel.abrirDialogoAgregar() },
                        containerColor = if (limiteAlcanzado) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.primary,

                    ) {
                        Icon(
                            if (limiteAlcanzado) Icons.Default.Block else Icons.Default.Add,
                            contentDescription = "Agregar Alumno"
                        )
                    }
                }
            ) { paddingValues ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(horizontal = 16.dp)
                ) {

                    // Tarjeta de Estado (Autenticación y Subscripción)
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        colors = CardDefaults.cardColors(containerColor = if (isProUser) Color(0xFFE8F5E9) else Color(0xFFFFFDE7))
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                if (isProUser) Icons.Default.Verified else Icons.Default.StarBorder,
                                contentDescription = "Estado",
                                tint = if (isProUser) Color.Green else Color(0xFFFFC107),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    if (isProUser) "✅ Subscripción PRO Activa" else "★ Versión Básica (FREE)",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isProUser) Color.Green else Color(0xFFFFC107)
                                )
                                Text(
                                    "ID: ${authState!!.uid.take(12)}...",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.Gray
                                )
                            }
                        }
                    }

                    // 4. Bloque de Límite Alcanzado (Solo se muestra si es FREE y está full)
                    if (limiteAlcanzado) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Warning,
                                    contentDescription = "Límite",
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    "Límite de ${MAX_ALUMNOS_FREE} alumnos alcanzado (FREE). Actualiza a PRO para agregar más.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                            }
                        }
                    }

                    // 5. Mostrar la Lista de Alumnos o el estado de carga
                    if (isLoading) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    } else if (alumnos.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "No hay alumnos aún. Usa el botón (+) para empezar.",
                                textAlign = TextAlign.Center,
                                color = Color.Gray
                            )
                        }
                    } else {
                        // 6. Lista Real de Alumnos (Leyendo de Room)
                        Text(
                            "Alumnos (${alumnos.size} de ${if (isProUser) "∞" else MAX_ALUMNOS_FREE}):",
                            modifier = Modifier.padding(vertical = 8.dp),
                            style = MaterialTheme.typography.titleMedium
                        )

                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(bottom = 80.dp) // Espacio para el FAB
                        ) {
                            items(alumnos) { alumno ->
                                AlumnoListItem(
                                    alumno = alumno,
                                    onEdit = {
                                        carteraViewModel.abrirDialogoAgregar(alumno)
                                    },
                                    onClick = { onAbrirFichaAlumno(alumno) }
                                )
                                Divider()
                            }
                        }
                    }
                }

                // 🔹 MOSTRAR DIÁLOGO CUANDO EL VM LO PIDA
                if (carteraViewModel.mostrarDialogoAgregar) {
                    DialogAgregarAlumno(
                        alumnoExistente = carteraViewModel.alumnoEnEdicion,
                        onDismiss = { carteraViewModel.cerrarDialogoAgregar() },
                        onGuardar = { alumno ->
                            carteraViewModel.agregarOActualizarAlumno(alumno) { exito, fueActualizacion ->
                                scope.launch {
                                    val mensaje = when {
                                        !exito && carteraViewModel.error.value != null -> carteraViewModel.error.value!!
                                        !exito -> "No se pudo guardar (duplicado o error)."
                                        fueActualizacion -> "Alumno actualizado correctamente."
                                        else -> "Alumno agregado correctamente."
                                    }
                                    snackbarHostState.showSnackbar(mensaje)
                                    carteraViewModel.limpiarError() // Limpiar error después de mostrar
                                }
                                if (exito) {
                                    carteraViewModel.cerrarDialogoAgregar()
                                }
                            }
                        }
                    )
                }
            }
        }
    }
} // 💡 CIERRE DE CARTERASCREEN

// ----------------------------------------------------
// 💡 NUEVO COMPOSABLE DE NIVEL SUPERIOR (CORRECTO)
// ----------------------------------------------------

@Composable
fun AlumnoListItem(
    alumno: AlumnoEntity,
    onEdit: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Ícono de Alumno
            Icon(
                Icons.Default.Person,
                contentDescription = "Alumno",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(36.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))

            // Información principal
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    alumno.nombre,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                // Usando un campo que existe en tu AlumnoEntity
                Text(
                    // Asumiendo que 'clasesCursadas' es un campo real o un campo simple para mostrar.
                    // Si no existe, reemplázalo por otro campo simple como 'nivelActual' o 'deporte'.
                    "Clases Cursadas: ${alumno.clasesCursadas}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            // Botón de Edición (Opcional)
            IconButton(onClick = onEdit) {
                Icon(Icons.Default.Edit, contentDescription = "Editar Alumno")
            }
        }
    }
}