package com.example.coachtrack

import android.util.Log
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
import kotlin.collections.find
import kotlin.collections.isNotEmpty
import kotlin.collections.joinToString

@Composable
fun GestionProfesoresScreen(
    viewModel: ProfesorViewModel = viewModel(),
    onVolverClick: () -> Unit
) {
    // Observamos las listas (StateFlow -> State)
    val profesores by viewModel.profesores.collectAsState()
    val alumnos by viewModel.alumnos.collectAsState()

    // Estados UI expuestos desde el ViewModel (MutableState) -> leemos .value
    val showDialog = viewModel.mostrarDialogoAgregar.value
    val profesorAEditar = viewModel.profesorEnEdicion.value

    val state = ProfesorListState(
        profesores = profesores,
        alumnosDisponibles = alumnos,
        isLoading = false
    )

    GestionProfesoresScreenContent(
        state = state,
        onVolverClick = onVolverClick,
        // para compatibilidad mantenemos onGuardarProfesor simple (sin asignación)
        onGuardarProfesor = viewModel::agregarOActualizarProfesor,
        // nueva lambda que crea/actualiza y opcionalmente asigna alumno
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
    val alumnosDisponibles: List<AlumnoEntity> = emptyList(),
    val isLoading: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GestionProfesoresScreenContent(
    state: ProfesorListState,
    onVolverClick: () -> Unit,
    onGuardarProfesor: (ProfesorEntity) -> Unit,
    onGuardarConAsignacion: (ProfesorEntity, Int?) -> Unit,
    onEliminarProfesor: (ProfesorEntity) -> Unit,
    onCountAssignedAlumnos: (Int) -> Int,
    onAbrirDialogo: (ProfesorEntity?) -> Unit,
    onCerrarDialogo: () -> Unit,
    showDialog: Boolean,
    profesorAEditar: ProfesorEntity?
) {

    val onProfesorClick: (ProfesorEntity) -> Unit = { profesor ->
        Log.d("GestionProfesoresScreen", "Edit clicked prof=${profesor.id} alumnosCount=${state.alumnosDisponibles.size} assignedId=${
            state.alumnosDisponibles.find { it.profesorInstructor == profesor.id }?.id
        }")
        state.alumnosDisponibles.forEach { alumno ->
            Log.d("GestionProfesoresScreen", "alumno id=${alumno.id} nombre=${alumno.nombre} profesorInstructor=${alumno.profesorInstructor}")
        }
        onAbrirDialogo(profesor)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestión de Colegas") },
                navigationIcon = {
                    IconButton(onClick = onVolverClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver a Gestión")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { onAbrirDialogo(null) }) {
                Icon(Icons.Default.Add, contentDescription = "Agregar Colega")
            }
        }
    ) { padding ->
        // Mostramos mensaje si no hay profesores pero no hacemos early-return (para que el diálogo se muestre)
        if (state.profesores.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No has agregado colegas. Pulsa '+' para añadir un nuevo profesor y delegar alumnos.",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(state.profesores, key = { it.id }) { profesor ->
                    val asignados = state.alumnosDisponibles.filter { it.profesorInstructor == profesor.id }
                    ProfesorCard(
                        profesor = profesor,
                        alumnosAsignadosCount = onCountAssignedAlumnos(profesor.id),
                        alumnosAsignados = asignados,
                        onEdit = onProfesorClick,
                        onDelete = onEliminarProfesor
                    )
                }
            }
        }

        // Mostrar el diálogo (sheet) si corresponde
        if (showDialog) {
            DialogAgregarProfesor(
                profesorExistente = profesorAEditar,
                alumnosDisponibles = state.alumnosDisponibles,
                onDismiss = onCerrarDialogo,
                onGuardar = { profesor, alumnoSeleccionado ->
                    // Si es nuevo (id == 0) usamos el flujo que inserta y asigna en una llamada
                    if (profesor.id == 0) {
                        onGuardarConAsignacion(profesor, alumnoSeleccionado)
                    } else {
                        // Si ya existía, actualizamos y si hay alumno seleccionado lo asignamos después
                        onGuardarProfesor(profesor)
                        if (alumnoSeleccionado != null) {
                            onGuardarConAsignacion(profesor, alumnoSeleccionado)
                        }
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
    alumnosAsignados: List<AlumnoEntity> = emptyList(),
    onEdit: (ProfesorEntity) -> Unit,
    onDelete: (ProfesorEntity) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onEdit(profesor) }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = profesor.nombre,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Especialidad: ${profesor.especialidad}",
                    style = MaterialTheme.typography.bodyMedium
                )

                if (alumnosAsignados.isNotEmpty()) {
                    Text(
                        text = "Alumno${if (alumnosAsignados.size > 1) "s" else ""} asignado${if (alumnosAsignados.size > 1) "s" else ""}: " +
                                alumnosAsignados.joinToString { it.nombre },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                } else {
                    Text(
                        text = "Alumnos Asignados: $alumnosAsignadosCount",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Row {
                IconButton(onClick = { onEdit(profesor) }) {
                    Icon(Icons.Default.Edit, contentDescription = "Editar Colega")
                }
                IconButton(onClick = { onDelete(profesor) }) {
                    Icon(Icons.Default.Delete, contentDescription = "Eliminar Colega")
                }
            }
        }
    }
}