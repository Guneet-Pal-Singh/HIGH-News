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

//@Composable
@Composable
fun SplashScreen(navController: NavController) {
    var expandCircle by remember { mutableStateOf(false) }

    val configuration = LocalConfiguration.current
    val density = LocalDensity.current

    // Get screen size in dp
    val screenWidthDp = configuration.screenWidthDp.dp
    val screenHeightDp = configuration.screenHeightDp.dp

    // Convert to pixels for precise diagonal calculation
    val screenWidthPx = with(density) { screenWidthDp.toPx() }
    val screenHeightPx = with(density) { screenHeightDp.toPx() }
    val diagonalPx = kotlin.math.hypot(screenWidthPx, screenHeightPx)
    val diagonalDp = with(density) { diagonalPx.toDp() }

    // Ensure complete coverage by making the circle significantly larger
    val maxCircleSize = diagonalDp * 2f // Increased to 2x for full coverage

    // Animate the circle expansion from 0.dp to the max computed size
    val circleSize by animateDpAsState(
        targetValue = if (expandCircle) maxCircleSize else 0.dp, // Start from 0.dp
        animationSpec = tween(durationMillis = 1200),
        label = "Circle Expansion"
    )

    LaunchedEffect(Unit) {
        delay(500) // Small delay before starting animation
        expandCircle = true
        delay(1200) // Wait for animation completion
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
                    .background(MaterialTheme.colorScheme.primary)
            )

            // Logo remains at a fixed size in the center
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "App Logo",
                modifier = Modifier.size(140.dp)
            )
        }
    }
}
