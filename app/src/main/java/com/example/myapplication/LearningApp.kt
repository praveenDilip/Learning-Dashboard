package com.example.myapplication

import android.app.Application
import com.example.learningdashboard.di.AppContainer
import kotlin.getValue

class LearningApp : Application() {
    val container: AppContainer by lazy { AppContainer(this) }
}