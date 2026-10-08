package com.example.learningdashboard.data.repository

import kotlinx.coroutines.delay

class InvalidCredentialsException : Exception("Invalid email or password")

class AuthRepository {

    // Demo credentials: demo@learn.com / password123
    suspend fun login(email: String, password: String): Result<Unit> {
        delay(1_000) // Simulated network latency
        return if (email == DEMO_EMAIL && password == DEMO_PASSWORD) {
            Result.success(Unit)
        } else {
            Result.failure(InvalidCredentialsException())
        }
    }

    private companion object {
        const val DEMO_EMAIL = "demo@learn.com"
        const val DEMO_PASSWORD = "password123"
    }
}