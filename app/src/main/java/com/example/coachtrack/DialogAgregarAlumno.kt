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
import com.example.coachtrack.NivelDropdownSelector

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

    AlertDialog(
        onDismissRequest = onDismiss,

        title = {
            Text(
                text = if (alumnoExistente == null) "Nuevo Alumno" else "Editar Alumno",
                style = MaterialTheme.typography.titleLarge
            )
        },

        // BOTONES DE ACCIÓN (fijos en el pie del diálogo)
        confirmButton = {
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
                                estadoPago = EstadoPago.PENDIENTE.name,
                                edad = 0,
                                telefono = telefono.text,
                                direccion = direccion.text,
                                notasEntrenador = "",
                                profesorInstructor = null
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

        // CONTENIDO DEL FORMULARIO (Desplazable)
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 8.dp, vertical = 0.dp),
                // 🆕 Espaciado vertical reducido a 6.dp
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // 🧍 Nombre
                OutlinedTextField(
                    value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre") },
                    // 🆕 Altura máxima reducida
                    modifier = Modifier.fillMaxWidth().heightIn(max = 60.dp)
                )
                // 🎾 Nivel actual
                NivelDropdownSelector(nivelActual = nivel, onNivelSeleccionado = { nivel = it })
                // 🎯 Objetivo
                OutlinedTextField(
                    value = objetivo, onValueChange = { objetivo = it }, label = { Text("Objetivo de entrenamiento") },
                    // 🆕 Altura máxima reducida
                    modifier = Modifier.fillMaxWidth().heightIn(max = 60.dp)
                )
                // ☎️ Teléfono
                OutlinedTextField(
                    value = telefono, onValueChange = { telefono = it }, label = { Text("Teléfono") },
                    // 🆕 Altura máxima reducida
                    modifier = Modifier.fillMaxWidth().heightIn(max = 60.dp)
                )
                // 📍 Dirección
                OutlinedTextField(
                    value = direccion, onValueChange = { direccion = it }, label = { Text("Dirección o email") },
                    // 🆕 Altura máxima reducida
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
                    // ⭐ Solución Final: Box con Clickable (Restar)
                    Box(
                        modifier = Modifier
                            .size(36.dp) // Área de toque más pequeña
                            .clickable { if (clasesPactadas > 1) clasesPactadas-- }
                            .align(Alignment.CenterVertically),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Remove,
                            contentDescription = "Restar clase",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp) // Tamaño del icono
                        )
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

                    // ⭐ Solución Final: Box con Clickable (Sumar)
                    Box(
                        modifier = Modifier
                            .size(36.dp) // Área de toque más pequeña
                            .clickable { clasesPactadas++ }
                            .align(Alignment.CenterVertically),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Agregar clase",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp) // Tamaño del icono
                        )
                    }
                }
            }
        }
    )
}

// Bloque NivelDropdownSelector también optimizado
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
                .heightIn(max = 60.dp) // 🆕 Altura máxima reducida aquí también
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