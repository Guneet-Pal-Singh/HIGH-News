package com.example.newsapp.screens

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.newsapp.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {
    var expandCircle by remember { mutableStateOf(false) }

    val configuration = LocalConfiguration.current
    val density = LocalDensity.current

    // Get screen width & height in dp
    val screenWidthDp = configuration.screenWidthDp.dp
    val screenHeightDp = configuration.screenHeightDp.dp

    // Get the largest screen dimension and scale it up for full coverage
    val maxDimension = maxOf(screenWidthDp, screenHeightDp)
    val maxCircleSize = maxDimension * 2.5f // Ensures full expansion

    // Animate the circle expansion
    val circleSize by animateDpAsState(
        targetValue = if (expandCircle) maxCircleSize else 200.dp,
        animationSpec = tween(durationMillis = 1000),
        label = "Circle Expansion"
    )

    LaunchedEffect(Unit) {
        delay(1500) // Delay before animation starts
        expandCircle = true
        delay(1000) // Wait for animation to complete
        navController.navigate("main_screen") {
            popUpTo("splash_screen") { inclusive = true }
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            // Expanding circle that fully covers the screen
            Box(
                modifier = Modifier
                    .size(circleSize)
                    .clip(CircleShape)
                    .background(Color(0xFFFFF4CC)) // Soft pastel yellow
            )

            // Fixed-size logo at 140.dp
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "App Logo",
                modifier = Modifier.size(140.dp)
            )
        }
    }
}
