package com.example.streakcard.ui.theme.component


import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Reusable floating animated emoji that bobs up/down
 * and gently rotates for a lively card logo effect.
 */
@Composable
fun AnimatedEmoji(
    emoji: String,
    modifier: Modifier = Modifier,
    fontSize: Float = 36f,
    isChecked: Boolean = false
) {
    val infiniteTransition = rememberInfiniteTransition(label = "emoji_anim")

    val offsetY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bob"
    )

    val rotation by infiniteTransition.animateFloat(
        initialValue = -6f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "rotate"
    )

    val scale by animateFloatAsState(
        targetValue = if (isChecked) 1.3f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "scale"
    )

    Text(
        text = emoji,
        fontSize = fontSize.sp,
        modifier = modifier
            .offset(y = offsetY.dp)
            .rotate(rotation)
            .scale(scale)
    )
}
