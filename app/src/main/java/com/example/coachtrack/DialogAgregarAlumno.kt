package com.example.coachtrack

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialogAgregarAlumno(
    alumnoExistente: AlumnoEntity? = null,
    onDismiss: () -> Unit,
    onGuardar: (AlumnoEntity) -> Unit
) {
    var nombre by remember { mutableStateOf(TextFieldValue(alumnoExistente?.nombre ?: "")) }
    var nivel by remember { mutableStateOf(alumnoExistente?.nivelActual ?: "Inicial") }
    var objetivo by remember { mutableStateOf(TextFieldValue(alumnoExistente?.objetivo ?: "")) }
    var telefono by remember { mutableStateOf(TextFieldValue(alumnoExistente?.telefono ?: "")) }
    var direccion by remember { mutableStateOf(TextFieldValue(alumnoExistente?.direccion ?: "")) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {},
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    if (alumnoExistente != null) "Editar Alumno" else "Nuevo Alumno",
                    style = MaterialTheme.typography.titleMedium
                )

                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre") },
                    modifier = Modifier.fillMaxWidth()
                )

                NivelDropdownSelector(nivel) { nivel = it }

                OutlinedTextField(
                    value = objetivo,
                    onValueChange = { objetivo = it },
                    label = { Text("Objetivo de Entrenamiento") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = telefono,
                    onValueChange = { telefono = it },
                    label = { Text("Teléfono") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = direccion,
                    onValueChange = { direccion = it },
                    label = { Text("Dirección") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) { Text("Cancelar") }
                    Spacer(Modifier.width(8.dp))
                    Button(onClick = {
                        if (nombre.text.isNotBlank()) {
                            val nuevoAlumno = AlumnoEntity(
                                id = alumnoExistente?.id ?: 0,
                                nombre = nombre.text,
                                nivelActual = nivel,
                                objetivo = objetivo.text,
                                telefono = telefono.text,
                                direccion = direccion.text,
                                clasesPactadas = alumnoExistente?.clasesPactadas ?: 0,
                                clasesCursadas = alumnoExistente?.clasesCursadas ?: 0,
                                estadoPago = alumnoExistente?.estadoPago ?: EstadoPago.PENDIENTE,
                                edad = alumnoExistente?.edad ?: 0,
                                notasEntrenador = alumnoExistente?.notasEntrenador ?: ""
                            )
                            onGuardar(nuevoAlumno)
                        }
                    }) {
                        Text(if (alumnoExistente != null) "Actualizar" else "Guardar")
                    }
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NivelDropdownSelector(
    nivelActual: String,
    onNivelSeleccionado: (String) -> Unit
) {
    val niveles = listOf("Inicial", "Básico", "Intermedio", "Avanzado", "Profesional")
    var expanded by remember { mutableStateOf(false) }

    // 🎨 Colores según nivel
    val colorNivel = when (nivelActual) {
        "Inicial" -> Color(0xFF2196F3)        // Azul
        "Básico" -> Color(0xFF64B5F6)         // Azul claro
        "Intermedio" -> Color(0xFFFFA000)     // Amarillo
        "Avanzado" -> Color(0xFF43A047)       // Verde medio
        "Profesional" -> Color(0xFF2E7D32)    // Verde fuerte
        else -> MaterialTheme.colorScheme.primary
    }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = nivelActual,
            onValueChange = {},
            readOnly = true,
            label = { Text("Nivel Actual") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            // ✅ Borde y texto cambian según el color del nivel
            colors = ExposedDropdownMenuDefaults.textFieldColors(
                focusedIndicatorColor = colorNivel,
                unfocusedIndicatorColor = colorNivel.copy(alpha = 0.5f),
                focusedLabelColor = colorNivel,
                cursorColor = colorNivel,
                focusedTextColor = colorNivel
            ),
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            niveles.forEach { nivel ->
                DropdownMenuItem(
                    text = {
                        Text(
                            nivel,
                            color = when (nivel) {
                                "Inicial" -> Color(0xFF2196F3)
                                "Básico" -> Color(0xFF64B5F6)
                                "Intermedio" -> Color(0xFFFFA000)
                                "Avanzado" -> Color(0xFF43A047)
                                "Profesional" -> Color(0xFF2E7D32)
                                else -> MaterialTheme.colorScheme.onSurface
                            }
                        )
                    },
                    onClick = {
                        onNivelSeleccionado(nivel)
                        expanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                )
            }
        }
    }
}
