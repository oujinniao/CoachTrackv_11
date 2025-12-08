package com.example.coachtrack

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PerfilProfesorViewModel(application: Application) : AndroidViewModel(application) {

    private val dataStore = CoachProfileDataStore(application)

    // Exponemos el perfil como StateFlow para Compose
    val perfil: StateFlow<CoachProfile> = dataStore.perfilFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = CoachProfile()
        )

    fun guardarPerfil(nombre: String, academia: String) {
        val uid = Firebase.auth.currentUser?.uid ?: ""  // por si quieres mostrarlo en la cabecera

        viewModelScope.launch {
            dataStore.guardarPerfil(
                nombre = nombre,
                academia = academia,
                userId = uid
            )
        }
    }
}
