package com.example.myapplication.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.learningdashboard.di.AppContainer
import com.example.learningdashboard.ui.dashboard.DashboardViewModel
import com.example.learningdashboard.ui.details.CourseDetailsViewModel
import com.example.learningdashboard.ui.login.LoginViewModel
import com.example.myapplication.ui.dashboard.DashboardScreen
import com.example.myapplication.ui.details.CourseDetailsScreen
import com.example.myapplication.ui.login.LoginScreen

object Routes {
    const val LOGIN = "login"
    const val DASHBOARD = "dashboard"
    const val DETAILS = "course/{courseId}"
    fun details(courseId: Int) = "course/$courseId"
}

@Composable
fun AppNavHost(
    container: AppContainer,
    modifier: Modifier = Modifier,
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.LOGIN,
        modifier = modifier,
    ) {

        composable(Routes.LOGIN) {
            val vm: LoginViewModel = viewModel(
                factory = viewModelFactory {
                    initializer { LoginViewModel(container.authRepository) }
                }
            )
            LoginScreen(
                viewModel = vm,
                onLoginSuccess = {
                    navController.navigate(Routes.DASHBOARD) {
                        popUpTo(Routes.LOGIN) { inclusive = true } // Prevent going back to login
                    }
                },
            )
        }

        composable(Routes.DASHBOARD) {
            val vm: DashboardViewModel = viewModel(
                factory = viewModelFactory {
                    initializer { DashboardViewModel(container.courseRepository) }
                }
            )
            DashboardScreen(
                viewModel = vm,
                onCourseClick = { courseId -> navController.navigate(Routes.details(courseId)) },
            )
        }

        composable(
            route = Routes.DETAILS,
            arguments = listOf(navArgument("courseId") { type = NavType.IntType }),
        ) { entry ->
            val courseId = entry.arguments!!.getInt("courseId")
            val vm: CourseDetailsViewModel = viewModel(
                factory = viewModelFactory {
                    initializer { CourseDetailsViewModel(courseId, container.courseRepository) }
                }
            )
            CourseDetailsScreen(viewModel = vm, onBack = { navController.popBackStack() })
        }
    }
}