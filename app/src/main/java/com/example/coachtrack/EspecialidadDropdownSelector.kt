package com.example.coachtrack

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EspecialidadDropdownSelector(
    especialidadActual: String,
    onEspecialidadSeleccionada: (String) -> Unit
) {
    // Aquí defines las especialidades disponibles
    val especialidades = listOf(
        "Adulto",
        "Infantil",
        "Competitivo",
        "Recreativo",
        "Condicionamiento físico",
        "Técnica",
        "Saques",
        " iniciación"
    )

    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = especialidadActual,
            onValueChange = {},
            readOnly = true,
            label = { Text("Especialidad del colega") },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            }
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            especialidades.forEach { especialidad ->
                DropdownMenuItem(
                    text = { Text(especialidad) },
                    onClick = {
                        onEspecialidadSeleccionada(especialidad)
                        expanded = false
                    }
                )
            }
        }
    }
}
