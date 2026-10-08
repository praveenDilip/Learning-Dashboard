package com.example.myapplication.ui.dashboard

import androidx.compose.ui.graphics.Color

object ProgressColors {
    val Complete = Color(0xFF2E7D32)   // Dark green: 100%
    val Almost = Color(0xFF7CB342)     // Light green: 75-99%
    val Halfway = Color(0xFFF9A825)    // Amber: 50-74%
    val Started = Color(0xFFEF6C00)    // Orange: 1-49%
    val NotStarted = Color(0xFFC62828) // Red: 0%
}

/** Maps a progress percentage (0-100) to a color. Thresholds are checked from highest to lowest. */
fun progressColor(percent: Int): Color = when {
    percent >= 100 -> ProgressColors.Complete
    percent >= 75 -> ProgressColors.Almost
    percent >= 50 -> ProgressColors.Halfway
    percent >= 1 -> ProgressColors.Started
    else -> ProgressColors.NotStarted
}