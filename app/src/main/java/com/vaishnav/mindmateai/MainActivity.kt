package com.vaishnav.mindmateai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.vaishnav.mindmateai.ui.home.HomeScreen
import com.vaishnav.mindmateai.ui.result.ResultScreen
import com.vaishnav.mindmateai.ui.chatbot.ChatbotScreen
import com.vaishnav.mindmateai.ui.theme.MindMateAITheme
import com.vaishnav.mindmateai.viewmodel.GeminiViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        setContent {
            MindMateAITheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val geminiViewModel: GeminiViewModel = viewModel()
                    NavHost(navController = navController, startDestination = "home") {
                        composable("home") {
                            HomeScreen(navController = navController, viewModel = geminiViewModel)
                        }
                        composable("chatbot") {
                            ChatbotScreen(navController = navController, viewModel = geminiViewModel)
                        }
                        composable(
                            "result/{title}",
                            arguments = listOf(
                                navArgument("title") { type = NavType.StringType }
                            )
                        ) { backStackEntry ->
                            val title = backStackEntry.arguments?.getString("title") ?: "Result"
                            ResultScreen(navController, geminiViewModel, title)
                        }
                    }
                }
            }
        }
    }
}
