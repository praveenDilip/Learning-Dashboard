package com.example.learningdashboard.data.repository

import androidx.room.withTransaction
import com.example.learningdashboard.data.local.AppDatabase
import com.example.learningdashboard.data.local.CourseProgressRow
import com.example.learningdashboard.data.local.CourseEntity
import com.example.learningdashboard.data.local.LessonEntity
import com.example.learningdashboard.data.remote.CourseApi
import com.example.myapplication.domain.model.Course
import com.example.myapplication.domain.model.Lesson
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CourseRepository(
    private val api: CourseApi,
    private val database: AppDatabase,
) {
    private val dao = database.courseDao()

    // ---- Reads: always from Room (works offline) ----

    fun observeCourses(): Flow<List<Course>> =
        dao.observeCourses().map { rows -> rows.map { it.toDomain() } }

    fun observeCourse(courseId: Int): Flow<Course?> =
        dao.observeCourse(courseId).map { it?.toDomain() }

    fun observeLessons(courseId: Int): Flow<List<Lesson>> =
        dao.observeLessons(courseId).map { list -> list.map { it.toDomain() } }

    // ---- Writes ----

    /** Pulls fresh data from the API and writes it into Room atomically. */
    suspend fun refresh(): Result<Unit> = try {
        val dtos = api.getCourses()
        database.withTransaction {
            dao.upsertCourses(
                dtos.map { CourseEntity(it.id, it.title, it.instructor) }
            )
            dao.insertMissingLessons(
                dtos.flatMap { course ->
                    course.lessons.map { lesson ->
                        LessonEntity(course.id, lesson.position, lesson.title, lesson.completed)
                    }
                }
            )
        }
        Result.success(Unit)
    } catch (e: CancellationException) {
        throw e // Never swallow coroutine cancellation
    } catch (e: Exception) {
        Result.failure(e)
    }

    /** Local write: the user's progress is saved immediately, with or without a network. */
    suspend fun setLessonCompleted(courseId: Int, position: Int, completed: Boolean) {
        dao.setLessonCompleted(courseId, position, completed)
    }
}

private fun CourseProgressRow.toDomain() =
    Course(id, title, instructor, totalLessons, completedLessons)

private fun LessonEntity.toDomain() = Lesson(position, title, isCompleted)