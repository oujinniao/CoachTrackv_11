package com.example.coachtrack

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.coachtrack.ui.theme.CoachTrackTheme
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : ComponentActivity() {
    companion object {
        private const val TAG = "CoachTrackAuth"
    }

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d(TAG, "═══════════════════════════════════════")
        Log.d(TAG, "🚀 MainActivity.onCreate() INICIADO")
        Log.d(TAG, "═══════════════════════════════════════")

        // ✅ 1. INICIALIZAR FIREBASE (CON LOGS)
        try {
            Log.d(TAG, "1. Inicializando FirebaseApp...")
            FirebaseApp.initializeApp(this)
            Log.d(TAG, "✅ FirebaseApp inicializado correctamente")
        } catch (e: Exception) {
            Log.e(TAG, "❌ ERROR inicializando FirebaseApp: ${e.message}")
        }

        // ✅ 2. OBTENER INSTANCIA DE AUTH
        auth = Firebase.auth
        Log.d(TAG, "2. Firebase Auth instanciado")

        // ✅ 3. VERIFICAR USUARIO ACTUAL
        val currentUser = auth.currentUser
        if (currentUser != null) {
            Log.d(TAG, "✅ USUARIO YA EXISTE:")
            Log.d(TAG, "   ID: ${currentUser.uid}")
            Log.d(TAG, "   Email: ${currentUser.email ?: "Anónimo"}")
            Log.d(TAG, "   Creación: ${currentUser.metadata?.creationTimestamp}")
        } else {
            Log.d(TAG, "🔍 No hay usuario, iniciando autenticación anónima...")

            // ✅ 4. AUTENTICAR ANÓNIMAMENTE (CON CALLBACKS COMPLETOS)
            auth.signInAnonymously()
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        Log.d(TAG, "═══════════════════════════════════════")
                        Log.d(TAG, "🎉 ✅ AUTENTICACIÓN ANÓNIMA EXITOSA!")
                        Log.d(TAG, "═══════════════════════════════════════")
                        Log.d(TAG, "User ID: ${user?.uid}")
                        Log.d(TAG, "Provider: ${user?.providerId}")
                        Log.d(TAG, "Is Anonymous: ${user?.isAnonymous}")
                    } else {
                        Log.e(TAG, "❌ ERROR EN AUTENTICACIÓN:")
                        Log.e(TAG, "   Código: ${task.exception?.javaClass?.simpleName}")
                        Log.e(TAG, "   Mensaje: ${task.exception?.message}")
                        task.exception?.printStackTrace()
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e(TAG, "❌ FALLA EN AUTENTICACIÓN (OnFailure):")
                    Log.e(TAG, "   ${exception.message}")
                    exception.printStackTrace()
                }
        }

        // ✅ 5. MOSTRAR UI (NO ESPERAR)
        Log.d(TAG, "3. Mostrando interfaz de usuario...")
        setContent {
            CoachTrackTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }

        Log.d(TAG, "═══════════════════════════════════════")
        Log.d(TAG, "🏁 MainActivity.onCreate() FINALIZADO")
        Log.d(TAG, "═══════════════════════════════════════")
    }
}