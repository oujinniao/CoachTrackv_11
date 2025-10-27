import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.coachtrack.SESIONES_GUARDADAS

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistorialScreen(onVolverClick: () -> Unit) {
    // Al ser mutableStateListOf, con leer directamente ya recompone cuando agregas
    val sesiones = SESIONES_GUARDADAS

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Historial de Sesiones") },
                navigationIcon = {
                    IconButton(onClick = onVolverClick) {
                        Text("Atrás")
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
                Text("Sin sesiones aún")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(pv)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(sesiones, key = { it.sessionId }) { sesion ->
                    Card {
                        Column(Modifier.padding(12.dp)) {
                            Text(
                                text = "${sesion.alumnoNombre} · ${sesion.fechaCreacion}",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "Duración total: ${sesion.duracionTotalMinutos} min",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                            Spacer(Modifier.height(8.dp))
                            Text("Ejercicios:", style = MaterialTheme.typography.labelLarge)

                            // Lista TODAS las plantillas guardadas en esa sesión (¡lo que buscabas!)
                            sesion.ejercicios.forEach { pl ->
                                Text(
                                    "• ${pl.nombre} (${pl.duracionMinutos} min) – ${pl.enfoque}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
