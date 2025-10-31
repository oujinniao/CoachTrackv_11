package com.example.coachtrack

import android.app.Application
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

// -------------------- ESTADOS DE NAVEGACIÓN --------------------
enum class PlanificacionState {
    PRINCIPAL,
    CARTERA,
    VIDEO
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanificacionScreen(onVolverClick: () -> Unit) {
    var currentState by remember { mutableStateOf(PlanificacionState.PRINCIPAL) }
    val ejerciciosSesion = remember { mutableStateListOf<Plantilla>() }
    var alumnoSeleccionado by remember { mutableStateOf<Alumnos?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // -------------------- ViewModel: traemos los alumnos reales desde Room --------------------
    val context = LocalContext.current
    val viewModel: CarteraViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return CarteraViewModel(context.applicationContext as Application) as T
            }
        }
    )

    // Observamos los alumnos reales en la base de datos
    val alumnosRoom by viewModel.alumnos.collectAsState(initial = emptyList())

    // Mapeamos AlumnoEntity -> Alumnos (modelo de dominio)
    val alumnos = alumnosRoom.map { entity ->
        Alumnos(
            id = entity.id.toString(),
            nombre = entity.nombre,
            nivelActual = entity.nivelActual,
            objetivo = entity.objetivo,
            clasesPactadas = entity.clasesPactadas,
            clasesCursadas = entity.clasesCursadas,
            estadoPago = EstadoPago.valueOf(entity.estadoPago),
            datosPersonales = DatosPersonales(
                edad = entity.edad,
                telefono = entity.telefono,
                direccion = entity.direccion
            )
        )
    }

    // -------------------- LÓGICA DE GUARDADO --------------------
    val onGuardarSesion: () -> Unit = {
        val alumno = alumnoSeleccionado

        if (alumno == null) {
            scope.launch { snackbarHostState.showSnackbar("Debe seleccionar un alumno.") }
        } else if (ejerciciosSesion.isEmpty()) {
            scope.launch { snackbarHostState.showSnackbar("Debe añadir al menos una plantilla.") }
        } else {
            try {
                val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm")
                val fechaGuardado = LocalDateTime.now().format(formatter)
                val nuevoSessionId = generarNuevoSessionId(getMockSesionesGuardadas())

                val nuevaSesion = SesionDeClase(
                    sessionId = nuevoSessionId,
                    alumnoNombre = alumno.nombre,
                    fechaCreacion = fechaGuardado,
                    duracionTotalMinutos = ejerciciosSesion.sumOf { it.duracionMinutos },
                    ejercicios = ejerciciosSesion.toList().toMutableList()
                )

                val sesionesCopia = getMockSesionesGuardadas().toMutableList()
                sesionesCopia.add(0, nuevaSesion)
                SESIONES_GUARDADAS.clear()
                SESIONES_GUARDADAS.addAll(sesionesCopia)

                scope.launch {
                    snackbarHostState.showSnackbar("Sesión guardada con éxito para ${alumno.nombre}.")
                }

                ejerciciosSesion.clear()
                alumnoSeleccionado = null
                currentState = PlanificacionState.PRINCIPAL
            } catch (e: Exception) {
                scope.launch {
                    snackbarHostState.showSnackbar("Error al guardar: ${e.message}")
                }
            }
        }
    }

    // -------------------- INTERFAZ PRINCIPAL --------------------
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Planificación de Sesión") },
                navigationIcon = {
                    IconButton(onClick = onVolverClick) {
                        Icon(Icons.Default.List, contentDescription = "Volver")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { pv ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(pv)
                .padding(horizontal = 16.dp)
        ) {
            // ---- SELECCIÓN DE ALUMNO (ahora usa lista real) ----
            AlumnoSelector(
                alumnoSeleccionado = alumnoSeleccionado,
                alumnos = alumnos, // ✅ lista de Room, ya no mock
                onAlumnoSelected = { alumnoSeleccionado = it }
            )

            // ---- LISTA DE EJERCICIOS ----
            Text(
                "Ejercicios de la Sesión (${ejerciciosSesion.size}):",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
            )

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val ejerciciosCopia = ejerciciosSesion.toList()
                items(ejerciciosCopia, key = { it.instanceId }) { ejercicio ->
                    EjercicioCard(
                        plantilla = ejercicio,
                        onRemove = { ejerciciosSesion.remove(ejercicio) }
                    )
                }

                item {
                    Divider(Modifier.padding(vertical = 8.dp))
                    Text(
                        "Plantillas Disponibles:",
                        style = MaterialTheme.typography.titleSmall
                    )
                }

                items(PLANTILLAS_MOCK, key = { it.id }) { plantilla ->
                    PlantillaCard(
                        plantilla = plantilla,
                        onAdd = {
                            ejerciciosSesion.add(
                                plantilla.copy(instanceId = UUID.randomUUID().toString())
                            )
                        }
                    )
                }
            }

            // ---- BOTONES SECUNDARIOS ----
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = { currentState = PlanificacionState.CARTERA },
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp)
                ) {
                    Icon(Icons.Default.List, contentDescription = "Cartera")
                    Spacer(Modifier.width(4.dp))
                    Text("Cartera")
                }

                Button(
                    onClick = { currentState = PlanificacionState.VIDEO },
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp)
                ) {
                    Icon(Icons.Default.Videocam, contentDescription = "Video")
                    Spacer(Modifier.width(4.dp))
                    Text("Video")
                }
            }

            // ---- BOTÓN GUARDAR ----
            Button(
                onClick = onGuardarSesion,
                enabled = alumnoSeleccionado != null && ejerciciosSesion.isNotEmpty(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
                    .height(50.dp)
            ) {
                Text("GUARDAR SESIÓN")
            }
        }
    }

    // -------------------- SUBPANTALLAS --------------------
    when (currentState) {
        PlanificacionState.CARTERA -> CarteraScreen(
            onVolver = { currentState = PlanificacionState.PRINCIPAL },
            onAbrirFichaAlumno = { alumnoSeleccionado = it }
        )

        PlanificacionState.VIDEO -> VideoScreen(
            onVolver = { currentState = PlanificacionState.PRINCIPAL }
        )

        else -> {}
    }
}

