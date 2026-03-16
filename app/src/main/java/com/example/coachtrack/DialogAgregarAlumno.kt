package com.example.coachtrack

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.input.KeyboardType

@Composable
private fun colorParaNivel(nivel: String): Color = when (nivel) {
    "Inicial"     -> MaterialTheme.colorScheme.outline
    "Básico"      -> MaterialTheme.colorScheme.primary
    "Intermedio"  -> MaterialTheme.colorScheme.secondary
    "Avanzado"    -> MaterialTheme.colorScheme.tertiary
    "Profesional" -> MaterialTheme.colorScheme.error
    else          -> MaterialTheme.colorScheme.onSurface
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialogAgregarAlumno(
    alumnoExistente: AlumnoEntity? = null,
    onDismiss: () -> Unit,
    onGuardar: (AlumnoEntity) -> Unit
) {
    var nombre by remember { mutableStateOf(alumnoExistente?.nombre ?: "") }
    var nivel by remember { mutableStateOf(alumnoExistente?.nivelActual ?: "Inicial") }
    var objetivo by remember { mutableStateOf(alumnoExistente?.objetivo ?: "") }
    var telefono by remember { mutableStateOf(alumnoExistente?.telefono ?: "") }
    var direccion by remember { mutableStateOf(alumnoExistente?.direccion ?: "") }
    var clasesPactadas by remember { mutableStateOf(alumnoExistente?.clasesPactadas ?: 1) }
    var tarifaPorClase by remember { mutableStateOf(alumnoExistente?.tarifaPorClase?.toString() ?: "") }
    var telefonoError by remember { mutableStateOf(false) }
    var tarifaError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (alumnoExistente == null) "Nuevo Alumno" else "Editar Alumno",
                style = MaterialTheme.typography.titleLarge
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    val telKey = telefono.trim()
                    val telValido = telKey.length >= 8 && telKey.all { it.isDigit() }
                    if (!telValido) {
                        telefonoError = true
                        return@Button
                    }

                    val tarifaInt = tarifaPorClase.trim().toIntOrNull() ?: -1
                    if (tarifaInt < 0) {
                        tarifaError = true
                        return@Button
                    }

                    if (nombre.isNotBlank()) {
                        val alumnoFinal = if (alumnoExistente != null) {
                            alumnoExistente.copy(
                                nombre = nombre,
                                nivelActual = nivel,
                                objetivo = objetivo,
                                telefono = telKey,
                                direccion = direccion.trim(),
                                clasesPactadas = clasesPactadas,
                                tarifaPorClase = tarifaInt
                            )
                        } else {
                            AlumnoEntity(
                                localId = 0L,
                                firebaseId = null,
                                nombre = nombre.trim(),
                                nivelActual = nivel,
                                objetivo = objetivo.trim(),
                                clasesPactadas = clasesPactadas,
                                clasesCursadas = 0,
                                estadoPago = EstadoPago.PENDIENTE.name,
                                edad = 0,
                                telefono = telKey,
                                direccion = direccion.trim(),
                                notasEntrenador = "",
                                profesorInstructor = null,
                                tarifaPorClase = tarifaInt
                            )
                        }
                        onGuardar(alumnoFinal)
                    }
                }
            ) {
                Text(if (alumnoExistente == null) "Guardar" else "Actualizar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                NivelDropdownSelector(
                    nivelActual = nivel,
                    onNivelSeleccionado = { nivel = it }
                )

                OutlinedTextField(
                    value = objetivo,
                    onValueChange = { objetivo = it },
                    label = { Text("Objetivo de entrenamiento") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = telefono,
                    onValueChange = { nuevo ->
                        telefono = nuevo.filter { it.isDigit() }
                        telefonoError = false
                    },
                    label = { Text("Teléfono") },
                    isError = telefonoError,
                    supportingText = {
                        if (telefonoError) Text("Ingrese un número de teléfono válido")
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = direccion,
                    onValueChange = { direccion = it },
                    label = { Text("Dirección o email") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = tarifaPorClase,
                    onValueChange = { nuevo ->
                        tarifaPorClase = nuevo.filter { it.isDigit() }
                        tarifaError = false
                    },
                    label = { Text("Tarifa por clase") },
                    isError = tarifaError,
                    supportingText = {
                        if (tarifaError) Text("Ingrese un valor válido")
                        else Text("Valor en tu moneda local")
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(4.dp))

                Text("Clases pactadas", style = MaterialTheme.typography.titleSmall)

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clickable { if (clasesPactadas > 1) clasesPactadas-- },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Remove,
                            contentDescription = "Restar clase",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Text(
                        text = "$clasesPactadas",
                        style = MaterialTheme.typography.titleLarge,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .width(60.dp)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .padding(vertical = 4.dp)
                    )

                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clickable { clasesPactadas++ },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Agregar clase",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NivelDropdownSelector(
    nivelActual: String,
    onNivelSeleccionado: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val niveles = listOf("Inicial", "Básico", "Intermedio", "Avanzado", "Profesional")
    val colorNivel = colorParaNivel(nivelActual)

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Nivel Actual",
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(bottom = 4.dp)
        )
    }

    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = nivelActual,
            onValueChange = {},
            readOnly = true,
            enabled = false,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true },
            textStyle = LocalTextStyle.current.copy(color = colorNivel),
            colors = OutlinedTextFieldDefaults.colors(
                disabledBorderColor = colorNivel.copy(alpha = 0.6f),
                disabledTextColor = colorNivel,
                disabledContainerColor = MaterialTheme.colorScheme.surface
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
                DropdownMenuItem(
                    text = {
                        Text(
                            nivel,
                            color = colorParaNivel(nivel),
                            modifier = Modifier.fillMaxWidth()
                        )
                    },
                    onClick = {
                        onNivelSeleccionado(nivel)
                        expanded = false
                    }
                )
            }
        }
    }
}