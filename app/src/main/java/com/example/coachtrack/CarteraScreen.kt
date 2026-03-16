package com.example.coachtrack

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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import timber.log.Timber

private const val MAX_ALUMNOS_FREE = 20

private enum class SnackbarTipo { EXITO, ERROR }

private data class SnackbarEvento(
    val mensaje: String,
    val tipo: SnackbarTipo
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarteraScreen(
    onVolver: () -> Unit,
    onAbrirFichaAlumno: (AlumnoEntity) -> Unit
) {
    val auth = Firebase.auth
    var authState by remember { mutableStateOf(auth.currentUser) }

    val authListener = remember {
        FirebaseAuth.AuthStateListener { firebaseAuth ->
            authState = firebaseAuth.currentUser
            if (authState == null) {
                Timber.d("Usuario desconectado, AuthState actualizado.")
            }
        }
    }

    DisposableEffect(auth) {
        auth.addAuthStateListener(authListener)
        onDispose { auth.removeAuthStateListener(authListener) }
    }

    when {
        authState == null -> {
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
            val carteraViewModel: CarteraViewModel = viewModel()

            val alumnos by carteraViewModel.alumnos.collectAsState()
            val isLoading by carteraViewModel.isLoading.collectAsState()
            val errorMessage by carteraViewModel.error.collectAsState()

            val isProUser = carteraViewModel.isProUser
            val limiteAlcanzado = !isProUser && alumnos.size >= MAX_ALUMNOS_FREE

            val snackbarHostState = remember { SnackbarHostState() }
            val scope = rememberCoroutineScope()

            var snackbarEvento by remember { mutableStateOf<SnackbarEvento?>(null) }

            val emailUsuario = authState?.email ?: authState?.uid?.take(12) ?: ""

            LaunchedEffect(Unit) {
                carteraViewModel.iniciarSincronizacionInicial()
            }

            LaunchedEffect(errorMessage) {
                if (errorMessage != null) {
                    snackbarEvento = SnackbarEvento(
                        mensaje = errorMessage!!,
                        tipo = SnackbarTipo.ERROR
                    )
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
                                Badge(
                                    containerColor = if (isProUser)
                                        MaterialTheme.colorScheme.primaryContainer
                                    else
                                        MaterialTheme.colorScheme.tertiaryContainer,
                                    contentColor = if (isProUser)
                                        MaterialTheme.colorScheme.onPrimaryContainer
                                    else
                                        MaterialTheme.colorScheme.onTertiaryContainer
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
                                text = "👤 $emailUsuario",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(end = 8.dp)
                            )
                        }
                    )
                },
                snackbarHost = {
                    SnackbarHost(hostState = snackbarHostState) { data ->
                        val tipo = snackbarEvento?.tipo ?: SnackbarTipo.EXITO
                        Snackbar(
                            snackbarData = data,
                            containerColor = when (tipo) {
                                SnackbarTipo.EXITO -> MaterialTheme.colorScheme.secondaryContainer
                                SnackbarTipo.ERROR -> MaterialTheme.colorScheme.errorContainer
                            },
                            contentColor = when (tipo) {
                                SnackbarTipo.EXITO -> MaterialTheme.colorScheme.onSecondaryContainer
                                SnackbarTipo.ERROR -> MaterialTheme.colorScheme.onErrorContainer
                            }
                        )
                    }
                },
                floatingActionButton = {
                    FloatingActionButton(
                        onClick = { carteraViewModel.abrirDialogoAgregar() },
                        containerColor = if (limiteAlcanzado)
                            MaterialTheme.colorScheme.errorContainer
                        else
                            MaterialTheme.colorScheme.primary
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
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isProUser)
                                MaterialTheme.colorScheme.secondaryContainer
                            else
                                MaterialTheme.colorScheme.tertiaryContainer
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                if (isProUser) Icons.Default.Verified else Icons.Default.StarBorder,
                                contentDescription = "Estado",
                                tint = if (isProUser)
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.tertiary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    if (isProUser) "Subscripción PRO Activa" else "Versión Básica (FREE)",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isProUser)
                                        MaterialTheme.colorScheme.onSecondaryContainer
                                    else
                                        MaterialTheme.colorScheme.onTertiaryContainer
                                )
                                Text(
                                    emailUsuario,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    if (limiteAlcanzado) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer
                            )
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
                                    "Límite de $MAX_ALUMNOS_FREE alumnos alcanzado (FREE). Actualiza a PRO para agregar más.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                            }
                        }
                    }

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
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        Text(
                            "Alumnos (${alumnos.size} de ${if (isProUser) "∞" else MAX_ALUMNOS_FREE}):",
                            modifier = Modifier.padding(vertical = 8.dp),
                            style = MaterialTheme.typography.titleMedium
                        )
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(bottom = 80.dp)
                        ) {
                            items(alumnos) { alumno ->
                                AlumnoListItem(
                                    alumno = alumno,
                                    onEdit = { carteraViewModel.abrirDialogoAgregar(alumno) },
                                    onClick = { onAbrirFichaAlumno(alumno) }
                                )
                                HorizontalDivider()
                            }
                        }
                    }
                }

                if (carteraViewModel.mostrarDialogoAgregar) {
                    DialogAgregarAlumno(
                        alumnoExistente = carteraViewModel.alumnoEnEdicion,
                        onDismiss = { carteraViewModel.cerrarDialogoAgregar() },
                        onGuardar = { alumno ->
                            carteraViewModel.agregarOActualizarAlumno(alumno) { exito, fueActualizacion ->
                                if (exito) {
                                    carteraViewModel.cerrarDialogoAgregar()
                                }
                                scope.launch {
                                    val evento = when {
                                        !exito && carteraViewModel.error.value != null ->
                                            SnackbarEvento(carteraViewModel.error.value!!, SnackbarTipo.ERROR)
                                        !exito ->
                                            SnackbarEvento("No se pudo guardar (duplicado o error).", SnackbarTipo.ERROR)
                                        fueActualizacion ->
                                            SnackbarEvento("Alumno actualizado correctamente.", SnackbarTipo.EXITO)
                                        else ->
                                            SnackbarEvento("Alumno agregado correctamente.", SnackbarTipo.EXITO)
                                    }
                                    snackbarEvento = evento
                                    snackbarHostState.showSnackbar(evento.mensaje)
                                    carteraViewModel.limpiarError()
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}

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
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Person,
                contentDescription = "Alumno",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(36.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    alumno.nombre,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "Clases Cursadas: ${alumno.clasesCursadas}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = onEdit) {
                Icon(Icons.Default.Edit, contentDescription = "Editar Alumno")
            }
        }
    }
}