package com.example.learningdashboard.di

import android.content.Context
import com.example.learningdashboard.data.local.AppDatabase
import com.example.learningdashboard.data.remote.CourseApi
import com.example.learningdashboard.data.remote.MockCourseApi
import com.example.learningdashboard.data.repository.AuthRepository
import com.example.learningdashboard.data.repository.CourseRepository

class AppContainer(context: Context) {

    private val database = AppDatabase.create(context)
    private val courseApi: CourseApi = MockCourseApi(simulateFailure = false)

    val authRepository = AuthRepository()
    val courseRepository = CourseRepository(courseApi, database)
}