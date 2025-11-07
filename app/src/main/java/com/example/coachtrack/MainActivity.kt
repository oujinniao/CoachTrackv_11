package com.example.coachtrack

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.coachtrack.ui.theme.CoachTrackTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CoachTrackTheme {
                AppNavigation() // ✅ Simple y limpio
            }
        }
    }
}