package com.example.coachtrack

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.coachtrack.AppNavigation
import com.example.coachtrack.ui.theme.CoachTrackTheme  // ✅ Import de tu theme personalizado

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CoachTrackApp()
        }
    }
}

@Composable
fun CoachTrackApp() {
    CoachTrackTheme {  // ✅ CAMBIADO: Usa tu theme personalizado en lugar de MaterialTheme
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background  // ✅ Referencia correcta
        ) {
            AppNavigation()  // Tu punto de entrada
        }
    }
}