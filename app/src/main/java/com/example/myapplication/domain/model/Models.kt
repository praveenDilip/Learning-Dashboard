package com.example.myapplication.domain.model

data class Course(
    val id: Int,
    val title: String,
    val instructor: String,
    val totalLessons: Int,
    val completedLessons: Int,
) {
    val progressPercent: Int
        get() = ProgressCalculator.percent(completedLessons, totalLessons)
}

data class Lesson(
    val position: Int,
    val title: String,
    val isCompleted: Boolean,
)


object ProgressCalculator {

    /** Returns progress as a whole percentage, rounded half-up. */
    fun percent(completed: Int, total: Int): Int {
        require(completed >= 0 && total >= 0) { "Counts cannot be negative" }
        if (total == 0) return 0
        return (completed * 100 + total / 2) / total
    }
}