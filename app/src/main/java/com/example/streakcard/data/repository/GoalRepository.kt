package com.example.streakcard.data.repository


import com.example.streakcard.domain.model.Goal
import kotlinx.coroutines.flow.Flow

interface GoalRepository {
    fun getAllGoals(): Flow<List<Goal>>
    fun getGoalById(id: Long): Flow<Goal?>
    fun getPinnedGoals(): Flow<List<Goal>>
    fun getWidgetGoals(): Flow<List<Goal>>
    suspend fun addGoal(goal: Goal): Long
    suspend fun updateGoal(goal: Goal)
    suspend fun deleteGoal(id: Long)
    suspend fun checkInGoal(id: Long)
}
