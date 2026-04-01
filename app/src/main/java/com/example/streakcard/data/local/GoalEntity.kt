package com.example.streakcard.data.local


import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.streakcard.data.model.CardColorScheme
import com.example.streakcard.data.model.CardShape
import com.example.streakcard.data.model.CardSize
import com.example.streakcard.data.model.MissPolicy

@Entity(tableName = "goals")
data class GoalEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val emoji: String = "🎯",
    val colorScheme: CardColorScheme = CardColorScheme.OCEAN,
    val cardShape: CardShape = CardShape.ROUNDED,
    val cardSize: CardSize = CardSize.MEDIUM,
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val totalCheckIns: Int = 0,
    val lastCheckedDate: String? = null,      // ISO date string yyyy-MM-dd
    val createdAt: String,                     // ISO date string
    val missPolicy: MissPolicy = MissPolicy.RESET,
    val isPinned: Boolean = false,             // pinned to first page
    val isWidgetEnabled: Boolean = false,      // show in widget
    val reminderTime: String? = null,          // HH:mm for daily reminder
    val notes: String = ""
)
