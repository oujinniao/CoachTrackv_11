package com.example.coachtrack

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.json.JSONObject

// Variables globales proporcionadas por el entorno.
// Las definimos como lateinit var para poder inicializarlas en tiempo de ejecución.
// Si no están definidas, el try/catch de la AppNavigation manejará la situación.
lateinit var __app_id: String
lateinit var __firebase_config: String
lateinit var __initial_auth_token: String

data class FirestoreState(
    val isAuthenticated: Boolean = false,
    val userId: String = "",
    val errorMessage: String? = null
)

/**
 * ViewModel que gestiona la inicialización y autenticación de Firebase.
 */
class FirebaseManager(application: android.app.Application) : AndroidViewModel(application) {

    private val TAG = "FirebaseManager"

    // Referencias a Firebase. Solo se inicializan si la configuración existe.
    var auth: FirebaseAuth? = null
    var db: FirebaseFirestore? = null
    var appId: String = "default-app-id"

    // Estado de Firestore (si está listo para usarse)
    private val _firestoreState = mutableStateOf(FirestoreState())
    val firestoreState: State<FirestoreState> = _firestoreState

    init {
        // La inicialización debe ocurrir en un bloque try/catch ya que
        // las variables globales podrían no estar definidas si se ejecuta fuera del entorno.
        try {
            appId = if (::__app_id.isInitialized) __app_id else "default-app-id"
            val firebaseConfigJson = if (::__firebase_config.isInitialized) __firebase_config else "{}"

            val firebaseConfig = parseFirebaseConfig(firebaseConfigJson)

            // 1. Inicializar Firebase
            if (FirebaseApp.getApps(application).isEmpty()) {
                val context = application.applicationContext
                val options = parseFirebaseConfig(firebaseConfigJson)
                FirebaseApp.initializeApp(context, options, appId)
                Log.d(TAG, "Firebase inicializado con éxito para AppId: $appId")
            }

            // 2. Obtener instancias de servicios
            auth = Firebase.auth
            db = Firebase.firestore
            db?.firestoreSettings = com.google.firebase.firestore.firestoreSettings {
                isPersistenceEnabled = true // Habilita persistencia offline
            }

            // 3. Autenticar al usuario
            authenticateUser()

        } catch (e: Exception) {
            Log.e(TAG, "Error durante la inicialización de Firebase: ${e.message}")
            _firestoreState.value = _firestoreState.value.copy(
                errorMessage = "Error de configuración de Firebase. ¿Estás en un entorno de desarrollo?"
            )
        }
    }

    /**
     * Autentica al usuario usando el token de seguridad o de forma anónima.
     */
    private fun authenticateUser() {
        viewModelScope.launch {
            try {
                // Si el token inicial está disponible, úsalo. Si no, usa autenticación anónima.
                if (::__initial_auth_token.isInitialized && __initial_auth_token.isNotEmpty()) {
                    auth?.signInWithCustomToken(__initial_auth_token)?.await()
                    Log.d(TAG, "Autenticación exitosa con Custom Token.")
                } else {
                    auth?.signInAnonymously()?.await()
                    Log.d(TAG, "Autenticación anónima exitosa.")
                }

                // Esperar a que el usuario se establezca
                auth?.currentUser?.let { user ->
                    _firestoreState.value = _firestoreState.value.copy(
                        isAuthenticated = true,
                        userId = user.uid
                    )
                    Log.d(TAG, "Usuario autenticado: ${user.uid}")
                    // Habilitar logging de Firestore para depuración
                    FirebaseFirestore.setLoggingEnabled(true)
                } ?: run {
                    throw IllegalStateException("No se pudo obtener el usuario autenticado.")
                }

            } catch (e: Exception) {
                Log.e(TAG, "Fallo en la autenticación: ${e.message}")
                _firestoreState.value = _firestoreState.value.copy(
                    errorMessage = "Fallo en la autenticación: ${e.message}"
                )
            }
        }
    }

    /**
     * Helper para parsear la configuración JSON en opciones de Firebase.
     */
    private fun parseFirebaseConfig(jsonString: String): com.google.firebase.FirebaseOptions {
        val json = JSONObject(jsonString)
        return com.google.firebase.FirebaseOptions.Builder()
            .setApplicationId(json.optString("applicationId"))
            .setApiKey(json.optString("apiKey"))
            .setProjectId(json.optString("projectId"))
            .setDatabaseUrl(json.optString("databaseUrl"))
            .setStorageBucket(json.optString("storageBucket"))
            .setGcmSenderId(json.optString("gcmSenderId"))
            .build()
    }
}