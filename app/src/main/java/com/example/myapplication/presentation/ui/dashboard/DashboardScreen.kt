package com.example.myapplication.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.learningdashboard.ui.dashboard.DashboardUiState
import com.example.learningdashboard.ui.dashboard.DashboardViewModel
import com.example.myapplication.domain.model.Course

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    onCourseClick: (Int) -> Unit,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    when (val s = state) {
        DashboardUiState.Loading -> CenteredContent {
            CircularProgressIndicator()
        }

        DashboardUiState.Empty -> CenteredContent {
            Text("No courses available")
            Spacer(Modifier.height(12.dp))
            Button(onClick = viewModel::refresh) { Text("Retry") }
        }

        is DashboardUiState.Error -> CenteredContent {
            Text(s.message)
            Spacer(Modifier.height(12.dp))
            Button(onClick = viewModel::refresh) { Text("Retry") }
        }

        is DashboardUiState.Success -> Column(Modifier.fillMaxSize()) {
            s.warning?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.onTertiaryContainer,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.tertiaryContainer)
                        .padding(12.dp),
                )
            }
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(s.courses, key = { it.id }) { course ->
                    CourseCard(course = course, onClick = { onCourseClick(course.id) })
                }
            }
        }
    }
}

@Composable
private fun CourseCard(
    course: Course,
    onClick: () -> Unit,
) {
    // One color per progress level, shared by the bar, the text, and the button.
    val barColor = progressColor(course.progressPercent)
    val isComplete = course.progressPercent >= 100

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            Modifier.padding(16.dp)
        ) {
            Text(
                text = course.title,
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = "Instructor: ${course.instructor}",
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = "${course.totalLessons} lessons",
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(Modifier.height(12.dp))

            LinearProgressIndicator(
                progress = { course.progressPercent / 100f },
                modifier = Modifier.fillMaxWidth(),
                color = barColor,
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = "${course.progressPercent}% complete",
                style = MaterialTheme.typography.bodySmall,
                color = barColor,
            )

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = onClick,
                modifier = Modifier.align(Alignment.End),
                colors = ButtonDefaults.buttonColors(
                    containerColor = barColor,
                    contentColor = Color.White,
                ),
            ) {
                Text(if (isComplete) "Completed" else "Continue")
            }
        }
    }
}

@Composable
private fun CenteredContent(content: @Composable () -> Unit) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) { content() }
    }
}