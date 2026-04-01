package com.example.streakcard.ui.theme.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.streakcard.data.model.CardShape
import com.streakcard.data.model.CardSize
import com.streakcard.domain.model.Goal
import com.streakcard.domain.model.StreakStatus
import com.streakcard.ui.theme.cardGradients
import kotlin.math.*

// ── Card shape clip paths ────────────────────────────────────────────────────

private fun stadiumShape() = RoundedCornerShape(50)
private fun roundedShape() = RoundedCornerShape(24.dp)
private fun sharpShape()   = RoundedCornerShape(6.dp)
private fun circleShape()  = CircleShape

private fun cardShape(shape: CardShape): Shape = when (shape) {
    CardShape.ROUNDED  -> roundedShape()
    CardShape.STADIUM  -> stadiumShape()
    CardShape.SHARP    -> sharpShape()
    CardShape.CIRCLE   -> circleShape()
    CardShape.DIAMOND  -> GenericShape { size, _ ->
        val cx = size.width / 2f
        val cy = size.height / 2f
        moveTo(cx, 0f)
        lineTo(size.width, cy)
        lineTo(cx, size.height)
        lineTo(0f, cy)
        close()
    }
}

private fun cardDimensions(size: CardSize): Pair<Dp, Dp> = when (size) {
    CardSize.SMALL  -> 140.dp to 160.dp
    CardSize.MEDIUM -> 170.dp to 200.dp
    CardSize.LARGE  -> 200.dp to 240.dp
}

// ── Progress arc painter ─────────────────────────────────────────────────────

private fun DrawScope.drawProgressArc(progress: Float, color: Color) {
    val strokeWidth = 6f
    val padding = strokeWidth / 2 + 4f
    drawArc(
        color = color.copy(alpha = 0.3f),
        startAngle = -90f,
        sweepAngle = 360f,
        useCenter = false,
        topLeft = Offset(padding, padding),
        size = Size(size.width - padding * 2, size.height - padding * 2),
        style = androidx.compose.ui.graphics.drawscope.Stroke(width = strokeWidth)
    )
    drawArc(
        color = color,
        startAngle = -90f,
        sweepAngle = 360f * progress,
        useCenter = false,
        topLeft = Offset(padding, padding),
        size = Size(size.width - padding * 2, size.height - padding * 2),
        style = androidx.compose.ui.graphics.drawscope.Stroke(
            width = strokeWidth,
            cap = StrokeCap.Round
        )
    )
}

// ── Main GoalCard composable ─────────────────────────────────────────────────

/**
 * Visually rich, animated goal card.
 * Reusable — pass Goal domain object and callbacks.
 */
