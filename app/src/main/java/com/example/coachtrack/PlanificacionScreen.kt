package com.example.coachtrack


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID // <-- ¡ESTA ES LA LÍNEA CRÍTICA QUE FALTABA!

// Importamos las pantallas
import com.example.coachtrack.CarteraScreen
import com.example.coachtrack.VideoScreen

// Estados de Navegación dentro de PlanificacionScreen
enum class PlanificacionState {
    PRINCIPAL, CARTERA, VIDEO, FICHA_TECNICA
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanificacionScreen(onVolverClick: () -> Unit) {
    // ESTADOS DE LA SESIÓN Y NAVEGACIÓN
    var currentState by remember { mutableStateOf(PlanificacionState.PRINCIPAL) }
    // Esta lista debe ser mutableStateListOf para observar cambios y evitar el crash.
    val ejerciciosSesion = remember { mutableStateListOf<Plantilla>() }
    var plantillaSeleccionada by remember { mutableStateOf<Plantilla?>(null) }
    var alumnoSeleccionado by remember { mutableStateOf<Alumnos?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Lógica para guardar la sesión
    // La función debe estar tipada como () -> Unit para evitar el error de compilación.
    val onGuardarSesion: () -> Unit = {
        if (alumnoSeleccionado != null && ejerciciosSesion.isNotEmpty()) {

            // **PASO CRÍTICO AÑADIDO:** Formatear la fecha a un String seguro
            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm")
            val fechaGuardado = LocalDateTime.now().format(formatter)

            val nuevoSessionId = generarNuevoSessionId(getMockSesionesGuardadas())
            val nuevaSesion = SesionDeClase(
                sessionId = nuevoSessionId,
                alumnoNombre = alumnoSeleccionado!!.nombre,
                // Usamos la cadena de texto segura y formateada
                fechaCreacion = fechaGuardado,
                duracionTotalMinutos = ejerciciosSesion.sumOf { it.duracionMinutos },
                // Mantenemos la corrección de lista para evitar duplicados y errores
                ejercicios = ejerciciosSesion.toList().toMutableList()
            )
            // ... (El resto del código de Snackbar y limpieza sigue igual)
            getMockSesionesGuardadas().add(0, nuevaSesion)

            val nombreAlumno = alumnoSeleccionado!!.nombre
            scope.launch {
                snackbarHostState.showSnackbar(
                    message = "Sesión guardada con éxito para $nombreAlumno!",
                    duration = SnackbarDuration.Short
                )
            }
            ejerciciosSesion.clear()
            alumnoSeleccionado = null
            currentState = PlanificacionState.PRINCIPAL
            onVolverClick()
        } else {
            scope.launch {
                snackbarHostState.showSnackbar(
                    message = "Debe seleccionar un alumno y al menos un ejercicio.",
                    duration = SnackbarDuration.Short
                )
            }
        }
    }

    // El Box contiene toda la pantalla y permite superponer las vistas de navegación
    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Planificación de Sesión") },
                    navigationIcon = {
                        IconButton(onClick = onVolverClick) {
                            Icon(Icons.Default.List, contentDescription = "Volver al Menú")
                        }
                    }
                    // Botones de acción eliminados de aquí (UX mejorado)
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
                // 1. Selección de Alumno (Fijo)
                AlumnoSelector(
                    alumnoSeleccionado = alumnoSeleccionado,
                    alumnos = remember { getMockAlumnos() },
                    // FIX FINAL: Asegura que el estado del alumno seleccionado se actualice correctamente.
                    onAlumnoSelected = { alumnoSeleccionado = it }
                )

                // 2. Panel de Ejercicios (Scrollable)
                Text(
                    "Ejercicios de la Sesión (${ejerciciosSesion.size}):",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                )

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // FIX CLAVE: Usamos 'instanceId' como clave única para evitar el crash al añadir duplicados.
                    items(ejerciciosSesion, key = { it.instanceId }) { ejercicio ->
                        EjercicioCard(
                            plantilla = ejercicio,
                            onRemove = {
                                ejerciciosSesion.remove(ejercicio)
                            }
                        )
                    }

                    // Lista de Plantillas disponibles
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
                                // Navega a la ficha técnica
                                plantillaSeleccionada = plantilla
                                currentState = PlanificacionState.FICHA_TECNICA
                            }
                        )
                    }
                }

                // 3. Botones Auxiliares (Cartera y Video) - MOVIDOS ABAJO (Mejora UX)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // Botón Cartera
                    Button(
                        onClick = { currentState = PlanificacionState.CARTERA },
                        modifier = Modifier.weight(1f).padding(end = 8.dp)
                    ) {
                        Icon(Icons.Default.List, contentDescription = "Cartera")
                        Spacer(Modifier.width(4.dp))
                        Text("Cartera")
                    }

                    // Botón Video Análisis
                    Button(
                        onClick = { currentState = PlanificacionState.VIDEO },
                        modifier = Modifier.weight(1f).padding(start = 8.dp)
                    ) {
                        Icon(Icons.Default.Videocam, contentDescription = "Video")
                        Spacer(Modifier.width(4.dp))
                        Text("Video")
                    }
                }

                // 4. Botón de Acción Fijo (Guardar)
                Button(
                    onClick = { onGuardarSesion() }, // Llamada segura a la función de guardado
                    enabled = alumnoSeleccionado != null && ejerciciosSesion.isNotEmpty(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                        .height(50.dp)
                ) {
                    Text("GUARDAR Y VOLVER")
                }
            }
        }

        // CONTROL DE NAVEGACIÓN SOBREPUESTA (Modal/Full Screen)
        when (currentState) {
            PlanificacionState.CARTERA -> CarteraScreen(
                onVolver = { currentState = PlanificacionState.PRINCIPAL }
            )
            PlanificacionState.VIDEO -> VideoScreen(
                onVolver = { currentState = PlanificacionState.PRINCIPAL }
            )
            PlanificacionState.FICHA_TECNICA -> {
                plantillaSeleccionada?.let { plantilla ->
                    // Creamos una nueva instancia de la plantilla (con instanceId único)
                    val nuevaInstancia = plantilla.copy(instanceId = UUID.randomUUID().toString())
                    PlantillaDetailScreen(
                        plantilla = nuevaInstancia,
                        onAdd = {
                            // Usamos la nueva instancia única para añadirla a la lista
                            ejerciciosSesion.add(nuevaInstancia)
                            currentState = PlanificacionState.PRINCIPAL
                            plantillaSeleccionada = null // Limpia después de usar
                        },
                        onVolver = { currentState = PlanificacionState.PRINCIPAL }
                    )
                }
            }
            PlanificacionState.PRINCIPAL -> { /* No se superpone nada */ }
        }
    }
}

// Componente para seleccionar alumno
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
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.List, contentDescription = "Alumno")
            Spacer(Modifier.width(8.dp))
            Text(
                alumnoSeleccionado?.nombre ?: "Seleccionar Alumno...",
                style = MaterialTheme.typography.titleMedium
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth(0.9f)
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

// Card de ejercicio ya añadido a la sesión
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
                Icon(Icons.Default.Delete, contentDescription = "Eliminar Ejercicio", tint = Color.Red)
            }
        }
    }
}

// Card de plantilla para añadir
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
            }
        }
    }
}
