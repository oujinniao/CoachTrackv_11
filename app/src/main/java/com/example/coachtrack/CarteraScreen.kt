package com.example.coachtrack

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarteraScreen(
    onVolver: () -> Unit
) {
    val alumnos = remember {
        mutableStateListOf(
            Alumnos("a1", "Juan Pérez", 8, 5, EstadoPago.PENDIENTE),
            Alumnos("a2", "María López", 10, 10, EstadoPago.DEUDA),
            Alumnos("a3", "Carlos Ruiz", 6, 2, EstadoPago.ADELANTADO)
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cartera de Alumnos") },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { pv ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(pv)
                .padding(horizontal = 16.dp)
        ) {
            Text(
                "Resumen de clases y pagos",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            LazyColumn(modifier = Modifier.weight(1f)) {
                items(alumnos, key = { it.id }) { alumno ->
                    AlumnoCard(
                        alumno = alumno,
                        onClaseDada = {
                            if (alumno.clasesCursadas < alumno.clasesPactadas) {
                                val index = alumnos.indexOf(alumno)
                                alumnos[index] = alumno.copy(
                                    clasesCursadas = alumno.clasesCursadas + 1,
                                    estadoPago = EstadoPago.PENDIENTE
                                )
                            }
                        },
                        onPagoRegistrar = {
                            val index = alumnos.indexOf(alumno)
                            alumnos[index] = alumno.copy(estadoPago = EstadoPago.ADELANTADO)
                        }
                    )
                }
            }

            Button(
                onClick = onVolver,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
            ) {
                Text("Volver")
            }
        }
    }
}

@Composable
fun AlumnoCard(
    alumno: Alumnos,
    onClaseDada: () -> Unit,
    onPagoRegistrar: () -> Unit
) {
    val clasesRestantes = alumno.clasesPactadas - alumno.clasesCursadas
    val colorEstado = when (alumno.estadoPago) {
        EstadoPago.ADELANTADO -> Color(0xFF4CAF50)
        EstadoPago.PENDIENTE -> Color(0xFFFFC107)
        EstadoPago.DEUDA -> Color(0xFFF44336)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Column(Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(alumno.nombre, style = MaterialTheme.typography.titleMedium)
                    Text(
                        "Clases restantes: $clasesRestantes",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        "Estado: ${alumno.estadoPago}",
                        style = MaterialTheme.typography.bodySmall,
                        color = colorEstado
                    )
                }
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(colorEstado)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = onClaseDada,
                    enabled = clasesRestantes > 0,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Check, contentDescription = null)
                    Spacer(Modifier.width(4.dp))
                    Text("Clase dada")
                }

                Button(
                    onClick = onPagoRegistrar,
                    enabled = alumno.estadoPago != EstadoPago.ADELANTADO,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Payment, contentDescription = null)
                    Spacer(Modifier.width(4.dp))
                    Text("Pagar")
                }
            }
        }
    }
}