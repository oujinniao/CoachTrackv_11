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
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarteraScreen(
    onVolver: () -> Unit
) {
    // Alumnos (Manteniendo el Mock para la demostración)
    val alumnos = remember {
        mutableStateListOf(
            Alumnos("a1", "Juan Pérez", 8, 5, EstadoPago.PENDIENTE),
            Alumnos("a2", "María López", 10, 10, EstadoPago.DEUDA),
            Alumnos("a3", "Carlos Ruiz", 6, 2, EstadoPago.ADELANTADO),
            Alumnos("a4", "Laura Martínez", 12, 11, EstadoPago.PENDIENTE),
            Alumnos("a5", "Felipe Gómez", 5, 0, EstadoPago.ADELANTADO)
        )
    }

    // Calculamos el resumen para el encabezado
    val totalDeuda = remember {
        derivedStateOf { alumnos.count { it.estadoPago == EstadoPago.DEUDA || it.estadoPago == EstadoPago.PENDIENTE } }
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
            // Nuevo: Tarjeta de Resumen Rápido (Control del dinero)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (totalDeuda.value > 0) Color(0xFFFFCCBC) else Color(0xFFC8E6C9)
                )
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        if (totalDeuda.value > 0) {
                            "¡ATENCIÓN! ${totalDeuda.value} alumnos pendientes de pago"
                        } else {
                            "Cartera al día. ¡Excelente!"
                        },
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = if (totalDeuda.value > 0) Color(0xFFD32F2F) else Color(0xFF388E3C)
                    )
                }
            }


            LazyColumn(modifier = Modifier.weight(1f)) {
                items(alumnos, key = { it.id }) { alumno ->
                    AlumnoCard(
                        alumno = alumno,
                        onClaseDada = {
                            if (alumno.clasesCursadas < alumno.clasesPactadas) {
                                val index = alumnos.indexOf(alumno)
                                alumnos[index] = alumno.copy(
                                    clasesCursadas = alumno.clasesCursadas + 1,
                                    estadoPago = if (alumno.clasesCursadas + 1 >= alumno.clasesPactadas) EstadoPago.PENDIENTE else alumno.estadoPago
                                )
                            }
                        },
                        onPagoRegistrar = {
                            val index = alumnos.indexOf(alumno)
                            alumnos[index] = alumno.copy(estadoPago = EstadoPago.ADELANTADO)
                        },
                        onContactoRapido = {
                            // Simulacion de accion WSP
                            println("Simulando envío de mensaje de WhatsApp a ${alumno.nombre} por pago pendiente.")
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
    onPagoRegistrar: () -> Unit,
    onContactoRapido: () -> Unit // Nueva funcion para el boton de contacto
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
                    Text(alumno.nombre, style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))

                    // Nuevo: Contador de Clases con progreso
                    LinearProgressIndicator(
                        progress = { alumno.clasesCursadas.toFloat() / alumno.clasesPactadas.toFloat() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(CircleShape),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )

                    Text(
                        "Clases: ${alumno.clasesCursadas} de ${alumno.clasesPactadas} (Restan: $clasesRestantes)",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 4.dp)
                    )

                }

                // Indicador de Estado de Pago (grande y visible)
                Column(
                    modifier = Modifier.width(60.dp),
                    horizontalAlignment = Alignment.End
                ) {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(colorEstado)
                    )
                    Text(
                        alumno.estadoPago.toString(),
                        style = MaterialTheme.typography.labelSmall,
                        color = colorEstado,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Fila de acciones (Clase dada, Pagar, Contacto Rápido)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = onClaseDada,
                    enabled = clasesRestantes > 0,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Clase")
                }

                Button(
                    onClick = onPagoRegistrar,
                    enabled = alumno.estadoPago != EstadoPago.ADELANTADO,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Payment, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Pagar")
                }

                // Nuevo: Botón de Contacto Rápido
                if (alumno.estadoPago == EstadoPago.DEUDA || alumno.estadoPago == EstadoPago.PENDIENTE) {
                    IconButton(
                        onClick = onContactoRapido,
                        modifier = Modifier.align(Alignment.CenterVertically)
                    ) {
                        Icon(
                            Icons.Default.Send,
                            contentDescription = "Enviar WhatsApp",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}
