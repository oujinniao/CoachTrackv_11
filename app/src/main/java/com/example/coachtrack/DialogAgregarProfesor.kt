package com.example.coachtrack

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlin.collections.find
import kotlin.collections.isNotEmpty

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialogAgregarProfesor(
    profesorExistente: ProfesorEntity?,
    alumnosDisponibles: List<AlumnoEntity>,
    onDismiss: () -> Unit,
    onGuardar: (ProfesorEntity, Int?) -> Unit
) {

    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    val scrollState = rememberScrollState()

    // Campos del profesor
    var nombre by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(profesorExistente?.nombre ?: ""))
    }
    var especialidad by rememberSaveable { mutableStateOf(profesorExistente?.especialidad ?: "Adulto") }
    var telefono by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(profesorExistente?.telefono ?: ""))
    }
    var email by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(profesorExistente?.email ?: ""))
    }
    var descripcion by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(profesorExistente?.descripcion ?: ""))
    }
    var disponibilidad by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(profesorExistente?.disponibilidad ?: ""))
    }

    // Estados para los dropdowns - ¡AMBOS!
    var alumnoSeleccionado by rememberSaveable { mutableStateOf<Int?>(null) }
    var expandedAlumno by remember { mutableStateOf(false) }

    var expandedEspecialidad by remember { mutableStateOf(false) }
    val especialidades = listOf("Adulto", "Infantil", "Avanzado", "Principiante", "Rehabilitación")

    // Inicializar alumno seleccionado
    LaunchedEffect(profesorExistente, alumnosDisponibles) {
        if (profesorExistente != null) {
            val alumnoAsignado = alumnosDisponibles.find { it.profesorInstructor == profesorExistente.id }
            alumnoSeleccionado = alumnoAsignado?.id
        }
    }

    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = {
            scope.launch { sheetState.hide() }
            onDismiss()
        },
        modifier = Modifier.fillMaxHeight(0.9f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState)
        ) {

            // HEADER
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        scope.launch { sheetState.hide() }
                        onDismiss()
                    },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(Icons.Default.ArrowBack, "Cerrar")
                }

                Text(
                    if (profesorExistente == null) "Nuevo Colega" else "Editar Colega",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.weight(1f)
                )
            }

            // FORMULARIO
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 16.dp)
            ) {

                // SECCIÓN 1: Información básica
                Text(
                    "Información Básica",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Nombre
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre completo") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                // 🔥 DROPDOWN DE ESPECIALIDAD - ¡RECUPERADO!
                ExposedDropdownMenuBox(
                    expanded = expandedEspecialidad,
                    onExpandedChange = { expandedEspecialidad = it }
                ) {
                    OutlinedTextField(
                        value = especialidad,
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable),
                        label = { Text("Especialidad") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedEspecialidad)
                        },
                        singleLine = true
                    )

                    ExposedDropdownMenu(
                        expanded = expandedEspecialidad,
                        onDismissRequest = { expandedEspecialidad = false }
                    ) {
                        especialidades.forEach { especialidadItem ->
                            DropdownMenuItem(
                                text = { Text(especialidadItem) },
                                onClick = {
                                    especialidad = especialidadItem
                                    expandedEspecialidad = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Teléfono y Email en fila
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = telefono,
                        onValueChange = { telefono = it },
                        label = { Text("Teléfono") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // SECCIÓN 2: Información adicional
                Text(
                    "Información Adicional",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Descripción
                OutlinedTextField(
                    value = descripcion,
                    onValueChange = { descripcion = it },
                    label = { Text("Descripción breve") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = false,
                    maxLines = 2,
                    placeholder = { Text("Ej: Especialista en...") }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Disponibilidad
                OutlinedTextField(
                    value = disponibilidad,
                    onValueChange = { disponibilidad = it },
                    label = { Text("Disponibilidad") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    placeholder = { Text("Ej: Lunes a Viernes 8:00-18:00") }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 🔥 DROPDOWN DE ALUMNO - ¡MANTENIDO!
                if (profesorExistente != null) {
                    Text(
                        "Asignación de Alumno",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    ExposedDropdownMenuBox(
                        expanded = expandedAlumno,
                        onExpandedChange = {
                            if (alumnosDisponibles.isNotEmpty()) {
                                expandedAlumno = it
                            }
                        }
                    ) {
                        OutlinedTextField(
                            value = alumnosDisponibles.find { it.id == alumnoSeleccionado }?.nombre
                                ?: "Sin asignar",
                            onValueChange = {},
                            readOnly = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(MenuAnchorType.PrimaryNotEditable),
                            label = { Text("Alumno asignado") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedAlumno)
                            },
                            enabled = alumnosDisponibles.isNotEmpty(),
                            singleLine = true
                        )

                        ExposedDropdownMenu(
                            expanded = expandedAlumno,
                            onDismissRequest = { expandedAlumno = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Sin asignar") },
                                onClick = {
                                    alumnoSeleccionado = null
                                    expandedAlumno = false
                                }
                            )
                            alumnosDisponibles.forEach { alumno ->
                                DropdownMenuItem(
                                    text = { Text(alumno.nombre) },
                                    onClick = {
                                        alumnoSeleccionado = alumno.id
                                        expandedAlumno = false
                                    }
                                )
                            }
                        }
                    }

                    // Mensaje informativo
                    if (alumnosDisponibles.isEmpty()) {
                        Text(
                            "No hay alumnos disponibles para asignar",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    } else {
                        Text(
                            "${alumnosDisponibles.size} alumnos disponibles - Toca para seleccionar",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // BOTONES
            Surface(
                tonalElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = {
                            scope.launch { sheetState.hide() }
                            onDismiss()
                        }
                    ) {
                        Text("Cancelar")
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            val profesorFinal = ProfesorEntity(
                                id = profesorExistente?.id ?: 0,
                                nombre = nombre.text,
                                especialidad = especialidad,
                                telefono = telefono.text,
                                email = email.text,
                                descripcion = descripcion.text,
                                disponibilidad = disponibilidad.text
                            )
                            onGuardar(profesorFinal, alumnoSeleccionado)
                            scope.launch { sheetState.hide() }
                        }
                    ) {
                        Text(if (profesorExistente == null) "Guardar" else "Actualizar")
                    }
                }
            }
        }
    }
}