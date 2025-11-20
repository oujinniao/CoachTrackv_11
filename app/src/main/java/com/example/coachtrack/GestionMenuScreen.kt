package com.example.coachtrack



import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.coachtrack.BotonGestion



@OptIn(ExperimentalMaterial3Api::class)

@Composable

fun GestionMenuScreen(
    onVolverClick: () -> Unit,
    onGestionAlumnosClick: () -> Unit,
    onHistorialAlumnosClick: () -> Unit,
    onGestionProfesoresClick: () -> Unit,
    onPagosClick: () -> Unit

) {

    Scaffold(
        topBar = {
           TopAppBar(
                title = { Text("Gestión") },
                navigationIcon = {
                    IconButton(onClick = onVolverClick) {

                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")

                    }

                }

            )

        }

    ) { padding ->



        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally

        ) {



// Gestión de Alumnos

            BotonGestion(
                titulo = "Gestión de Alumnos",
                descripcion = "Agregar, editar y ver estados",
                onClick = onGestionAlumnosClick

            )



// Historial de Alumnos

            BotonGestion(
                titulo = "Historial de Alumnos",
                descripcion = "Revisar progreso y sesiones",
                onClick = onHistorialAlumnosClick

            )



// Gestión de Profesores (futuro)
            BotonGestion(
                titulo = "Gestión de Profesores",
                descripcion = "Administrar instructores (futuro)",
                onClick = onGestionProfesoresClick

            )



// Pagos (futuro)

            BotonGestion(
                titulo = "Pagos",
                descripcion = "Enviar enlaces de cobro (futuro)",
                onClick = onPagosClick

            )

        }

    }

}



@Composable

fun BotonGestion(titulo: String, descripcion: String, onClick: () -> Unit) {

    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
           .height(80.dp),
        colors= ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ),
        shape = RoundedCornerShape(40.dp)

    ) {

        Column(Modifier.fillMaxWidth()) {
            Text(titulo,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimary)
            Text(
                descripcion,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.95f)

            )

        }

    }

}