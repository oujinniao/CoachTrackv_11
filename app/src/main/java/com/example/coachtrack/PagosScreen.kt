package com.example.coachtrack

import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PagosScreen(
    viewModel: ProfesorViewModel = viewModel(),
    onVolver: () -> Unit
) {
    BackHandler { onVolver() }

    val alumnos by viewModel.alumnos.collectAsState()
    val ctx = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pagos") },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->

        if (alumnos.isEmpty()) {
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("No hay alumnos disponibles aún.")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(alumnos, key = { it.localId }) { alumno ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { }
                    ) {
                        Row(
                            Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(Modifier.weight(1f)) {
                                Text(
                                    alumno.nombre,
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    "Clases: ${alumno.clasesCursadas} / ${alumno.clasesPactadas} • ${alumno.estadoPago}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Button(
                                onClick = {
                                    val link = generarEnlacePagoRealista(alumno)
                                    compartirEnlace(ctx, link, alumno.nombre)
                                }
                            ) {
                                Text("Enviar enlace")
                            }
                        }
                    }
                }
            }
        }
    }
}

// TODO: el monto por clase debería venir del perfil del profesor — pendiente para versión Pro
fun generarEnlacePagoRealista(alumno: AlumnoEntity): String {
    val montoClases = alumno.clasesPactadas * alumno.tarifaPorClase
    val idStable = alumno.firebaseId ?: alumno.localId.toString()
    return "https://pago.coachtrack.app/pay?" +
            "alumno=${alumno.nombre.replace(" ", "%20")}" +
            "&id=$idStable" +
            "&monto=$montoClases"
}

fun compartirEnlace(context: android.content.Context, url: String, nombre: String) {
    val intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(
            Intent.EXTRA_TEXT,
            "Hola $nombre, aquí está tu enlace para pagar tus clases:\n$url"
        )
        type = "text/plain"
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    context.startActivity(Intent.createChooser(intent, "Enviar enlace de pago"))
}