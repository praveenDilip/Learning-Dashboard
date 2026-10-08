package com.example.learningdashboard.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "courses")
data class CourseEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val instructor: String,
)

@Entity(
    tableName = "lessons",
    primaryKeys = ["courseId", "position"],
    foreignKeys = [
        ForeignKey(
            entity = CourseEntity::class,
            parentColumns = ["id"],
            childColumns = ["courseId"],
            onDelete = ForeignKey.CASCADE,
        )
    ],
    indices = [Index("courseId")],
)
data class LessonEntity(
    val courseId: Int,
    val position: Int,
    val title: String,
    val isCompleted: Boolean,
)