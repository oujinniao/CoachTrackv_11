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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.ui.window.Dialog
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun CarteraScreen(
    onVolver: () -> Unit,
    onAbrirFichaAlumno: (Alumnos) -> Unit
) {
    var filtro by remember { mutableStateOf("") }
    var filtroEstado by remember { mutableStateOf<EstadoPago?>(null) }
    var showDashboard by remember { mutableStateOf(false) }
    var showList by remember { mutableStateOf(false) }
    var showAddDialog by remember { mutableStateOf(false) } // 👈 NUEVO
    val snackbarHostState = remember { SnackbarHostState() }

    val coroutineScope = rememberCoroutineScope()


    // 🔹 Animaciones de entrada
    LaunchedEffect(Unit) {
        delay(300)
        showDashboard = true
        delay(300)
        showList = true
    }

    // 🔹 Lista en memoria (mutable)
    val alumnos = remember {
        mutableStateListOf(
            Alumnos(
                id = "a1",
                nombre = "Juan Pérez",
                nivel = "Intermedio",
                objetivo = "Mejorar saque",
                clasesPactadas = 8,
                clasesCursadas = 5,
                estadoPago = EstadoPago.PENDIENTE,
                datosPersonales = DatosPersonales(
                    edad = 28,
                    sexo = "Masculino",
                    altura = 180,
                    peso = 78,
                    direccion = "Av. Providencia 1000, Santiago",
                    telefono = "987654321",
                    email = "juanperez@gmail.com",
                    fechaNacimiento = "1996-02-18"
                ),
                nivelJuego = "Club Amateur"
            ),
            Alumnos(
                id = "a2",
                nombre = "María López",
                nivel = "Avanzado",
                objetivo = "Competencia regional",
                clasesPactadas = 10,
                clasesCursadas = 10,
                estadoPago = EstadoPago.ADELANTADO,
                datosPersonales = DatosPersonales(
                    edad = 26,
                    sexo = "Femenino",
                    altura = 170,
                    peso = 65,
                    direccion = "Calle Los Leones 700",
                    telefono = "986543210",
                    email = "maria.lopez@gmail.com",
                    fechaNacimiento = "1998-05-12"
                ),
                nivelJuego = "Torneo local"
            )
        )
    }

    // 🔹 Filtros
    val alumnosFiltradosPorNombre = alumnos.filter { it.nombre.contains(filtro, ignoreCase = true) }
    val alumnosFiltrados = alumnosFiltradosPorNombre.filter {
        filtroEstado == null || it.estadoPago == filtroEstado
    }

    val totalAlumnos = alumnos.size
    val alDia = alumnos.count { it.estadoPago == EstadoPago.ADELANTADO }
    val pendientes = alumnos.count { it.estadoPago == EstadoPago.PENDIENTE }
    val enDeuda = alumnos.count { it.estadoPago == EstadoPago.DEUDA }


    // ------------------- UI -------------------
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
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar Alumno")
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
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
                        DashboardCard(
                            "Total", totalAlumnos.toString(), Color(0xFF1565C0),
                            modifier = Modifier.weight(1f)
                        ) { filtroEstado = null }

                        Spacer(Modifier.width(8.dp))

                        DashboardCard(
                            "Al día", alDia.toString(), Color(0xFF2E7D32),
                            modifier = Modifier.weight(1f)
                        ) { filtroEstado = EstadoPago.ADELANTADO }
                    }
                    Spacer(Modifier.height(8.dp))
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        DashboardCard(
                            "Pendientes", pendientes.toString(), Color(0xFFFFA000),
                            modifier = Modifier.weight(1f)
                        ) { filtroEstado = EstadoPago.PENDIENTE }

                        Spacer(Modifier.width(8.dp))

                        DashboardCard(
                            "Deuda", enDeuda.toString(), Color(0xFFC62828),
                            modifier = Modifier.weight(1f)
                        ) { filtroEstado = EstadoPago.DEUDA }
                    }
                }
            }

            if (filtroEstado != null) {
                TextButton(
                    onClick = { filtroEstado = null },
                    modifier = Modifier.align(Alignment.End)
                ) { Text("Quitar filtro") }
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

        // ---------------- DIALOGO AGREGAR ALUMNO ----------------
        if (showAddDialog) {
            Dialog(onDismissRequest = { showAddDialog = false }) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    // Estados de cada campo
                    var nombre by remember { mutableStateOf("") }
                    var nivel by remember { mutableStateOf("") }
                    var objetivo by remember { mutableStateOf("") }
                    var edad by remember { mutableStateOf("") }
                    var telefono by remember { mutableStateOf("") }
                    var direccion by remember { mutableStateOf("") }
                    var email by remember { mutableStateOf("") }
                    var sexo by remember { mutableStateOf("") }
                    var nivelJuego by remember { mutableStateOf("") }

                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            "Agregar nuevo alumno",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )

                        // -------------- Datos básicos --------------
                        OutlinedTextField(
                            value = nombre,
                            onValueChange = { nombre = it },
                            label = { Text("Nombre completo") })
                        OutlinedTextField(
                            value = nivel,
                            onValueChange = { nivel = it },
                            label = { Text("Nivel actual") })
                        OutlinedTextField(
                            value = objetivo,
                            onValueChange = { objetivo = it },
                            label = { Text("Objetivo principal") })

                        // -------------- Datos personales --------------
                        Divider(Modifier.padding(vertical = 8.dp))
                        Text("Datos personales", fontWeight = FontWeight.SemiBold)
                        OutlinedTextField(
                            value = edad,
                            onValueChange = { edad = it },
                            label = { Text("Edad") },
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = sexo,
                            onValueChange = { sexo = it },
                            label = { Text("Sexo") },
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = telefono,
                            onValueChange = { telefono = it },
                            label = { Text("Teléfono") })
                        OutlinedTextField(
                            value = direccion,
                            onValueChange = { direccion = it },
                            label = { Text("Dirección") })
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text("Correo electrónico") })
                        OutlinedTextField(
                            value = nivelJuego,
                            onValueChange = { nivelJuego = it },
                            label = { Text("Nivel de juego") })

                        Spacer(Modifier.height(12.dp))

                        // -------------- Botones --------------
                        Row(
                            horizontalArrangement = Arrangement.End,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            TextButton(onClick = { showAddDialog = false }) {
                                Text("Cancelar")
                            }
                            Button(onClick = {
                                if (nombre.isNotBlank()) {
                                    alumnos.add(
                                        Alumnos(
                                            id = "a${alumnos.size + 1}",
                                            nombre = nombre,
                                            nivel = nivel,
                                            objetivo = objetivo,
                                            clasesPactadas = 0,
                                            clasesCursadas = 0,
                                            estadoPago = EstadoPago.PENDIENTE,
                                            datosPersonales = DatosPersonales(
                                                edad = edad.toIntOrNull() ?: 0,
                                                sexo = sexo,
                                                telefono = telefono,
                                                direccion = direccion,
                                                email = email
                                            ),
                                            nivelJuego = nivelJuego
                                        )
                                    )
                                    showAddDialog = false

                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar("Alumno agregado correctamente")
                                    }
                                }
                            }) {
                                Text("Guardar")
                            }
                        }
                    }
                }
            }
        }
    }
}


// ---------------- DASHBOARD CARD ----------------
@Composable
fun DashboardCard(
    title: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.08f))
    ) {
        Column(
            Modifier
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
    onAbrirFichaAlumno: (Alumnos) -> Unit
) {
    val progresoAnimado by animateFloatAsState(
        targetValue = alumno.clasesCursadas.toFloat() / alumno.clasesPactadas.toFloat(),
        animationSpec = spring(dampingRatio = 0.7f)
    )

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
        }
    }
}
