package com.example.coachtrack

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Extensión para crear el DataStore
private val Context.profileDataStore by preferencesDataStore(name = "coach_profile_prefs")

class CoachProfileDataStore(private val context: Context) {

    companion object {
        private val KEY_NOMBRE = stringPreferencesKey("nombre_profesor")
        private val KEY_ACADEMIA = stringPreferencesKey("academia_profesor")
        private val KEY_USER_ID = stringPreferencesKey("user_id_profesor")
    }

    val perfilFlow: Flow<CoachProfile> = context.profileDataStore.data.map { prefs ->
        CoachProfile(
            nombreProfesor = prefs[KEY_NOMBRE] ?: "",
            academia = prefs[KEY_ACADEMIA] ?: "",
            userId = prefs[KEY_USER_ID] ?: ""
        )
    }

    suspend fun guardarPerfil(
        nombre: String,
        academia: String,
        userId: String
    ) {
        context.profileDataStore.edit { prefs ->
            prefs[KEY_NOMBRE] = nombre
            prefs[KEY_ACADEMIA] = academia
            prefs[KEY_USER_ID] = userId
        }
    }
}
