package com.example.coachtrack

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.input.TextFieldValue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialogAgregarAlumno(
    onDismiss: () -> Unit,
    onGuardar: (AlumnoEntity) -> Unit
) {
    var nombre by remember { mutableStateOf(TextFieldValue("")) }
    var nivel by remember { mutableStateOf("Inicial") }
    var objetivo by remember { mutableStateOf(TextFieldValue("")) }
    var telefono by remember { mutableStateOf(TextFieldValue("")) }
    var direccion by remember { mutableStateOf(TextFieldValue("")) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {},
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Nuevo Alumno", style = MaterialTheme.typography.titleMedium)

                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Selector simple de nivel actual
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
                    TextButton(onClick = onDismiss) {
                        Text("Cancelar")
                    }
                    Spacer(Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (nombre.text.isNotBlank()) {
                                val nuevoAlumno = AlumnoEntity(
                                    nombre = nombre.text,
                                    nivelActual = nivel,
                                    objetivo = objetivo.text,
                                    telefono = telefono.text,
                                    direccion = direccion.text,
                                    clasesPactadas = 0,
                                    clasesCursadas = 0,
                                    estadoPago = EstadoPago.PENDIENTE,
                                    edad = 0,
                                    notasEntrenador = ""
                                )
                                onGuardar(nuevoAlumno)
                            }
                        }
                    ) {
                        Text("Guardar")
                    }
                }
            }
        }
    )
}

@Composable
fun NivelDropdownSelector(nivelActual: String, onNivelSeleccionado: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val niveles = listOf("Inicial", "Intermedio", "Avanzado", "Competitivo")

    Box {
        OutlinedTextField(
            value = nivelActual,
            onValueChange = {},
            readOnly = true,
            label = { Text("Nivel Actual") },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true }
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            niveles.forEach { nivel ->
                DropdownMenuItem(
                    text = { Text(nivel) },
                    onClick = {
                        onNivelSeleccionado(nivel)
                        expanded = false
                    }
                )
            }
        }
    }
}
