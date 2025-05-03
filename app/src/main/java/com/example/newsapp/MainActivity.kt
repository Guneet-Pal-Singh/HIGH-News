package com.example.newsapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.newsapp.api.Article
import com.example.newsapp.screens.*
import com.example.newsapp.ui.theme.NewsAppTheme
import com.google.gson.Gson

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val themeViewModel = ThemeViewModel(application)
        setContent {
            val theme by themeViewModel.theme.collectAsState()
            themeViewModel.registerPowerSaverReceiver()

            Log.d("MainActivity", "Current theme: $theme")

            // Use the unified theme composable
            NewsAppTheme(theme = theme) {
                val navController = rememberNavController()
                val viewModel = ViewModelProfileScreen(application = this.application)

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "splash_screen",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("splash_screen") { SplashScreen(navController) }
                        composable("main_screen") { LoginScreen(navController) }
                        composable("home_screen") { HomeScreen(navController) }
                        composable("register_screen") { RegisterScreen(navController) }
                        composable("profile_screen") {
                            ProfileScreen(navController, viewModel, themeViewModel)
                        }
                        composable("news_detail/{article}") { backStackEntry ->
                            val json = backStackEntry.arguments?.getString("article")
                            val article = Gson().fromJson(json, Article::class.java)
                            NewsDetailScreen(navController, article, viewModel)
                        }
                    }
                }
            }
        }
    }
}
