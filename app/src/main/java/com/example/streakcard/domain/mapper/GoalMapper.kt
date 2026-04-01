package com.example.streakcard.domain.mapper

import com.example.streakcard.data.local.GoalEntity
import com.example.streakcard.domain.model.Goal
import java.time.LocalDate

fun GoalEntity.toDomain(): Goal = Goal(
    id = id,
    title = title,
    emoji = emoji,
    colorScheme = colorScheme,
    cardShape = cardShape,
    cardSize = cardSize,
    currentStreak = currentStreak,
    longestStreak = longestStreak,
    totalCheckIns = totalCheckIns,
    lastCheckedDate = lastCheckedDate?.let { LocalDate.parse(it) },
    createdAt = LocalDate.parse(createdAt),
    missPolicy = missPolicy,
    isPinned = isPinned,
    isWidgetEnabled = isWidgetEnabled,
    reminderTime = reminderTime,
    notes = notes
)

fun Goal.toEntity(): GoalEntity = GoalEntity(
    id = id,
    title = title,
    emoji = emoji,
    colorScheme = colorScheme,
    cardShape = cardShape,
    cardSize = cardSize,
    currentStreak = currentStreak,
    longestStreak = longestStreak,
    totalCheckIns = totalCheckIns,
    lastCheckedDate = lastCheckedDate?.toString(),
    createdAt = createdAt.toString(),
    missPolicy = missPolicy,
    isPinned = isPinned,
    isWidgetEnabled = isWidgetEnabled,
    reminderTime = reminderTime,
    notes = notes
)
