package com.example.coachtrack

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DialogNuevaTactica(
    alumnoNombre: String,
    tacticaViewModel: TacticaViewModel,
    onDismiss: () -> Unit // Función para cerrar el diálogo
) {
    // 1. Estados de la UI
    var titulo by rememberSaveable { mutableStateOf("") }
    var descripcion by rememberSaveable { mutableStateOf("") }
    var nivel by rememberSaveable { mutableStateOf("Básico") } // Estado para el Nivel

    val nivelesDisponibles = listOf("Básico", "Intermedio", "Avanzado", "Élite")

    // Validación básica
    val isReadyToSave = titulo.isNotBlank() && descripcion.isNotBlank()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Registrar Nueva Táctica para $alumnoNombre") },
        text = {
            Column(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = titulo,
                    onValueChange = { titulo = it },
                    label = { Text("Título de la Táctica") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = descripcion,
                    onValueChange = { descripcion = it },
                    label = { Text("Descripción / Detalle de la Táctica") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                )

                // Dropdown para seleccionar el Nivel
                NivelDropdown(
                    selectedNivel = nivel,
                    onNivelSelected = { nivel = it },
                    niveles = nivelesDisponibles
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (isReadyToSave) {
                        // 💡 Llamada al ViewModel usando la función que creaste
                        tacticaViewModel.guardarTactica(
                            titulo = titulo,
                            descripcion = descripcion,
                            nivel = nivel
                        )
                        onDismiss()
                    }
                },
                enabled = isReadyToSave
            ) {
                Icon(Icons.Default.Save, contentDescription = "Guardar")
                Spacer(Modifier.width(4.dp))
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Icon(Icons.Default.Close, contentDescription = "Cancelar")
                Spacer(Modifier.width(4.dp))
                Text("Cancelar")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NivelDropdown(
    selectedNivel: String,
    onNivelSelected: (String) -> Unit,
    niveles: List<String>
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedNivel,
            onValueChange = {},
            readOnly = true,
            label = { Text("Nivel de Táctica") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            niveles.forEach { nivel ->
                DropdownMenuItem(
                    text = { Text(nivel) },
                    onClick = {
                        onNivelSelected(nivel)
                        expanded = false
                    }
                )
            }
        }
    }
}