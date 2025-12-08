package com.example.coachtrack

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.input.TextFieldValue

@Composable
fun DialogEditarPerfilProfesor(
    perfilActual: CoachProfile,
    onDismiss: () -> Unit,
    onGuardar: (String, String) -> Unit
) {
    var nombre by remember { mutableStateOf(TextFieldValue(perfilActual.nombreProfesor)) }
    var academia by remember { mutableStateOf(TextFieldValue(perfilActual.academia)) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Configurar perfil del profesor") },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre del profesor") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Text,
                        capitalization = KeyboardCapitalization.Words
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = academia,
                    onValueChange = { academia = it },
                    label = { Text("Academia / Club (opcional)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Text,
                        capitalization = KeyboardCapitalization.Words
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onGuardar(nombre.text.trim(), academia.text.trim())
                }
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
