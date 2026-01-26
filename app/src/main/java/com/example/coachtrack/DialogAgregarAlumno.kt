package com.example.coachtrack

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
    var clasesPactadas by remember { mutableStateOf(alumnoExistente?.clasesPactadas ?: 1) }

    var telefonoError by remember { mutableStateOf(false) }
    //val telKey = telefono.text.trim()

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

                    val telKey = telefono.text.trim()

                    val telValido = telKey.length >= 8 && telKey.all { it.isDigit() }
                    if (!telValido) {
                        telefonoError = true
                        return@Button
                    }


                    if (nombre.text.isNotBlank()) {
                        val alumnoFinal = if (alumnoExistente != null) {
                            // Edición: Preservamos todos los campos de persistencia (localId, firebaseId, FKs)
                            alumnoExistente.copy(
                                nombre = nombre.text,
                                nivelActual = nivel,
                                objetivo = objetivo.text,
                                telefono = telKey,
                                direccion = direccion.text.trim(),
                                clasesPactadas = clasesPactadas
                            )
                        } else {
                            // ✅ Creación: Inicializamos la Entidad con IDs de Room y Firebase nulos
                            AlumnoEntity(
                                localId = 0L, // Para que Room Autogenerate
                                firebaseId = null, // Se asignará en el Repositorio
                                nombre = nombre.text.trim(),
                                nivelActual = nivel,
                                objetivo = objetivo.text.trim(),
                                clasesPactadas = clasesPactadas,
                                clasesCursadas = 0,
                                estadoPago = EstadoPago.PENDIENTE.name,
                                edad = 0,
                                telefono = telKey,
                                direccion = direccion.text.trim(),
                                notasEntrenador = "",
                                profesorInstructor = null // FK local inicial
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
                    .padding(horizontal = 8.dp, vertical = 0.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // 🧍 Nombre
                OutlinedTextField(
                    value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre") },
                    modifier = Modifier.fillMaxWidth().heightIn(max = 60.dp)
                )
                // 🎾 Nivel actual
                NivelDropdownSelector(nivelActual = nivel, onNivelSeleccionado = { nivel = it })
                // 🎯 Objetivo
                OutlinedTextField(
                    value = objetivo, onValueChange = { objetivo = it }, label = { Text("Objetivo de entrenamiento") },
                    modifier = Modifier.fillMaxWidth().heightIn(max = 60.dp)
                )
                // ☎️ Teléfono
                OutlinedTextField(
                    value = telefono,
                    onValueChange = { nuevo ->
                        // (Opcional) filtra solo dígitos, para que no entren letras ni espacios
                        val filtrado = nuevo.text.filter { it.isDigit() }
                        telefono = nuevo.copy(text = filtrado)
                        telefonoError = false
                    },
                    label = { Text("Teléfono") },
                    isError = telefonoError,
                    supportingText = {
                        if (telefonoError) Text("Ingrese un número de teléfono válido")
                    },
                    singleLine = true,
                    maxLines = 1,
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                        keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                // 📍 Dirección
                OutlinedTextField(
                    value = direccion, onValueChange = { direccion = it }, label = { Text("Dirección o email") },
                    modifier = Modifier.fillMaxWidth().heightIn(max = 60.dp)
                )


                Spacer(Modifier.height(4.dp))
                Text("Clases pactadas", style = MaterialTheme.typography.titleSmall)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 0.dp)
                ) {
                    // Restar
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clickable { if (clasesPactadas > 1) clasesPactadas-- }
                            .align(Alignment.CenterVertically),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Remove, contentDescription = "Restar clase", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
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

                    // Sumar
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clickable { clasesPactadas++ }
                            .align(Alignment.CenterVertically),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Agregar clase", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
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
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = nivelActual,
            onValueChange = {},
            readOnly = true,
            enabled = false,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 60.dp)
                .clickable { expanded = true },
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