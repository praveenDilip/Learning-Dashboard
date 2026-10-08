package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import com.example.myapplication.ui.navigation.AppNavHost
import com.example.myapplication.ui.theme.SystemBlue

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val blue = SystemBlue.toArgb()
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(blue),
            navigationBarStyle = SystemBarStyle.dark(blue),
        )

        val container = (application as LearningApp).container

        setContent {
            MaterialTheme {
                Scaffold { innerPadding ->
                    AppNavHost(
                        container = container,
                        modifier = Modifier.padding(innerPadding),
                    )
                }
            }
        }
    }
}