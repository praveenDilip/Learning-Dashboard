package com.example.myapplication.ui.details

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.learningdashboard.ui.details.CourseDetailsUiState
import com.example.learningdashboard.ui.details.CourseDetailsViewModel
import com.example.myapplication.domain.model.Lesson
import com.example.myapplication.ui.dashboard.ProgressColors
import com.example.myapplication.ui.dashboard.progressColor

@Composable
fun CourseDetailsScreen(
    viewModel: CourseDetailsViewModel,
    onBack: () -> Unit,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    when (val s = state) {
        CourseDetailsUiState.Loading -> Box(Modifier.fillMaxSize(), Alignment.Center) {
            CircularProgressIndicator()
        }

        CourseDetailsUiState.NotFound -> Column(Modifier.padding(24.dp)) {
            Text("This course no longer exists.")
            Button(onClick = onBack) { Text("Back") }
        }

        is CourseDetailsUiState.Content -> {
            val barColor = progressColor(s.course.progressPercent)

            LazyColumn(
                contentPadding = PaddingValues(16.dp),
            ) {
                item {
                    Text(s.course.title, style = MaterialTheme.typography.headlineSmall)
                    Text("Instructor: ${s.course.instructor}")
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = "Progress: ${s.course.progressPercent}%  " +
                                "(${s.course.completedLessons}/${s.course.totalLessons} lessons)",
                        color = barColor,
                    )
                    LinearProgressIndicator(
                        progress = { s.course.progressPercent / 100f },
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        color = barColor,
                    )
                    HorizontalDivider()
                }

                items(s.lessons, key = { it.position }) { lesson ->
                    LessonRow(lesson = lesson, onToggle = { viewModel.toggleLesson(lesson) })
                    HorizontalDivider()
                }
            }
        }
    }
}

@Composable
private fun LessonRow(lesson: Lesson, onToggle: () -> Unit) {
    val statusColor = if (lesson.isCompleted) {
        ProgressColors.Complete
    } else {
        ProgressColors.NotStarted
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onToggle)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Checkbox(
            checked = lesson.isCompleted,
            onCheckedChange = { onToggle() },
            colors = CheckboxDefaults.colors(
                checkedColor = ProgressColors.Complete,
            ),
        )
        Text(lesson.title, modifier = Modifier.weight(1f))
        Text(
            text = if (lesson.isCompleted) "✓ Completed" else "○ Pending",
            style = MaterialTheme.typography.bodySmall,
            color = statusColor,
        )
    }
}