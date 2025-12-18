package com.example.coachtrack

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier

import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun DialogNuevaSesion(
    alumnoId: Long,
    alumnoNombre: String,
    sesionViewModel: SesionViewModel,
    onDismiss: () -> Unit
) {
    var fecha by rememberSaveable { mutableStateOf(LocalDate.now().format(DateTimeFormatter.ISO_DATE)) }
    var duracion by rememberSaveable { mutableStateOf("60") }
    var notas by rememberSaveable { mutableStateOf("") }
    var ejercicios by rememberSaveable { mutableStateOf("") }

    val duracionInt = duracion.toIntOrNull() ?: 0

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Registrar Sesión para $alumnoNombre") },
        text = {
            Column(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = fecha,
                    onValueChange = { fecha = it },
                    label = { Text("Fecha (YYYY-MM-DD)") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = duracion,
                    onValueChange = { duracion = it.filter { char -> char.isDigit() } },
                    label = { Text("Duración (minutos)") },
                    // -> REFERENCIA SIMPLIFICADA
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = ejercicios,
                    onValueChange = { ejercicios = it },
                    label = { Text("Ejercicios Realizados") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                )

                OutlinedTextField(
                    value = notas,
                    onValueChange = { notas = it },
                    label = { Text("Notas de la Sesión") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (duracionInt > 0 && alumnoId != 0L) {
                        // Creamos la SesionEntity con los campos correctos (duracion, ejercicios)
                        val nuevaSesion = SesionEntity(
                            alumnoId = alumnoId,
                            alumnoNombre = alumnoNombre,
                            fecha = fecha,
                            duracion = duracionInt,
                            ejercicios = ejercicios,
                            notas = notas,
                            completada = true
                        )
                        sesionViewModel.guardarNuevaSesion(nuevaSesion)
                        onDismiss()
                    }
                },
                enabled = duracionInt > 0
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