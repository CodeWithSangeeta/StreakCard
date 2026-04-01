package com.example.streakcard.ui.theme.component



import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.streakcard.domain.model.StreakStatus

/**
 * Reusable streak badge showing fire emoji + count with pulsing animation.
 */
@Composable
fun StreakBadge(
    streak: Int,
    status: StreakStatus,
    modifier: Modifier = Modifier
) {
    val bgColor by animateColorAsState(
        targetValue = when (status) {
            StreakStatus.DONE_TODAY -> Color(0xFF43A047)
            StreakStatus.AT_RISK    -> Color(0xFFE53935)
            StreakStatus.ACTIVE     -> Color(0xFFF57C00)
            StreakStatus.NOT_STARTED -> Color(0xFF9E9E9E)
        },
        animationSpec = tween(400),
        label = "badge_color"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "badge_pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (status == StreakStatus.AT_RISK) 1.08f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(700, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Row(
        modifier = modifier
            .scale(scale)
            .clip(RoundedCornerShape(20.dp))
            .background(bgColor.copy(alpha = 0.9f))
            .padding(horizontal = 10.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = when (status) {
                StreakStatus.DONE_TODAY  -> "✅"
                StreakStatus.AT_RISK     -> "⚠️"
                StreakStatus.NOT_STARTED -> "🌱"
                StreakStatus.ACTIVE      -> "🔥"
            },
            fontSize = 13.sp
        )
        Text(
            text = "$streak",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )
    }
}
