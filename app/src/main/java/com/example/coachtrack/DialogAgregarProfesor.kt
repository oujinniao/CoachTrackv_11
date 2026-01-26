package com.example.coachtrack

import com.example.coachtrack.AlumnoEntity
import com.example.coachtrack.ProfesorEntity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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


@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun DialogAgregarProfesor(
    profesorExistente: ProfesorEntity?,
    alumnosDisponibles: List<AlumnoEntity>,
    onDismiss: () -> Unit,
    // ID del alumno seleccionado es Long? (ID local/Room)
    onGuardar: (ProfesorEntity, Long?) -> Unit
) {

    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
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
    var emailError by remember { mutableStateOf(false) }
    val emailKey = email.text.trim().lowercase()

    // Estados para los dropdowns
    var alumnoSeleccionadoIdLocal by rememberSaveable { mutableStateOf<Long?>(null) }
    var expandedAlumno by remember { mutableStateOf(false) }

    var expandedEspecialidad by remember { mutableStateOf(false) }
    val especialidades = listOf("Adulto", "Infantil", "Avanzado", "Principiante", "Rehabilitación")

    // Inicializar alumno seleccionado o precarga el alumno que ya está asignado a un profesor
    LaunchedEffect(profesorExistente, alumnosDisponibles) {
        if (profesorExistente != null) {
            val alumnoAsignado = alumnosDisponibles.find { it.profesorInstructor == profesorExistente.id }

            // 🔑 CORRECCIÓN: Asignar el ID local (Long?) al estado (Long?)
            alumnoSeleccionadoIdLocal = alumnoAsignado?.localId
        }
    }

    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = {
            scope.launch { sheetState.hide() }
            onDismiss()
        },
        modifier = Modifier.fillMaxHeight()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // HEADER
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Volver")
                }
                Text(
                    text = if (profesorExistente == null) "Nuevo Profesor" else "Editar Profesor",
                    style = MaterialTheme.typography.headlineSmall
                )
                Spacer(modifier = Modifier.width(48.dp))
            }

            // Sección de Campos de Texto
            Text("Detalles del Profesor", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = telefono, onValueChange = { telefono = it }, label = { Text("Teléfono") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = email,
                onValueChange = { email = it; emailError = false },
                label = { Text("Email") },
                isError = emailError,
                supportingText = { if (emailError) Text("Email es obligatorio") else null },
                modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(8.dp))

            // DROPDOWN ESPECIALIDAD
            ExposedDropdownMenuBox(
                expanded = expandedEspecialidad,
                onExpandedChange = { expandedEspecialidad = it },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = especialidad,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Especialidad") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedEspecialidad) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expandedEspecialidad,
                    onDismissRequest = { expandedEspecialidad = false }
                ) {
                    especialidades.forEach { item ->
                        DropdownMenuItem(
                            text = { Text(item) },
                            onClick = {
                                especialidad = item
                                expandedEspecialidad = false
                            }
                        )
                    }
                }
            }
            Spacer(Modifier.height(8.dp))

            // Descripción y Disponibilidad
            OutlinedTextField(value = descripcion, onValueChange = { descripcion = it }, label = { Text("Descripción") }, modifier = Modifier.fillMaxWidth(), minLines = 2)
            OutlinedTextField(value = disponibilidad, onValueChange = { disponibilidad = it }, label = { Text("Disponibilidad") }, modifier = Modifier.fillMaxWidth(), minLines = 2)
            Spacer(Modifier.height(16.dp))


            // Sección de Asignación de Alumno
            if (profesorExistente != null) {
                Text("Asignación de Alumno (Opcional)", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))

                // DROPDOWN DE ALUMNO
                ExposedDropdownMenuBox(
                    expanded = expandedAlumno,
                    onExpandedChange = {
                        if (alumnosDisponibles.isNotEmpty()) {
                            expandedAlumno = it
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = alumnosDisponibles.find { it.localId == alumnoSeleccionadoIdLocal }?.nombre
                            ?: "Sin asignar",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Alumno Asignado") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedAlumno) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )

                    ExposedDropdownMenu(
                        expanded = expandedAlumno,
                        onDismissRequest = { expandedAlumno = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Sin asignar") },
                            onClick = {
                                alumnoSeleccionadoIdLocal = null
                                expandedAlumno = false
                            }
                        )
                        alumnosDisponibles.forEach { alumno ->
                            DropdownMenuItem(
                                text = { Text(alumno.nombre) },
                                onClick = {
                                    alumnoSeleccionadoIdLocal = alumno.localId
                                    expandedAlumno = false
                                }
                            )
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))
            }
            // ----------------------------------------------------------------------


            // BOTÓN DE GUARDAR
            Button(
                onClick = {
                    val profesorFinal = ProfesorEntity(
                        id = profesorExistente?.id ?: 0,
                        // ✅ CLAVE: Mantenemos el firebaseId existente al editar (o null si es nuevo)
                        firebaseId = profesorExistente?.firebaseId,
                        nombre = nombre.text.trim(),
                        especialidad = especialidad,
                        telefono = telefono.text.trim(),
                        email = emailKey,
                        descripcion = descripcion.text,
                        disponibilidad = disponibilidad.text
                    )
                    // Llamamos a la función de guardar con el profesor y el ID local del alumno
                    onGuardar(profesorFinal, alumnoSeleccionadoIdLocal)
                    scope.launch { sheetState.hide() }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (profesorExistente == null) "Guardar Profesor" else "Actualizar Profesor")
            }

            Spacer(Modifier.height(60.dp))
        }
    }
}