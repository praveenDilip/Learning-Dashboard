package com.example.learningdashboard.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

/** Shared SELECT that computes lesson counts in SQL, so progress is always fresh. */
private const val PROGRESS_SELECT = """
    SELECT c.id AS id,
           c.title AS title,
           c.instructor AS instructor,
           (SELECT COUNT(*) FROM lessons l WHERE l.courseId = c.id) AS totalLessons,
           (SELECT COUNT(*) FROM lessons l WHERE l.courseId = c.id AND l.isCompleted = 1) AS completedLessons
    FROM courses c
"""

data class CourseProgressRow(
    val id: Int,
    val title: String,
    val instructor: String,
    val totalLessons: Int,
    val completedLessons: Int,
)

@Dao
interface CourseDao {

    @Query("$PROGRESS_SELECT ORDER BY c.id")
    fun observeCourses(): Flow<List<CourseProgressRow>>

    @Query("$PROGRESS_SELECT WHERE c.id = :courseId")
    fun observeCourse(courseId: Int): Flow<CourseProgressRow?>

    @Query("SELECT * FROM lessons WHERE courseId = :courseId ORDER BY position")
    fun observeLessons(courseId: Int): Flow<List<LessonEntity>>

    // Upsert updates existing rows. REPLACE would delete the row first,
    // and the ON DELETE CASCADE would then wipe all lesson progress.
    @Upsert
    suspend fun upsertCourses(courses: List<CourseEntity>)

    // IGNORE keeps existing lessons, so a refresh never overwrites the
    // user's completion state. Only new lessons are inserted.
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMissingLessons(lessons: List<LessonEntity>)

    @Query("UPDATE lessons SET isCompleted = :completed WHERE courseId = :courseId AND position = :position")
    suspend fun setLessonCompleted(courseId: Int, position: Int, completed: Boolean)
}