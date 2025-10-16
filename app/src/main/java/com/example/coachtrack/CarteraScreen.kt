package com.example.coachtrack

import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
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
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun CarteraScreen(
    onVolver: () -> Unit,
    onAbrirFichaAlumno: (Alumnos) -> Unit
) {
    var filtro by remember { mutableStateOf("") }
    var showDashboard by remember { mutableStateOf(false) }
    var showList by remember { mutableStateOf(false) }

    // 🔹 Animación secuencial: primero Dashboard, luego Lista
    LaunchedEffect(Unit) {
        delay(300)
        showDashboard = true
        delay(300)
        showList = true
    }

    // ---------------- MOCK DE ALUMNOS ----------------
    val alumnos = remember {
        mutableStateListOf(
            Alumnos("a1", "Juan Pérez", "Intermedio", "Mejorar saque", 8, 5, EstadoPago.PENDIENTE),
            Alumnos("a2", "María López", "Avanzado", "Competencia regional", 10, 10, EstadoPago.DEUDA),
            Alumnos("a3", "Carlos Ruiz", "Inicial", "Consistencia de revés", 6, 2, EstadoPago.ADELANTADO),
            Alumnos("a4", "Laura Martínez", "Intermedio", "Velocidad de desplazamiento", 12, 11, EstadoPago.PENDIENTE),
            Alumnos("a5", "Felipe Gómez", "Inicial", "Aprender técnica de saque", 5, 0, EstadoPago.ADELANTADO),
            Alumnos("a6", "Diego Díaz", "Intermedio", "Resistencia física", 12, 10, EstadoPago.PENDIENTE),
            Alumnos("a7", "Pedro Soto", "Intermedio", "Control de bola", 8, 4, EstadoPago.PENDIENTE),
            Alumnos("a8", "Ana Torres", "Inicial", "Postura básica", 5, 2, EstadoPago.ADELANTADO),
            Alumnos("a9", "José Morales", "Avanzado", "Táctica de partido", 10, 7, EstadoPago.DEUDA),
            Alumnos("a10", "Sofía Rivas", "Intermedio", "Regularidad", 7, 5, EstadoPago.ADELANTADO),
            Alumnos("a11", "Claudio Vega", "Inicial", "Saque con efecto", 6, 3, EstadoPago.PENDIENTE)
        )
    }

    val alumnosFiltrados = alumnos.filter { it.nombre.contains(filtro, ignoreCase = true) }

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
            // ---------------- BUSCADOR ----------------
            OutlinedTextField(
                value = filtro,
                onValueChange = { filtro = it },
                label = { Text("Buscar alumno...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )

            // ---------------- DASHBOARD ----------------
            AnimatedVisibility(
                visible = showDashboard,
                enter = fadeIn(animationSpec = tween(700)) +
                        scaleIn(initialScale = 0.7f, animationSpec = spring(dampingRatio = 0.5f)),
                exit = fadeOut()
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "Resumen general",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(4.dp))
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        DashboardCard("Total", totalAlumnos.toString(), Color(0xFF1565C0), Modifier.weight(1f))
                        Spacer(Modifier.width(8.dp))
                        DashboardCard("Al día", alDia.toString(), Color(0xFF2E7D32), Modifier.weight(1f))
                    }
                    Spacer(Modifier.height(8.dp))
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        DashboardCard("Pendientes", pendientes.toString(), Color(0xFFFFA000), Modifier.weight(1f))
                        Spacer(Modifier.width(8.dp))
                        DashboardCard("Deuda", enDeuda.toString(), Color(0xFFC62828), Modifier.weight(1f))
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            // ---------------- AVISO ----------------
            val hayPendientes = (enDeuda + pendientes) > 0
            val avisoColor by animateColorAsState(
                if (hayPendientes) Color(0xFFFFCCBC) else Color(0xFFC8E6C9),
                animationSpec = spring(dampingRatio = 0.7f)
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = avisoColor)
            ) {
                Text(
                    text = if (hayPendientes)
                        "¡ATENCIÓN! ${enDeuda + pendientes} alumnos con pagos pendientes"
                    else
                        "Cartera al día. ¡Excelente!",
                    modifier = Modifier.padding(12.dp),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = if (hayPendientes) Color(0xFFD32F2F) else Color(0xFF388E3C),
                    textAlign = TextAlign.Center
                )
            }

            Spacer(Modifier.height(8.dp))

            // ---------------- LISTA ----------------
            AnimatedVisibility(
                visible = showList,
                enter = fadeIn(animationSpec = tween(600)) +
                        slideInVertically(initialOffsetY = { it / 2 }),
                exit = fadeOut()
            ) {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
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
                                println("Enviando WhatsApp a ${alumno.nombre}")
                            },
                            onAbrirFichaAlumno = { onAbrirFichaAlumno(alumno) }
                        )
                    }
                }
            }

            // ---------------- BOTÓN VOLVER ----------------
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

// ---------------- DASHBOARD CARD ----------------
@Composable
fun DashboardCard(title: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.08f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value, color = color, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
            Text(title, style = MaterialTheme.typography.bodySmall, color = color.copy(alpha = 0.8f))
        }
    }
}

// ---------------- CARD DE ALUMNO ----------------
@Composable
fun AlumnoCard(
    alumno: Alumnos,
    onClaseDada: () -> Unit,
    onPagoRegistrar: () -> Unit,
    onContactoRapido: () -> Unit,
    onAbrirFichaAlumno: (Alumnos) -> Unit
) {
    val progresoAnimado by animateFloatAsState(
        targetValue = alumno.clasesCursadas.toFloat() / alumno.clasesPactadas.toFloat(),
        animationSpec = spring(dampingRatio = 0.7f)
    )

    val colorEstado by animateColorAsState(
        when (alumno.estadoPago) {
            EstadoPago.ADELANTADO -> Color(0xFF4CAF50)
            EstadoPago.PENDIENTE -> Color(0xFFFFC107)
            EstadoPago.DEUDA -> Color(0xFFF44336)
        },
        animationSpec = spring(dampingRatio = 0.7f)
    )

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
                        progress = { progresoAnimado },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(CircleShape),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                    Text(
                        "Clases: ${alumno.clasesCursadas} / ${alumno.clasesPactadas}",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
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
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = onClaseDada, modifier = Modifier.weight(1f)) {
                    Icon(Icons.Default.Check, null, Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Clase")
                }
                Button(onClick = onPagoRegistrar, modifier = Modifier.weight(1f)) {
                    Icon(Icons.Default.Payment, null, Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Pagar")
                }
                if (alumno.estadoPago != EstadoPago.ADELANTADO) {
                    IconButton(onClick = onContactoRapido) {
                        Icon(Icons.Default.Send, null, tint = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }
}
