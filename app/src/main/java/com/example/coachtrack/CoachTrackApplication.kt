package com.example.coachtrack

import android.app.Application
import com.google.firebase.BuildConfig
import timber.log.Timber


class CoachTrackApplication : Application() {
    val container: AppContainer by lazy { AppContainer(this) }

    override fun onCreate() {
        super.onCreate()
        if(BuildConfig.DEBUG){
            Timber.plant(Timber.DebugTree())
        }
    }
}

//Con override..." Timber queda completamente funcional.Los Logs aparecerán en el Logcat con el nombre de la clase como tag,
//y en release se silencian solos
