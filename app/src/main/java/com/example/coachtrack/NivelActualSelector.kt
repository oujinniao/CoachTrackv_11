package com.example.coachtrack

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.*



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NivelActualSelector(
    nivelActual: String,
    onNivelChange: (String) -> Unit
) {
    val niveles = listOf("Básico", "Intermedio", "Avanzado", "Competitivo", "Profesional")
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = nivelActual,
            onValueChange = {},
            readOnly = true,
            label = { Text("Nivel actual") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
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
                    text = { Text(nivel) },
                    onClick = {
                        onNivelChange(nivel)
                        expanded = false
                    }
                )
            }
        }
    }
}
