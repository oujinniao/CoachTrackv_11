package com.example.coachtrack

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.runtime.saveable.rememberSaveable
import kotlin.collections.find
import androidx.compose.foundation.layout.imePadding




@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialogAgregarProfesor(
    profesorExistente: ProfesorEntity? = null,
    alumnosDisponibles: List<AlumnoEntity>,
    onDismiss: () -> Unit,
    // ahora devuelve también el alumno seleccionado (o null)
    onGuardar: (ProfesorEntity, Int?) -> Unit
) {
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)

    // Campos (ejemplo con rememberSaveable)
    var nombre by rememberSaveable(stateSaver = TextFieldValue.Saver) { mutableStateOf(TextFieldValue(profesorExistente?.nombre ?: "")) }
    var especialidad by rememberSaveable { mutableStateOf(profesorExistente?.especialidad ?: "Adulto") }
    var telefono by rememberSaveable(stateSaver = TextFieldValue.Saver) { mutableStateOf(TextFieldValue(profesorExistente?.telefono ?: "")) }
    var email by rememberSaveable(stateSaver = TextFieldValue.Saver) { mutableStateOf(TextFieldValue(profesorExistente?.email ?: "")) }
    var descripcion by rememberSaveable(stateSaver = TextFieldValue.Saver) { mutableStateOf(TextFieldValue(profesorExistente?.descripcion ?: "")) }
    var disponibilidad by rememberSaveable(stateSaver = TextFieldValue.Saver) { mutableStateOf(TextFieldValue(profesorExistente?.disponibilidad ?: "")) }

    // Alumno seleccionado (puede seleccionarse también en creación)
    val initialAlumnoId = profesorExistente?.let { prof ->
        alumnosDisponibles.find { it.profesorInstructor == prof.id }?.id
    }
    Log.d("DialogAgregarProfesor", "opening dialog for prof=${profesorExistente?.id} initialAlumnoId=$initialAlumnoId alumnosCount=${alumnosDisponibles.size}")

    var alumnoSeleccionadoId by rememberSaveable { mutableStateOf<Int?>(initialAlumnoId) }
    var expandedAl by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = {
            scope.launch { sheetState.hide() }
            onDismiss()
        },
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .imePadding()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    scope.launch { sheetState.hide() }
                    onDismiss()
                }) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Cerrar")
                }
                Text(
                    text = if (profesorExistente == null) "Nuevo Colega" else "Editar Colega",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.weight(1f).padding(start = 8.dp)
                )
            }

            // Campo scrollable (ocupa el espacio disponible)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth())

                EspecialidadDropdownSelector(
                    especialidadActual = especialidad,
                    onEspecialidadSeleccionada = { especialidad = it }
                )

                OutlinedTextField(value = telefono, onValueChange = { telefono = it }, label = { Text("Teléfono") }, modifier = Modifier.fillMaxWidth())

                OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth())

                OutlinedTextField(value = descripcion, onValueChange = { descripcion = it }, label = { Text("Descripción / Notas") }, modifier = Modifier.fillMaxWidth().heightIn(min = 80.dp))

                OutlinedTextField(value = disponibilidad, onValueChange = { disponibilidad = it }, label = { Text("Disponibilidad") }, modifier = Modifier.fillMaxWidth().heightIn(min = 80.dp))

                // Selector de Alumno Asignado (mostramos siempre para permitir asignar al crear)
                OutlinedTextField(
                    value = alumnosDisponibles.find { it.id == alumnoSeleccionadoId }?.nombre ?: "Sin asignar",
                    onValueChange = {},
                    readOnly = true,
                    enabled = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { expandedAl = true },
                    label = { Text("Alumno Asignado") },
                    trailingIcon = { Icon(Icons.Filled.ArrowDropDown, contentDescription = "Seleccionar alumno") }
                )

                DropdownMenu(expanded = expandedAl, onDismissRequest = { expandedAl = false }) {
                    DropdownMenuItem(text = { Text("Sin asignar") }, onClick = {
                        alumnoSeleccionadoId = null
                        expandedAl = false
                    })
                    alumnosDisponibles.forEach { alumno ->
                        DropdownMenuItem(text = { Text(alumno.nombre) }, onClick = {
                            alumnoSeleccionadoId = alumno.id
                            expandedAl = false
                        })
                    }
                }
            }

            // Row de acciones FIJAS (siempre visibles)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = {
                    scope.launch { sheetState.hide() }
                    onDismiss()
                }) {
                    Text("Cancelar")
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(onClick = {
                    if (nombre.text.isNotBlank()) {
                        val profesorFinal = ProfesorEntity(
                            id = profesorExistente?.id ?: 0,
                            nombre = nombre.text,
                            especialidad = especialidad,
                            telefono = telefono.text,
                            email = email.text,
                            descripcion = descripcion.text,
                            disponibilidad = disponibilidad.text
                        )
                        // devolvemos el profesor y el alumno seleccionado (o null)
                        onGuardar(profesorFinal, alumnoSeleccionadoId)
                        scope.launch { sheetState.hide() }
                        onDismiss()
                    }
                }) {
                    Text(if (profesorExistente == null) "Guardar" else "Actualizar")
                }
            }
        }
    }
}
@Composable
private fun EspecialidadDropdownSelector(
    especialidadActual: String,
    onEspecialidadSeleccionada: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val especialidades = listOf("Infantil", "Adulto", "Profesional", "Tenis", "Padel")

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Especialidad",
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(bottom = 4.dp)
        )
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 60.dp)
            .clickable { expanded = true }
    ) {
        OutlinedTextField(
            value = especialidadActual,
            onValueChange = {},
            readOnly = true,
            enabled = true,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                disabledBorderColor = MaterialTheme.colorScheme.outline,
                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                disabledContainerColor = Color.Transparent
            ),
            trailingIcon = {
                Icon(Icons.Filled.ArrowDropDown, contentDescription = "Desplegar especialidades")
            }
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth(0.9f)
        ) {
            especialidades.forEach { esp ->
                DropdownMenuItem(
                    text = { Text(esp, modifier = Modifier.fillMaxWidth()) },
                    onClick = {
                        onEspecialidadSeleccionada(esp)
                        expanded = false
                    }
                )
            }
        }
    }
}