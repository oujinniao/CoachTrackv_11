package com.example.coachtrack

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.compose.rememberAsyncImagePainter



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlantillaDetailScreen(
    plantilla: Plantilla,
    onAdd: () -> Unit,
    onVolver: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(plantilla.nombre) },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { pv ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(pv)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            println("PlantillaDetailScreen URL = ${plantilla.imageUrl}")
            val painter = rememberAsyncImagePainter(
                model = plantilla.imageUrl,
                onState = { state ->
                    when (state) {
                        is AsyncImagePainter.State.Loading -> println("🟢 Coil cargando...")
                        is AsyncImagePainter.State.Success -> println("✅ Coil éxito")
                        is AsyncImagePainter.State.Error   -> println("❌ Coil error: ${state.result.throwable}")
                        else -> Unit
                    }
                }
            )

            Image(
                painter = painter,
                contentDescription = plantilla.nombre,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(Modifier.height(16.dp))
            Text(plantilla.descripcion, style = MaterialTheme.typography.bodyMedium)

            Spacer(Modifier.height(24.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(onClick = onVolver) { Text("Cancelar") }
                Button(onClick = onAdd) { Text("Añadir a sesión") }
            }
        }
    }
}