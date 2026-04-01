package com.example.streakcard.widget


import android.content.Context
import androidx.compose.runtime.Composable
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import androidx.glance.layout.defaultWeight
import androidx.glance.ImageProvider
import com.streakcard.MainActivity
import com.streakcard.data.local.AppDatabase
import com.streakcard.domain.mapper.toDomain
import com.streakcard.domain.model.Goal
import kotlinx.coroutines.flow.first
import androidx.glance.layout.wrapContentWidth
import androidx.glance.text.FontStyle
import androidx.compose.ui.graphics.Color
import androidx.glance.layout.wrapContentSize
import androidx.glance.unit.Dimension

class GoalWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val db = AppDatabase.buildInstance(context)
        val goals = try {
            db.goalDao().getWidgetGoals().first().map { it.toDomain() }
        } catch (e: Exception) {
            emptyList()
        }

        provideContent {
            WidgetContent(goals = goals.take(4))
        }
    }
}

@Composable
private fun WidgetContent(goals: List<Goal>) {
    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(ColorProvider(Color(0xFF1C1B1F)))
            .padding(androidx.glance.layout.padding(horizontal = 12, vertical = 10))
            .cornerRadius(16),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "🔥 StreakCard",
            style = TextStyle(
                color = ColorProvider(Color.White),
                fontSize = androidx.glance.unit.sp(14),
                fontWeight = FontWeight.Bold
            )
        )
        Spacer(GlanceModifier.height(8.dp))

        if (goals.isEmpty()) {
            Text(
                text = "No widget goals. Long-press a card in the app.",
                style = TextStyle(
                    color = ColorProvider(Color.White.copy(alpha = 0.6f)),
                    fontSize = androidx.glance.unit.sp(12)
                )
            )
        } else {
            goals.forEach { goal ->
                WidgetGoalRow(goal)
                Spacer(GlanceModifier.height(6.dp))
            }
        }
    }
}

@Composable
private fun WidgetGoalRow(goal: Goal) {
    Row(
        modifier = GlanceModifier
            .fillMaxWidth()
            .background(ColorProvider(Color(0xFF2D2C2A)))
            .cornerRadius(10)
            .padding(androidx.glance.layout.padding(horizontal = 10, vertical = 6))
            .clickable(actionStartActivity<MainActivity>()),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = goal.emoji,
            style = TextStyle(fontSize = androidx.glance.unit.sp(18)),
            modifier = GlanceModifier.padding(end = 8.dp)
        )
        Column(modifier = GlanceModifier.defaultWeight()) {
            Text(
                text = goal.title,
                style = TextStyle(
                    color = ColorProvider(Color.White),
                    fontSize = androidx.glance.unit.sp(13),
                    fontWeight = FontWeight.Medium
                ),
                maxLines = 1
            )
            Text(
                text = if (goal.isCheckedToday) "✅ Done today"
                else "🔥 Streak: ${goal.currentStreak}",
                style = TextStyle(
                    color = ColorProvider(
                        if (goal.isCheckedToday) Color(0xFF81C784) else Color(0xFFFFA726)
                    ),
                    fontSize = androidx.glance.unit.sp(11)
                )
            )
        }
    }
}

private val Int.dp: androidx.glance.unit.Dp
    get() = androidx.glance.unit.Dp(this.toFloat())