@Composable
fun GoalCard(
    goal: Goal,
    onCheckIn: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val gradient = cardGradients[goal.colorScheme]!!
    val shape = cardShape(goal.cardShape)
    val (width, height) = cardDimensions(goal.cardSize)

    // Animate check-in
    var showConfetti by remember { mutableStateOf(false) }
    val cardScale by animateFloatAsState(
        targetValue = if (goal.isCheckedToday) 1f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "cardScale"
    )

    // Shimmer on checked state
    val shimmerAlpha by rememberInfiniteTransition(label = "shimmer").animateFloat(
        initialValue = 0f, targetValue = 0.15f,
        animationSpec = infiniteRepeatable(tween(1200), RepeatMode.Reverse),
        label = "shimmerAlpha"
    )

    // Progress ring — target: 30 days = 100%
    val progressTarget = (goal.currentStreak / 30f).coerceIn(0f, 1f)
    val progressAnim by animateFloatAsState(
        targetValue = progressTarget,
        animationSpec = tween(800, easing = FastOutSlowInEasing),
        label = "progress"
    )

    Box(
        modifier = modifier
            .scale(cardScale)
            .width(width)
            .height(height)
            .clip(shape)
            .clickable(onClick = onClick)
    ) {
        // ── Gradient background
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(gradient.start, gradient.mid, gradient.end),
                        start = Offset(0f, 0f),
                        end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                    )
                )
        )

        // ── Shimmer overlay when checked
        if (goal.isCheckedToday) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White.copy(alpha = shimmerAlpha))
            )
        }

        // ── Decorative circle in background
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                color = Color.White.copy(alpha = 0.06f),
                radius = size.width * 0.75f,
                center = Offset(size.width * 0.85f, -size.height * 0.1f)
            )
            drawCircle(
                color = Color.White.copy(alpha = 0.04f),
                radius = size.width * 0.5f,
                center = Offset(-size.width * 0.15f, size.height * 0.85f)
            )
        }

        // ── Progress arc (drawn on Canvas at full size)
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawProgressArc(progressAnim, gradient.accent)
        }

        // ── Card content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top row: pin + emoji + streak badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                if (goal.isPinned) {
                    Icon(
                        imageVector = Icons.Default.PushPin,
                        contentDescription = "Pinned",
                        tint = Color.White.copy(alpha = 0.8f),
                        modifier = Modifier.size(16.dp)
                    )
                } else {
                    Spacer(Modifier.width(16.dp))
                }

                AnimatedEmoji(
                    emoji = goal.emoji,
                    fontSize = when (goal.cardSize) {
                        CardSize.SMALL  -> 28f
                        CardSize.MEDIUM -> 36f
                        CardSize.LARGE  -> 44f
                    },
                    isChecked = goal.isCheckedToday
                )

                StreakBadge(streak = goal.currentStreak, status = goal.streakStatus)
            }

            // Middle: title
            Text(
                text = goal.title,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = when (goal.cardSize) {
                    CardSize.SMALL  -> 13.sp
                    CardSize.MEDIUM -> 15.sp
                    CardSize.LARGE  -> 17.sp
                },
                maxLines = 2,
                lineHeight = 20.sp
            )

            // Bottom row: stats + check button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Best: ${goal.longestStreak}",
                        color = Color.White.copy(alpha = 0.75f),
                        fontSize = 11.sp
                    )
                    Text(
                        text = "Total: ${goal.totalCheckIns}",
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 10.sp
                    )
                }

                // Check-in button
                CheckInButton(
                    isChecked = goal.isCheckedToday,
                    accentColor = gradient.accent,
                    onCheckIn = {
                        showConfetti = true
                        onCheckIn()
                    }
                )
            }
        }

        // ── Confetti overlay
        if (showConfetti) {
            ConfettiEffect(
                modifier = Modifier.fillMaxSize(),
                trigger = true,
                colors = listOf(gradient.accent, gradient.start, gradient.mid, Color.White)
            )
            LaunchedEffect(Unit) {
                kotlinx.coroutines.delay(1800)
                showConfetti = false
            }
        }
    }
}

// ── Check-in button ──────────────────────────────────────────────────────────

@Composable
private fun CheckInButton(
    isChecked: Boolean,
    accentColor: Color,
    onCheckIn: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isChecked) 1f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "btnScale"
    )

    val btnColor by animateColorAsState(
        targetValue = if (isChecked) Color(0xFF43A047) else Color.White.copy(alpha = 0.25f),
        animationSpec = tween(300),
        label = "btnColor"
    )

    // Pulse animation on the button when not yet checked
    val pulseScale by rememberInfiniteTransition(label = "pulse").animateFloat(
        initialValue = 1f,
        targetValue = if (!isChecked) 1.12f else 1f,
        animationSpec = infiniteRepeatable(tween(900, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "pulseScale"
    )

    Box(
        modifier = Modifier
            .scale(scale * pulseScale)
            .size(38.dp)
            .clip(CircleShape)
            .background(btnColor)
            .border(2.dp, Color.White.copy(alpha = 0.6f), CircleShape)
            .clickable(enabled = !isChecked, onClick = onCheckIn),
        contentAlignment = Alignment.Center
    ) {
        if (isChecked) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Done",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        } else {
            Text(text = "✓", color = Color.White, fontSize = 16.sp)
        }
    }
}
