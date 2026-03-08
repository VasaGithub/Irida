package com.travelplanner.irida.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.travelplanner.irida.ui.theme.*
import kotlinx.coroutines.delay
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import com.travelplanner.irida.R
@Composable
fun SplashScreen(onSplashFinished: () -> Unit = {}) {

    // Animate logo scale on entry
    val logoScale = remember { Animatable(0.5f) }
    val logoAlpha = remember { Animatable(0f) }
    val textAlpha = remember { Animatable(0f) }
    val subtitleAlpha = remember { Animatable(0f) }
    var loadingProgress by remember { mutableFloatStateOf(0f) }

    val animatedProgress by animateFloatAsState(
        targetValue = loadingProgress,
        animationSpec = tween(durationMillis = 2000, easing = EaseInOutCubic),
        label = "loading"
    )

    LaunchedEffect(Unit) {
        // Logo animation
        logoScale.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium
            )
        )
        logoAlpha.animateTo(1f, animationSpec = tween(400))

        // Text fade in
        textAlpha.animateTo(1f, animationSpec = tween(500))
        delay(200)
        subtitleAlpha.animateTo(1f, animationSpec = tween(500))

        // Loading bar
        delay(300)
        loadingProgress = 1f
        delay(2200)

        onSplashFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(NavyDeep, NavyMid, Color(0xFF0D1F35))
                )
            ),
        contentAlignment = Alignment.Center
    ) {

        // Background decorative circles
        Box(
            modifier = Modifier
                .size(300.dp)
                .align(Alignment.TopEnd)
                .offset(x = 80.dp, y = (-60).dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            TurquoisePrimary.copy(alpha = 0.08f),
                            Color.Transparent
                        )
                    ),
                    shape = CircleShape
                )
        )
        Box(
            modifier = Modifier
                .size(250.dp)
                .align(Alignment.BottomStart)
                .offset(x = (-60).dp, y = 60.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            GoldAccent.copy(alpha = 0.06f),
                            Color.Transparent
                        )
                    ),
                    shape = CircleShape
                )
        )

        // Main content
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {

            // Logo placeholder
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo Irida",
                modifier = Modifier
                    .size(110.dp)
                    .scale(logoScale.value)
            )

            Spacer(modifier = Modifier.height(28.dp))

            // App name
            Text(
                text = "Irida",
                style = MaterialTheme.typography.displayLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 2.sp
                ),
                color = White.copy(alpha = textAlpha.value),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Tagline
            Text(
                text = "Travel Planner",
                style = MaterialTheme.typography.titleMedium,
                color = TurquoisePrimary.copy(alpha = subtitleAlpha.value),
                letterSpacing = 4.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Planifica tus aventuras a tu manera.",
                style = MaterialTheme.typography.bodyMedium,
                color = GrayMid.copy(alpha = subtitleAlpha.value),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(56.dp))

            // Loading bar
            LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier
                    .width(180.dp)
                    .height(3.dp)
                    .clip(CircleShape),
                color = TurquoisePrimary,
                trackColor = NavyLight,
                strokeCap = StrokeCap.Round
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "LOADING...",
                style = MaterialTheme.typography.labelSmall,
                color = GrayMid.copy(alpha = subtitleAlpha.value),
                letterSpacing = 3.sp
            )
        }

        // Version at bottom
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "v1.0.0 · Sprint 01",
                style = MaterialTheme.typography.labelSmall,
                color = GrayDark,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Built with ❤️ at Campus Igualada",
                style = MaterialTheme.typography.labelSmall,
                color = GrayDark
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
    IridaTheme {
        SplashScreen()
    }
}