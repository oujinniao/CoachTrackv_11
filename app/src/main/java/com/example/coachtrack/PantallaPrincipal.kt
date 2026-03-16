package com.example.coachtrack

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun PantallaPrincipal(
    onPlanificarClick: () -> Unit,
    onGestionClick: () -> Unit,
    onVideoAnalisisClick: () -> Unit,
    onDashboardClick: () -> Unit,
    userId: String,
    onCerrarSesion: (() -> Unit)? = null
) {
    var mostrarDialogoCerrarSesion by remember { mutableStateOf(false) }

    val perfilViewModel: PerfilProfesorViewModel = viewModel()
    val perfil by perfilViewModel.perfil.collectAsState()

    var mostrarDialogoPerfil by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { TopAppBarPrincipal() }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (perfil.nombreProfesor.isNotBlank())
                                "Prof. ${perfil.nombreProfesor}"
                            else
                                "Configura tu nombre",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (perfil.academia.isNotBlank())
                                perfil.academia
                            else
                                "Añade tu academia / club",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    IconButton(onClick = { mostrarDialogoPerfil = true }) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Editar perfil"
                        )
                    }
                }
            }

            BotonFuncionalidad(
                icon = Icons.Default.EventNote,
                texto = "PLANIFICAR SESIÓN",
                descripcion = "Crea tu clase en 2 clics",
                onClick = onPlanificarClick
            )

            BotonFuncionalidad(
                icon = Icons.Default.ManageAccounts,
                texto = "GESTIÓN",
                descripcion = "Alumnos, historial, profesores, pagos",
                onClick = onGestionClick
            )

            BotonFuncionalidad(
                icon = Icons.Default.Dashboard,
                texto = "DASHBOARD",
                descripcion = "Resumen de alumnos, pagos y clases",
                onClick = onDashboardClick
            )

            BotonFuncionalidad(
                icon = Icons.Default.Campaign,
                texto = "MARKETING / PORTAFOLIO",
                descripcion = "Graba y envía feedback visual",
                onClick = onVideoAnalisisClick
            )

            OutlinedButton(
                onClick = { mostrarDialogoCerrarSesion = true },
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
            ) {
                Icon(Icons.Default.ExitToApp, contentDescription = "Cerrar sesión")
                Spacer(Modifier.width(8.dp))
                Text("Cerrar sesión", fontWeight = FontWeight.Bold)
            }

            if (mostrarDialogoCerrarSesion) {
                AlertDialog(
                    onDismissRequest = { mostrarDialogoCerrarSesion = false },
                    title = { Text("Cerrar sesión") },
                    text = { Text("¿Deseas cerrar sesión y volver al inicio?") },
                    confirmButton = {
                        TextButton(onClick = {
                            mostrarDialogoCerrarSesion = false
                            onCerrarSesion?.invoke()
                        }) {
                            Text(
                                "Sí, salir",
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { mostrarDialogoCerrarSesion = false }) {
                            Text("Cancelar")
                        }
                    }
                )
            }
        }
    }

    if (mostrarDialogoPerfil) {
        DialogEditarPerfilProfesor(
            perfilActual = perfil,
            onDismiss = { mostrarDialogoPerfil = false },
            onGuardar = { nuevoNombre, nuevaAcademia ->
                perfilViewModel.guardarPerfil(nuevoNombre, nuevaAcademia)
                mostrarDialogoPerfil = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBarPrincipal() {
    TopAppBar(title = { Text("CoachTrack 🎾") })
}

@Composable
fun BotonFuncionalidad(
    icon: ImageVector,
    texto: String,
    descripcion: String,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(78.dp),
        shape = RoundedCornerShape(26.dp),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 14.dp),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = texto,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(14.dp))
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(texto, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(descripcion, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}