package com.example.learningdashboard.data.remote

import kotlinx.coroutines.delay
import java.io.IOException

data class CourseDto(
    val id: Int,
    val title: String,
    val instructor: String,
    val lessons: List<LessonDto>,
)

data class LessonDto(
    val position: Int,
    val title: String,
    val completed: Boolean, // Used only to seed the first sync
)

interface CourseApi {
    suspend fun getCourses(): List<CourseDto>
}

class MockCourseApi(
    private val simulateFailure: Boolean = false, // Flip to true to demo the API failure state
) : CourseApi {

    override suspend fun getCourses(): List<CourseDto> {
        delay(800) // Simulated network latency
        if (simulateFailure) throw IOException("Simulated network failure")

        return listOf(
            CourseDto(
                id = 1,
                title = "Python Programming",
                instructor = "John Smith",
                lessons = lessons(
                    count = 20,
                    completed = 13, // 65%
                    titles = listOf("Introduction", "Variables & Data Types", "Functions", "OOP"),
                ),
            ),
            CourseDto(
                id = 2,
                title = "Generative AI",
                instructor = "Sarah Williams",
                lessons = lessons(count = 16, completed = 6), // 6/16 = 38%
            ),
            CourseDto(
                id = 3,
                title = "Full Stack Development",
                instructor = "David Brown",
                lessons = lessons(count = 28, completed = 7), // 25%
            ),
        )
    }

    private fun lessons(count: Int, completed: Int, titles: List<String> = emptyList()) =
        (1..count).map { i ->
            LessonDto(
                position = i,
                title = titles.getOrNull(i - 1) ?: "Lesson $i",
                completed = i <= completed,
            )
        }
}