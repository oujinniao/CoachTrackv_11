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
import com.example.coachtrack.compartirEnlace
import com.example.coachtrack.generarEnlacePagoRealista
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PagosScreen(
    viewModel: ProfesorViewModel = viewModel(),
    onVolver: () -> Unit
) {
    //-------------BANCKHANDLER PARA ESTA PANTALLA-------------------
    BackHandler {
        onVolver()
    }


    val alumnos by viewModel.alumnos.collectAsState()
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()

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
                Modifier.fillMaxSize().padding(padding),
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
                items(alumnos, key = { it.id }) { alumno ->

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { }
                    ) {
                        Row(
                            Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(Modifier.weight(1f)) {
                                Text(alumno.nombre, style = MaterialTheme.typography.titleMedium)
                                Text("ID: ${alumno.id}", style = MaterialTheme.typography.bodySmall)
                            }

                            Button(
                                onClick = {
                                    scope.launch {
                                        val link = generarEnlacePagoRealista(alumno)
                                        compartirEnlace(ctx, link, alumno.nombre)
                                    }
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

/** ------------------------------------------------------------------
 *   ENLACE REALISTA (simula Flow, Stripe o PayPal)
 *   Esto es 100% funcional como demostración.
 *   Luego puedo ayudarte a cambiarlo por Flow.cl real.
------------------------------------------------------------------ **/
fun generarEnlacePagoRealista(alumno: AlumnoEntity): String {

    val montoClases = alumno.clasesPactadas * 8000  // ejemplo 8.000 CLP por clase

    return "https://pago.coachtrack.app/pay?" +
            "alumno=${alumno.nombre.replace(" ", "%20")}" +
            "&id=${alumno.id}" +
            "&monto=${montoClases}"
}

/** ------------------------------------------------------------------
 *   COMPARTIR EL ENLACE POR WHATSAPP / EMAIL / SMS / INSTAGRAM
------------------------------------------------------------------ **/
fun compartirEnlace(context: android.content.Context, url: String, nombre: String) {
    val intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(
            Intent.EXTRA_TEXT,
            "Hola $nombre 👋\nAquí está tu enlace para pagar tus clases:\n$url"
        )
        type = "text/plain"
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }

    context.startActivity(Intent.createChooser(intent, "Enviar enlace de pago"))
}
