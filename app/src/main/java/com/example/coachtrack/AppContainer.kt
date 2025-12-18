package com.example.coachtrack

import android.app.Application
import androidx.room.Room
import com.example.coachtrack.data.repository.AlumnoRepositoryHibrido
import com.example.coachtrack.data.repository.ProfesorRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlin.jvm.java

class AppContainer(applicationContext: Application) {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = Firebase.firestore

    private val database: CoachTrackDatabase by lazy {
        Room.databaseBuilder(
            context = applicationContext,
            klass = CoachTrackDatabase::class.java,
            name = "coachtrack_db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    private val alumnoDao by lazy { database.alumnoDao() }
    private val sesionDao by lazy { database.sesionDao() }
    private val tacticaDao by lazy { database.tacticaDao() }
    private val profesorDao by lazy { database.profesorDao() }

    val alumnoRepositoryHibrido: AlumnoRepositoryHibrido by lazy {
        AlumnoRepositoryHibrido(
            auth = auth,
            firestore = firestore,
            alumnoDao = alumnoDao,
            profesorDao = profesorDao,
            sesionDao = sesionDao,
            tacticaDao = tacticaDao
        )
    }

    val profesorRepository: ProfesorRepository by lazy {
        ProfesorRepository(
            auth = auth,
            firestore = firestore,
            profesorDao = profesorDao,
            alumnoDao = alumnoDao,
            db = database
        )
    }

    val sesionRepository: SesionRepository by lazy {
        SesionRepository(applicationContext)     }
}
