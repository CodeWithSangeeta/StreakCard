package com.example.streakcard.domain.usecase

import com.example.streakcard.data.repository.GoalRepository

import javax.inject.Inject

class DeleteGoalUseCase @Inject constructor(private val repository: GoalRepository) {
    suspend operator fun invoke(goalId: Long) = repository.deleteGoal(goalId)
}
