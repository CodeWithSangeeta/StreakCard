package com.example.streakcard.domain.model


package com.streakcard.domain.model

import com.streakcard.data.model.CardColorScheme
import com.streakcard.data.model.CardShape
import com.streakcard.data.model.CardSize
import com.streakcard.data.model.MissPolicy
import java.time.LocalDate

data class Goal(
    val id: Long = 0,
    val title: String,
    val emoji: String = "🎯",
    val colorScheme: CardColorScheme = CardColorScheme.OCEAN,
    val cardShape: CardShape = CardShape.ROUNDED,
    val cardSize: CardSize = CardSize.MEDIUM,
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val totalCheckIns: Int = 0,
    val lastCheckedDate: LocalDate? = null,
    val createdAt: LocalDate = LocalDate.now(),
    val missPolicy: MissPolicy = MissPolicy.RESET,
    val isPinned: Boolean = false,
    val isWidgetEnabled: Boolean = false,
    val reminderTime: String? = null,
    val notes: String = ""
) {
    val isCheckedToday: Boolean
        get() = lastCheckedDate == LocalDate.now()

    val isAtRisk: Boolean
        get() {
            val yesterday = LocalDate.now().minusDays(1)
            return lastCheckedDate != null && lastCheckedDate == yesterday && !isCheckedToday
        }

    val streakStatus: StreakStatus
        get() = when {
            isCheckedToday -> StreakStatus.DONE_TODAY
            currentStreak == 0 -> StreakStatus.NOT_STARTED
            isAtRisk -> StreakStatus.AT_RISK
            else -> StreakStatus.ACTIVE
        }
}

enum class StreakStatus { NOT_STARTED, ACTIVE, DONE_TODAY, AT_RISK }
