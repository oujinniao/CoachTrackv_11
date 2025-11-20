package com.example.coachtrack

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import com.example.coachtrack.BotonFuncionalidad
import com.example.coachtrack.TopAppBarPrincipal

@Composable
fun PantallaPrincipal(
    onPlanificarClick: () -> Unit,
    onGestionClick: () -> Unit,
    onVideoAnalisisClick: () -> Unit,
    userId: String,
    onCerrarSesion: (() -> Unit)? = null
) {

    var mostrarDialogoCerrarSesion by remember { mutableStateOf(false) }

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
            // CARD DATOS ENTRENADOR
            //------------------------------------
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Prof. Alejandro González", style = MaterialTheme.typography.titleLarge)
                    Text("Academia Central de Tenis", style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "ID Usuario: $userId",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
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
            // BOTÓN GESTIÓN (NUEVO)
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
            // DIÁLOGO CONFIRMACIÓN
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
            contentColor = MaterialTheme.colorScheme.onPrimary)
    ) {
        Column(horizontalAlignment = Alignment.Start, modifier = Modifier.fillMaxWidth()) {
            Text(texto, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(descripcion, style = MaterialTheme.typography.bodySmall)
        }
    }
}
