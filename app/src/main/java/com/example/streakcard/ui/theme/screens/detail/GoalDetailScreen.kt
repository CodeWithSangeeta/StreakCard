package com.example.streakcard.ui.theme.screens.detail


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.streakcard.domain.model.Goal
import com.streakcard.domain.model.StreakStatus
import com.streakcard.ui.components.GoalCard
import com.streakcard.ui.theme.cardGradients
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalDetailScreen(
    onBack: () -> Unit,
    viewModel: GoalDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.isDeleted) {
        if (uiState.isDeleted) onBack()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(uiState.goal?.title ?: "Goal Detail") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Back") }
                },
                actions = {
                    IconButton(onClick = viewModel::delete) {
                        Icon(Icons.Default.Delete, "Delete", tint = MaterialTheme.colorScheme.error)
                    }
                }
            )
        }
    ) { padding ->
        val goal = uiState.goal
        if (goal == null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            DetailContent(goal = goal, onCheckIn = viewModel::checkIn, modifier = Modifier.padding(padding))
        }
    }
}

@Composable
private fun DetailContent(goal: Goal, onCheckIn: () -> Unit, modifier: Modifier = Modifier) {
    val gradient = cardGradients[goal.colorScheme]!!
    val daysSinceStart = ChronoUnit.DAYS.between(goal.createdAt, LocalDate.now()).toInt()

    Column(
        modifier = modifier.fillMaxSize().verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(Brush.linearGradient(listOf(gradient.start, gradient.mid, gradient.end))),
            contentAlignment = Alignment.Center
        ) {
            GoalCard(goal = goal, onCheckIn = onCheckIn, onClick = {})
        }

        // Stats row
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            StatRow(
                items = listOf(
                    Triple("🔥", "Current", "${goal.currentStreak} days"),
                    Triple("🏆", "Best", "${goal.longestStreak} days"),
                    Triple("✅", "Total", "${goal.totalCheckIns} check-ins"),
                    Triple("📅", "Since", "${daysSinceStart} days ago")
                )
            )

            Spacer(Modifier.height(16.dp))

            // Status card
            StatusCard(goal)

            Spacer(Modifier.height(16.dp))

            // Miss policy info
            InfoCard(
                icon = if (goal.missPolicy.name == "RESET") "🔁" else "➡️",
                title = "Miss Policy",
                body = if (goal.missPolicy.name == "RESET")
                    "Streak resets to 1 when you miss a day."
                else "Streak continues even if you skip a day."
            )

            if (goal.notes.isNotBlank()) {
                Spacer(Modifier.height(12.dp))
                InfoCard(icon = "📝", title = "Notes", body = goal.notes)
            }

            Spacer(Modifier.height(100.dp))
        }
    }
}

@Composable
private fun StatRow(items: List<Triple<String, String, String>>) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        items.forEach { (icon, label, value) ->
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(icon, fontSize = 24.sp)
                Text(value, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
            }
        }
    }
}

@Composable
private fun StatusCard(goal: Goal) {
    val (bg, msg, emoji) = when (goal.streakStatus) {
        StreakStatus.DONE_TODAY  -> Triple(Color(0xFF43A047), "Great job! You've checked in today.", "🎉")
        StreakStatus.AT_RISK     -> Triple(Color(0xFFE53935), "Check in today to keep your streak alive!", "⚠️")
        StreakStatus.ACTIVE      -> Triple(Color(0xFFF57C00), "Keep going — check in before midnight!", "🔥")
        StreakStatus.NOT_STARTED -> Triple(Color(0xFF607D8B), "Mark your first check-in to start your streak.", "🌱")
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(bg.copy(alpha = 0.12f))
            .border(1.dp, bg.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(emoji, fontSize = 28.sp)
        Text(msg, fontWeight = FontWeight.Medium, fontSize = 14.sp, color = bg)
    }
}

@Composable
private fun InfoCard(icon: String, title: String, body: String) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(icon, fontSize = 22.sp)
            Column {
                Text(title, fontWeight = FontWeight.SemiBold, fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                Text(body, fontSize = 14.sp)
            }
        }
    }
}
