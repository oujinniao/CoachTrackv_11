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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarteraScreen(
    onVolver: () -> Unit,
    onAbrirFichaAlumno: (Alumnos) -> Unit
) {
    var filtro by remember { mutableStateOf("") }

    // Mock local
    val alumnos = remember {
        mutableStateListOf(
            Alumnos("a1", "Juan Pérez", "Intermedio", "Mejorar saque", 8, 5, EstadoPago.PENDIENTE),
            Alumnos("a2", "María López", "Avanzado", "Competencia regional", 10, 10, EstadoPago.DEUDA),
            Alumnos("a3", "Carlos Ruiz", "Inicial", "Consistencia de revés", 6, 2, EstadoPago.ADELANTADO),
            Alumnos("a4", "Laura Martínez", "Intermedio", "Velocidad de desplazamiento", 12, 11, EstadoPago.PENDIENTE),
            Alumnos("a5", "Felipe Gómez", "Inicial", "Aprender técnica de saque", 5, 0, EstadoPago.ADELANTADO),
            Alumnos("a6", "Diego Díaz", "Intermedio", "Resistencia física", 12, 10, EstadoPago.PENDIENTE)
        )
    }

    // Filtro + orden por progreso
    val alumnosFiltrados = remember(filtro, alumnos) {
        alumnos
            .filter { it.nombre.contains(filtro, ignoreCase = true) }
            .sortedByDescending { it.clasesCursadas.toFloat() / it.clasesPactadas.toFloat() }
    }

    // KPIs
    val totalAlumnos = alumnos.size
    val alDia = alumnos.count { it.estadoPago == EstadoPago.ADELANTADO }
    val pendientes = alumnos.count { it.estadoPago == EstadoPago.PENDIENTE }
    val enDeuda = alumnos.count { it.estadoPago == EstadoPago.DEUDA }

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

            // 🔎 Buscar
            OutlinedTextField(
                value = filtro,
                onValueChange = { filtro = it },
                label = { Text("Buscar alumno...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )

            // 📊 Dashboard 2x2 (evita corte en pantallas angostas)
            Row(Modifier.fillMaxWidth()) {
                DashboardCard(title = "Total", value = totalAlumnos.toString(), color = Color(0xFF1565C0), modifier = Modifier.weight(1f))
                Spacer(Modifier.width(8.dp))
                DashboardCard(title = "Al día", value = alDia.toString(), color = Color(0xFF2E7D32), modifier = Modifier.weight(1f))
            }
            Spacer(Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth()) {
                DashboardCard(title = "Pendientes", value = pendientes.toString(), color = Color(0xFFFFA000), modifier = Modifier.weight(1f))
                Spacer(Modifier.width(8.dp))
                DashboardCard(title = "Deuda", value = enDeuda.toString(), color = Color(0xFFC62828), modifier = Modifier.weight(1f))
            }

            // Separación sutil antes del aviso
            Spacer(Modifier.height(8.dp))

            // 💰 Aviso de pagos
            val hayPendientes = (enDeuda + pendientes) > 0
            Card(
                modifier = Modifier
                    .fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (hayPendientes) Color(0xFFFFCCBC) else Color(0xFFC8E6C9)
                )
            ) {
                Text(
                    text = if (hayPendientes)
                        "¡ATENCIÓN! ${enDeuda + pendientes} alumnos con pagos pendientes"
                    else
                        "Cartera al día. ¡Excelente!",
                    modifier = Modifier.padding(12.dp),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = if (hayPendientes) Color(0xFFD32F2F) else Color(0xFF388E3C)
                )
            }

            // 📝 Lista
            Spacer(Modifier.height(8.dp))
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(alumnosFiltrados, key = { it.id }) { alumno ->
                    AlumnoCard(
                        alumno = alumno,
                        onClaseDada = {
                            if (alumno.clasesCursadas < alumno.clasesPactadas) {
                                val i = alumnos.indexOf(alumno)
                                alumnos[i] = alumno.copy(
                                    clasesCursadas = alumno.clasesCursadas + 1,
                                    estadoPago = if (alumno.clasesCursadas + 1 >= alumno.clasesPactadas)
                                        EstadoPago.PENDIENTE else alumno.estadoPago
                                )
                            }
                        },
                        onPagoRegistrar = {
                            val i = alumnos.indexOf(alumno)
                            alumnos[i] = alumno.copy(estadoPago = EstadoPago.ADELANTADO)
                        },
                        onContactoRapido = {
                            println("Simulando WhatsApp a ${alumno.nombre}")
                        },
                        onAbrirFichaAlumno = { onAbrirFichaAlumno(alumno) }
                    )
                }
            }

            // ⬅ Volver
            Button(
                onClick = onVolver,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
            ) { Text("Volver") }
        }
    }
}

// ---------- UI helpers ----------

@Composable
private fun DashboardCard(
    title: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.10f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                color = color,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 20.sp,                // número grande
                textAlign = TextAlign.Center
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = color.copy(alpha = 0.9f)
            )
        }
    }
}

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
                    Text(
                        alumno.nombre,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
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

            Spacer(Modifier.height(12.dp))

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
