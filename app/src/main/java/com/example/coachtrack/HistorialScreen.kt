import android.app.Application
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.coachtrack.SesionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistorialScreen(onVolverClick: () -> Unit) {
    val context = LocalContext.current
    val sesionViewModel: SesionViewModel = viewModel(
        factory = androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
            .getInstance(context.applicationContext as Application)
    )

    val sesiones by sesionViewModel.sesiones.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Historial de Sesiones") },
                navigationIcon = {
                    IconButton(onClick = onVolverClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { pv ->
        if (sesiones.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(pv),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("No hay sesiones registradas")
                Text("Las sesiones aparecerán aquí cuando las crees",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(pv)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(sesiones, key = { it.id }) { sesion ->
                    Card {
                        Column(Modifier.padding(12.dp)) {
                            Text(
                                text = "Alumno ID: ${sesion.alumnoId} · ${sesion.fecha}",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "Duración: ${sesion.duracion} min",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                            Text(
                                text = "Ejercicios: ${sesion.ejercicios}",
                                style = MaterialTheme.typography.bodySmall
                            )
                            if (sesion.notas.isNotBlank()) {
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = "Notas: ${sesion.notas}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}