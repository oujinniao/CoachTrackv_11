package com.example.coachtrack

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Search
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
    onVolver: () -> Unit,
    onAbrirFichaAlumno: (Alumnos) -> Unit
) {
    var filtro by remember { mutableStateOf("") }
    var filtroEstado by remember { mutableStateOf<EstadoPago?>(null) }

    val alumnos = remember {
        mutableStateListOf(
            Alumnos("a1", "Juan Pérez", "Intermedio", "Mejorar saque", 8, 5, EstadoPago.PENDIENTE),
            Alumnos("a2", "María López", "Avanzado", "Competencia regional", 10, 10, EstadoPago.DEUDA),
            Alumnos("a3", "Carlos Ruiz", "Inicial", "Consistencia de revés", 6, 2, EstadoPago.ADELANTADO),
            Alumnos("a4", "Laura Martínez", "Intermedio", "Velocidad de desplazamiento", 12, 11, EstadoPago.PENDIENTE),
            Alumnos("a5", "Felipe Gómez", "Inicial", "Aprender técnica de saque", 5, 0, EstadoPago.ADELANTADO),
            Alumnos("a6", "Tomás Vega", "Avanzado", "Fondo físico", 8, 8, EstadoPago.DEUDA),
            Alumnos("a7", "Sofía Herrera", "Intermedio", "Saque y volea", 9, 6, EstadoPago.PENDIENTE),
            Alumnos("a8", "Martín Castillo", "Inicial", "Revés cruzado", 4, 2, EstadoPago.ADELANTADO),
            Alumnos("a9", "Valentina Torres", "Avanzado", "Táctica de partido", 10, 9, EstadoPago.PENDIENTE),
            Alumnos("a10", "Ignacio Rojas", "Inicial", "Golpe de derecha", 6, 3, EstadoPago.DEUDA)
        )
    }

    val total = alumnos.size
    val pendientes = alumnos.count { it.estadoPago == EstadoPago.PENDIENTE }
    val alDia = alumnos.count { it.estadoPago == EstadoPago.ADELANTADO }
    val enDeuda = alumnos.count { it.estadoPago == EstadoPago.DEUDA }

    val alumnosFiltrados = alumnos.filter {
        (filtroEstado == null || it.estadoPago == filtroEstado) &&
                it.nombre.contains(filtro, ignoreCase = true)
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
            // 🔍 Campo de búsqueda
            OutlinedTextField(
                value = filtro,
                onValueChange = { filtro = it },
                label = { Text("Buscar alumno...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )

            // 🟦 Dashboard compacto interactivo
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                DashboardMiniCard("Total", total.toString(), Color(0xFF1565C0)) { filtroEstado = null }
                DashboardMiniCard("Al día", alDia.toString(), Color(0xFF2E7D32)) { filtroEstado = EstadoPago.ADELANTADO }
                DashboardMiniCard("Pend.", pendientes.toString(), Color(0xFFFFA000)) { filtroEstado = EstadoPago.PENDIENTE }
                DashboardMiniCard("Deuda", enDeuda.toString(), Color(0xFFC62828)) { filtroEstado = EstadoPago.DEUDA }
            }

            if (filtroEstado != null) {
                TextButton(
                    onClick = { filtroEstado = null },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Quitar filtro")
                }
            }

            // 📋 Lista de alumnos
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(alumnosFiltrados, key = { it.id }) { alumno ->
                    AlumnoCard(
                        alumno = alumno,
                        onClaseDada = {
                            if (alumno.clasesCursadas < alumno.clasesPactadas) {
                                val index = alumnos.indexOf(alumno)
                                alumnos[index] = alumno.copy(
                                    clasesCursadas = alumno.clasesCursadas + 1,
                                    estadoPago = if (alumno.clasesCursadas + 1 >= alumno.clasesPactadas)
                                        EstadoPago.PENDIENTE else alumno.estadoPago
                                )
                            }
                        },
                        onPagoRegistrar = {
                            val index = alumnos.indexOf(alumno)
                            alumnos[index] = alumno.copy(estadoPago = EstadoPago.ADELANTADO)
                        },
                        onContactoRapido = {
                            println("Enviando mensaje a ${alumno.nombre} por pago pendiente...")
                        },
                        onAbrirFichaAlumno = { onAbrirFichaAlumno(alumno) }
                    )
                }
            }

            // 🔙 Botón de volver
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

// 🔹 Tarjeta pequeña del Dashboard
@Composable
fun DashboardMiniCard(titulo: String, valor: String, color: Color, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .size(width = 85.dp, height = 80.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.12f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = titulo,
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = color
            )
            Text(
                text = valor,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = Color.Black
            )
        }
    }
}

// 🔹 Tarjeta individual de alumno
@Composable
fun AlumnoCard(
    alumno: Alumnos,
    onClaseDada: () -> Unit,
    onPagoRegistrar: () -> Unit,
    onContactoRapido: () -> Unit,
    onAbrirFichaAlumno: (Alumnos) -> Unit
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
            .clickable { onAbrirFichaAlumno(alumno) }
    ) {
        Column(Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(alumno.nombre, style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
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
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = onClaseDada, enabled = clasesRestantes > 0, modifier = Modifier.weight(1f)) {
                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Clase")
                }
                Button(onClick = onPagoRegistrar, enabled = alumno.estadoPago != EstadoPago.ADELANTADO, modifier = Modifier.weight(1f)) {
                    Icon(Icons.Default.Payment, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Pagar")
                }
                if (alumno.estadoPago == EstadoPago.DEUDA || alumno.estadoPago == EstadoPago.PENDIENTE) {
                    IconButton(onClick = onContactoRapido, modifier = Modifier.align(Alignment.CenterVertically)) {
                        Icon(Icons.Default.Send, contentDescription = "Enviar WhatsApp", tint = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }
}
