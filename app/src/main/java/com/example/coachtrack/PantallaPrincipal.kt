package com.example.coachtrack

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun PantallaPrincipal(
    onPlanificarClick: () -> Unit,
    onGestionClick: () -> Unit,
    onVideoAnalisisClick: () -> Unit,
    userId: String,
    onCerrarSesion: (() -> Unit)? = null
) {
    var mostrarDialogoCerrarSesion by remember { mutableStateOf(false) }

    // 🔹 ViewModel del perfil del profesor
    val perfilViewModel: PerfilProfesorViewModel = viewModel()
    val perfil by perfilViewModel.perfil.collectAsState()

    // 🔹 Control del diálogo para editar el perfil
    var mostrarDialogoPerfil by remember { mutableStateOf(false) }

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

            //------------------------------------
            // CARD DATOS ENTRENADOR (DINÁMICA)
            //------------------------------------
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

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        // Nombre del profesor
                        Text(
                            text = if (perfil.nombreProfesor.isNotBlank())
                                "Prof. ${perfil.nombreProfesor}"
                            else
                                "Configura tu nombre",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )

                        // Academia / club
                        Text(
                            text = if (perfil.academia.isNotBlank())
                                perfil.academia
                            else
                                "Añade tu academia / club",
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // ID de usuario (prefiere el guardado, si no, el que viene por parámetro)
                        val uidToShow = perfil.userId.ifBlank { userId }
                        if (uidToShow.isNotBlank()) {
                            Text(
                                "ID Usuario: $uidToShow",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }

                    // Botón para editar el perfil
                    IconButton(onClick = { mostrarDialogoPerfil = true }) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Editar perfil"
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            //------------------------------------
            // BOTÓN PLANIFICAR SESIÓN
            //------------------------------------
            BotonFuncionalidad(
                texto = "PLANIFICAR SESIÓN",
                descripcion = "Crea tu clase en 2 clics",
                onClick = onPlanificarClick
            )

            Spacer(modifier = Modifier.height(16.dp))

            //------------------------------------
            // BOTÓN GESTIÓN
            //------------------------------------
            BotonFuncionalidad(
                texto = "GESTIÓN",
                descripcion = "Alumnos, historial, profesores, pagos",
                onClick = onGestionClick
            )

            Spacer(modifier = Modifier.height(16.dp))

            //------------------------------------
            // BOTÓN MARKETING
            //------------------------------------
            BotonFuncionalidad(
                texto = "MARKETING / PORTAFOLIO",
                descripcion = "Graba y envía feedback visual",
                onClick = onVideoAnalisisClick
            )

            Spacer(modifier = Modifier.height(40.dp))

            //------------------------------------
            // BOTÓN CERRAR SESIÓN
            //------------------------------------
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

            //------------------------------------
            // DIÁLOGO CONFIRMACIÓN CERRAR SESIÓN
            //------------------------------------
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
                            Text("Sí, salir", color = Color.Red)
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

    //------------------------------------
    // DIÁLOGO EDITAR PERFIL PROFESOR
    //------------------------------------
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
fun BotonFuncionalidad(texto: String, descripcion: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        )
    ) {
        Column(horizontalAlignment = Alignment.Start, modifier = Modifier.fillMaxWidth()) {
            Text(texto, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(descripcion, style = MaterialTheme.typography.bodySmall)
        }
    }
}
