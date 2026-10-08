package com.example.learningdashboard.ui.login

object LoginValidator {

    private val emailRegex = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

    fun emailError(email: String): String? = when {
        email.isBlank() -> "Email is required"
        !emailRegex.matches(email.trim()) -> "Enter a valid email"
        else -> null
    }

    fun passwordError(password: String): String? = when {
        password.isBlank() -> "Password is required"
        password.length < 6 -> "Password must be at least 6 characters"
        else -> null
    }
}