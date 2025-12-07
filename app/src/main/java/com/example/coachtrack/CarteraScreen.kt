package com.example.coachtrack

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarteraScreen(
    onVolver: () -> Unit,
    onAbrirFichaAlumno: (AlumnoEntity) -> Unit
) {
    val TAG = "CarteraScreen"

    BackHandler(onBack = onVolver)

    val auth = Firebase.auth

    LaunchedEffect(Unit) {
        Log.d(TAG, "═══════════════════════════════════════")
        Log.d(TAG, "🎬 CarteraScreen COMPOSADA")
        Log.d(TAG, "Auth instance: ${auth.hashCode()}")
        Log.d(TAG, "CurrentUser es null? ${auth.currentUser == null}")
        Log.d(TAG, "CurrentUser ID: ${auth.currentUser?.uid ?: "NULL"}")
        Log.d(TAG, "═══════════════════════════════════════")
    }

    var authState by remember { mutableStateOf(auth.currentUser) }

    DisposableEffect(Unit) {
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            authState = user
            Log.d(TAG, "🔄 AuthStateListener disparado:")
            Log.d(TAG, "   User: ${user?.uid ?: "NULL"}")
            Log.d(TAG, "   Cambio: ${auth.currentUser?.uid ?: "NULL"} → ${user?.uid ?: "NULL"}")
        }

        auth.addAuthStateListener(listener)

        onDispose {
            auth.removeAuthStateListener(listener)
        }
    }

    when {
        authState == null -> {
            // ------- SIN USUARIO AUTENTICADO -------
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(24.dp)
                ) {
                    Icon(
                        Icons.Default.Error,
                        contentDescription = "Error",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(80.dp)
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        "🔍 DIAGNÓSTICO: auth.currentUser es NULL",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "MainActivity debería haber autenticado.\n" +
                                "Revisa Logcat con filtro: 'CoachTrackAuth'",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    Button(
                        onClick = {
                            Log.d(TAG, "Usuario presionó: Intentar autenticación MANUAL")
                            auth.signInAnonymously()
                                .addOnCompleteListener { task ->
                                    Log.d(TAG, "Resultado manual: ${if (task.isSuccessful) "ÉXITO" else "FALLA"}")
                                }
                        },
                        modifier = Modifier.fillMaxWidth(0.8f)
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = "Reintentar")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Autenticar MANUALMENTE")
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedButton(
                        onClick = onVolver,
                        modifier = Modifier.fillMaxWidth(0.8f)
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Volver al menú")
                    }
                }
            }
        }

        else -> {
            // ------- USUARIO AUTENTICADO -------
            Log.d(TAG, "✅ MOSTRANDO PANTALLA NORMAL para usuario: ${authState!!.uid}")

            val carteraViewModel: CarteraViewModel = viewModel()
            val alumnos by carteraViewModel.alumnos.collectAsState()
            val snackbarHostState = remember { SnackbarHostState() }
            val scope = rememberCoroutineScope()

            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text("Cartera de Alumnos")
                                Badge(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                                ) {
                                    Text("✓")
                                }
                            }
                        },
                        navigationIcon = {
                            IconButton(onClick = onVolver) {
                                Icon(
                                    Icons.Filled.ArrowBack,
                                    contentDescription = "Volver al menú"
                                )
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
                        containerColor = MaterialTheme.colorScheme.primary
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Agregar Alumno")
                    }
                }
            ) { paddingValues ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(horizontal = 16.dp)
                ) {
                    // tarjeta de “autenticado”
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = "Autenticado",
                                tint = Color.Green,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    "✅ Autenticado correctamente",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Green
                                )
                                Text(
                                    "ID: ${authState!!.uid.take(12)}...",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.Gray
                                )
                            }
                        }
                    }

                    // Aquí iría tu lista de alumnos, filtros, etc.
                    Text(
                        "Pantalla funcionando - Botón (+) disponible",
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
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
                                        !exito -> "No se pudo guardar (duplicado o error)."
                                        fueActualizacion -> "Alumno actualizado correctamente."
                                        else -> "Alumno agregado correctamente."
                                    }
                                    snackbarHostState.showSnackbar(mensaje)
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
}
