package com.example.coachtrack

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlin.collections.filter
import kotlin.collections.isNotEmpty
import kotlin.collections.joinToString

@Composable
fun GestionProfesoresScreen(
    viewModel: ProfesorViewModel = viewModel(),
    onVolverClick: () -> Unit
) {
    val profesores by viewModel.profesores.collectAsState()
    val alumnos by viewModel.alumnos.collectAsState()
    // 🎯 CORRECCIÓN: Usar alumnosDisponibles en lugar de alumnos
    val alumnosDisponibles by viewModel.alumnosDisponibles.collectAsState()

    val showDialog = viewModel.mostrarDialogoAgregar.value
    val profesorAEditar = viewModel.profesorEnEdicion.value

    val state = ProfesorListState(
        profesores = profesores,
        alumnosDisponibles = alumnos // Esto se mantiene para la lista de profesores
    )

    BackHandler {
        if (showDialog) {
            viewModel.cerrarDialogoAgregar()
        } else {
            onVolverClick()
        }
    }

    GestionProfesoresScreenContent(
        state = state,
        // 🎯 CORRECCIÓN: Pasar alumnosDisponibles al contenido
        alumnosDisponibles = alumnosDisponibles,
        onVolverClick = onVolverClick,
        onGuardarProfesor = viewModel::agregarOActualizarProfesor,
        onGuardarConAsignacion = viewModel::agregarOActualizarProfesorConAsignacion,
        onEliminarProfesor = viewModel::eliminarProfesor,
        onCountAssignedAlumnos = viewModel::countAlumnosAsignados,
        onAbrirDialogo = viewModel::abrirDialogoAgregar,
        onCerrarDialogo = viewModel::cerrarDialogoAgregar,
        showDialog = showDialog,
        profesorAEditar = profesorAEditar
    )
}

data class ProfesorListState(
    val profesores: List<ProfesorEntity> = emptyList(),
    val alumnosDisponibles: List<AlumnoEntity> = emptyList()
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GestionProfesoresScreenContent(
    state: ProfesorListState,
    // 🎯 CORRECCIÓN: Nuevo parámetro para alumnos disponibles
    alumnosDisponibles: List<AlumnoEntity>,
    onVolverClick: () -> Unit,
    onGuardarProfesor: (ProfesorEntity) -> Unit,
    onGuardarConAsignacion: (ProfesorEntity, String?) -> Unit,
    onEliminarProfesor: (ProfesorEntity) -> Unit,
    onCountAssignedAlumnos: (Int) -> Int,
    onAbrirDialogo: (ProfesorEntity?) -> Unit,
    onCerrarDialogo: () -> Unit,
    showDialog: Boolean,
    profesorAEditar: ProfesorEntity?
) {

    BackHandler(enabled = true) {
        if (showDialog) {
            onCerrarDialogo()
        } else {
            onVolverClick()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestión de Colegas") },
                navigationIcon = {
                    IconButton(onClick = onVolverClick) {
                        Icon(Icons.Default.ArrowBack, "Volver")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { onAbrirDialogo(null) }) {
                Icon(Icons.Default.Add, "Agregar Colega")
            }
        }
    ) { padding ->

        if (state.profesores.isEmpty()) {
            Box(
                Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "No has agregado colegas. Pulsa '+' para añadir uno.",
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(state.profesores, key = { it.id }) { profesor ->
                    val alumnosAsignados = state.alumnosDisponibles.filter { it.profesorInstructor == profesor.id }

                    ProfesorCard(
                        profesor = profesor,
                        alumnosAsignados = alumnosAsignados,
                        alumnosAsignadosCount = onCountAssignedAlumnos(profesor.id),
                        onEdit = onAbrirDialogo,
                        onDelete = onEliminarProfesor
                    )
                }
            }
        }

        if (showDialog) {
            DialogAgregarProfesor(
                profesorExistente = profesorAEditar,
                // 🎯 CORRECCIÓN: Pasar alumnosDisponibles (solo los disponibles)
                alumnosDisponibles = alumnosDisponibles,
                onDismiss = onCerrarDialogo,
                onGuardar = { profesor, alumnoAsignado ->
                    if (profesor.id == 0)
                        onGuardarConAsignacion(profesor, alumnoAsignado)
                    else {
                        onGuardarProfesor(profesor)
                        if (alumnoAsignado != null)
                            onGuardarConAsignacion(profesor, alumnoAsignado)
                    }
                    onCerrarDialogo()
                }
            )
        }
    }
}

@Composable
fun ProfesorCard(
    profesor: ProfesorEntity,
    alumnosAsignadosCount: Int,
    alumnosAsignados: List<AlumnoEntity>,
    onEdit: (ProfesorEntity) -> Unit,
    onDelete: (ProfesorEntity) -> Unit
) {
    Card(
        Modifier.fillMaxWidth().clickable { onEdit(profesor) }
    ) {
        Row(
            Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(profesor.nombre, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text("Especialidad: ${profesor.especialidad}")

                if (alumnosAsignados.isNotEmpty()) {
                    Text(
                        "Alumnos: ${alumnosAsignados.joinToString { it.nombre }}",
                        color = MaterialTheme.colorScheme.primary
                    )
                } else {
                    Text(
                        "Alumnos asignados: $alumnosAsignadosCount",
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Row {
                IconButton(onClick = { onEdit(profesor) }) {
                    Icon(Icons.Default.Edit, "Editar")
                }
                IconButton(onClick = { onDelete(profesor) }) {
                    Icon(Icons.Default.Delete, "Eliminar")
                }
            }
        }
    }
}