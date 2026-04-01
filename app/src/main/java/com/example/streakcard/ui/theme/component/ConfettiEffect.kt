package com.example.streakcard.ui.theme.component


import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

data class ConfettiParticle(
    val startX: Float,
    val startY: Float,
    val angle: Float,
    val speed: Float,
    val color: Color,
    val size: Float,
    val rotation: Float
)

/**
 * Reusable burst confetti effect for goal check-in celebration.
 */
@Composable
fun ConfettiEffect(
    modifier: Modifier = Modifier,
    trigger: Boolean,
    colors: List<Color> = listOf(
        Color(0xFFFF6B6B), Color(0xFFFFE66D), Color(0xFF4ECDC4),
        Color(0xFFA8E6CF), Color(0xFFFF8B94), Color(0xFF98D8C8)
    )
) {
    if (!trigger) return

    val particles = remember {
        List(30) {
            ConfettiParticle(
                startX = 0.5f,
                startY = 0.5f,
                angle = Random.nextFloat() * 360f,
                speed = Random.nextFloat() * 0.4f + 0.15f,
                color = colors.random(),
                size = Random.nextFloat() * 12f + 6f,
                rotation = Random.nextFloat() * 360f
            )
        }
    }

    val transition = rememberInfiniteTransition(label = "confetti")
    val progress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "progress"
    )

    Canvas(modifier = modifier) {
        particles.forEach { p ->
            val alpha = (1f - progress).coerceIn(0f, 1f)
            val rad = Math.toRadians(p.angle.toDouble())
            val x = size.width * (p.startX + cos(rad).toFloat() * p.speed * progress * 2f)
            val y = size.height * (p.startY + sin(rad).toFloat() * p.speed * progress * 2f) +
                    (progress * progress * size.height * 0.3f)
            drawCircle(
                color = p.color.copy(alpha = alpha),
                radius = p.size,
                center = Offset(x, y)
            )
        }
    }
}