// -------------------- COMPONENTES AUXILIARES --------------------

@Composable
fun AlumnoSelector(
    alumnoSeleccionado: Alumnos?,
    alumnos: List<Alumnos>,
    onAlumnoSelected: (Alumnos) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
            .clickable { expanded = true }
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = alumnoSeleccionado?.nombre ?: "Seleccionar Alumno...",
                    style = MaterialTheme.typography.titleMedium
                )
                alumnoSeleccionado?.let {
                    Text(
                        text = "Nivel: ${it.nivelActual} | Objetivo: ${it.objetivo}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = "Abrir lista",
                tint = MaterialTheme.colorScheme.primary
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            alumnos.forEach { alumno ->
                DropdownMenuItem(
                    text = { Text(alumno.nombre) },
                    onClick = {
                        onAlumnoSelected(alumno)
                        expanded = false
                    }
                )
            }
        }
    }
}

// ---- Tarjeta para ejercicio agregado ----
@Composable
fun EjercicioCard(
    plantilla: Plantilla,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(plantilla.nombre, style = MaterialTheme.typography.titleSmall)
                Text(
                    "Duración: ${plantilla.duracionMinutos} min | Enfoque: ${plantilla.enfoque}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            IconButton(onClick = onRemove) {
                Icon(Icons.Default.Delete, contentDescription = "Eliminar ejercicio", tint = Color.Red)
            }
        }
    }
}

// ---- Tarjeta para plantilla disponible ----
@Composable
fun PlantillaCard(
    plantilla: Plantilla,
    onAdd: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(plantilla.nombre, style = MaterialTheme.typography.titleSmall)
                Text(
                    "Duración: ${plantilla.duracionMinutos} min | Enfoque: ${plantilla.enfoque}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Button(onClick = onAdd) {
                Icon(Icons.Default.Add, contentDescription = "Añadir a sesión")
                Spacer(Modifier.width(6.dp))
                Text("Añadir")
            }
        }
    }
}
