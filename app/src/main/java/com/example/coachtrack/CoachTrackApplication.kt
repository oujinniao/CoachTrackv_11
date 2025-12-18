package com.example.coachtrack

import android.app.Application


class CoachTrackApplication : Application() {
    val container: AppContainer by lazy { AppContainer(this) }
}
