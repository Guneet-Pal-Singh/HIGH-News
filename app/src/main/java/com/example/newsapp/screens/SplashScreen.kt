package com.example.newsapp.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
    var morphToRectangle by remember { mutableStateOf(false) }

    val configuration = LocalConfiguration.current
    val density = LocalDensity.current

    // Compute screen diagonal for full coverage
    val screenWidthDp = configuration.screenWidthDp.dp
    val screenHeightDp = configuration.screenHeightDp.dp
    val screenWidthPx = with(density) { screenWidthDp.toPx() }
    val screenHeightPx = with(density) { screenHeightDp.toPx() }
    val diagonalPx = kotlin.math.hypot(screenWidthPx, screenHeightPx)
    val diagonalDp = with(density) { diagonalPx.toDp() }

    // Max circle size for full screen coverage
    val maxCircleSize = remember { diagonalDp * 2f }

    // Animate circle expansion
    val circleSize by animateDpAsState(
        targetValue = if (expandCircle) maxCircleSize else 0.dp,
        animationSpec = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
        label = "Circle Expansion"
    )

    // Animate shape morphing from circle to rounded rectangle
    val cornerRadius by animateDpAsState(
        targetValue = if (morphToRectangle) 0.dp else 100.dp,
        animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
        label = "Shape Morphing"
    )

    LaunchedEffect(Unit) {
        delay(500)  // Initial delay before animation starts
        expandCircle = true
        delay(150) // Wait for circle to fully expand
        morphToRectangle = true
        delay(800)  // Give time for the rectangle transition
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
            // Expanding & Morphing Shape
            Box(
                modifier = Modifier
                    .size(circleSize)
                    .clip(RoundedCornerShape(cornerRadius))
                    .background(MaterialTheme.colorScheme.primary)
            )

            // Logo stays fixed at center
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "App Logo",
                modifier = Modifier.size(140.dp)
            )
        }
    }
}
