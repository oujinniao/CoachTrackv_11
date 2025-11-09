package com.example.coachtrack

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
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
    var clasesPactadas by remember { mutableStateOf(alumnoExistente?.clasesPactadas ?: 4) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {},
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = if (alumnoExistente == null) "Nuevo Alumno" else "Editar Alumno",
                    style = MaterialTheme.typography.titleMedium
                )

                // 🧍 Nombre
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre") },
                    modifier = Modifier.fillMaxWidth()
                )

                // 🎾 Nivel actual con color dinámico
                NivelDropdownSelector(
                    nivelActual = nivel,
                    onNivelSeleccionado = { nivel = it }
                )

                // 🎯 Objetivo
                OutlinedTextField(
                    value = objetivo,
                    onValueChange = { objetivo = it },
                    label = { Text("Objetivo de entrenamiento") },
                    modifier = Modifier.fillMaxWidth()
                )

                // ☎️ Teléfono
                OutlinedTextField(
                    value = telefono,
                    onValueChange = { telefono = it },
                    label = { Text("Teléfono") },
                    modifier = Modifier.fillMaxWidth()
                )

                // 📍 Dirección
                OutlinedTextField(
                    value = direccion,
                    onValueChange = { direccion = it },
                    label = { Text("Dirección") },
                    modifier = Modifier.fillMaxWidth()
                )

                // 🧮 Clases pactadas
                Spacer(Modifier.height(12.dp))
                Text("Clases pactadas", style = MaterialTheme.typography.titleSmall)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                ) {
                    IconButton(onClick = { if (clasesPactadas > 1) clasesPactadas-- }) {
                        Icon(Icons.Default.Remove, contentDescription = "Restar clase")
                    }
                    Text(
                        text = "$clasesPactadas",
                        style = MaterialTheme.typography.titleLarge,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .width(60.dp)
                            .background(Color(0xFFF5F5F5))
                            .padding(vertical = 4.dp)
                    )
                    IconButton(onClick = { clasesPactadas++ }) {
                        Icon(Icons.Default.Add, contentDescription = "Agregar clase")
                    }
                }

                Spacer(Modifier.height(8.dp))

                // 🔘 Botones inferiores
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) { Text("Cancelar") }
                    Spacer(Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (nombre.text.isNotBlank()) {
                                val alumnoFinal = if (alumnoExistente != null) {
                                    alumnoExistente.copy(
                                        nombre = nombre.text,
                                        nivelActual = nivel,
                                        objetivo = objetivo.text,
                                        telefono = telefono.text,
                                        direccion = direccion.text,
                                        clasesPactadas = clasesPactadas
                                    )
                                } else {
                                    AlumnoEntity(
                                        id = 0,
                                        nombre = nombre.text,
                                        nivelActual = nivel,
                                        objetivo = objetivo.text,
                                        clasesPactadas = clasesPactadas,
                                        clasesCursadas = 0,
                                        estadoPago = EstadoPago.PENDIENTE,
                                        edad = 0,
                                        telefono = telefono.text,
                                        direccion = direccion.text,
                                        notasEntrenador = ""
                                    )
                                }
                                onGuardar(alumnoFinal)
                            }
                        }
                    ) {
                        Text(if (alumnoExistente == null) "Guardar" else "Actualizar")
                    }
                }
            }
        }
    )
}

@Composable
private fun NivelDropdownSelector(
    nivelActual: String,
    onNivelSeleccionado: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val niveles = listOf("Inicial", "Básico", "Intermedio", "Avanzado", "Profesional")

    // 🎨 Color según el nivel actual
    val colorNivel = when (nivelActual) {
        "Inicial" -> Color.Gray
        "Básico" -> Color(0xFF64B5F6)
        "Intermedio" -> Color(0xFF4CAF50)
        "Avanzado" -> Color(0xFFFFA000)
        "Profesional" -> Color(0xFFD32F2F)
        else -> Color.Black
    }
    Column (
        modifier = Modifier.fillMaxWidth()){
        Text(text = "Nivel Actual",
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(bottom = 4.dp))
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = true }
    ) {
        OutlinedTextField(
            value = nivelActual,
            onValueChange = {},
            readOnly = true,
            enabled = false, // 👈 Esto es importante
            modifier = Modifier.fillMaxWidth(),
            textStyle = LocalTextStyle.current.copy(color = colorNivel),
            colors = OutlinedTextFieldDefaults.colors(
                disabledBorderColor = colorNivel.copy(alpha = 0.6f),
                disabledTextColor = colorNivel,
                disabledContainerColor = Color.Transparent
            ),
            trailingIcon = {
                Icon(
                    Icons.Filled.ArrowDropDown,
                    contentDescription = "Desplegar niveles",
                    tint = colorNivel
                )
            }
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth(0.9f)
        ) {
            niveles.forEach { nivel ->
                val nivelColor = when (nivel) {
                    "Inicial" -> Color.Gray
                    "Básico" -> Color(0xFF64B5F6)
                    "Intermedio" -> Color(0xFF4CAF50)
                    "Avanzado" -> Color(0xFFFFA000)
                    "Profesional" -> Color(0xFFD32F2F)
                    else -> Color.Black
                }

                DropdownMenuItem(
                    text = {
                        Text(
                            nivel,
                            color = nivelColor,
                            modifier = Modifier.fillMaxWidth()
                        ) },
                    onClick = {
                        onNivelSeleccionado(nivel)
                        expanded = false
                    }
                )
            }
        }
    }
}
