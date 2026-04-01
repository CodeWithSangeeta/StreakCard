package com.example.streakcard.domain.usecase


import com.example.streakcard.data.repository.GoalRepository
import com.example.streakcard.domain.model.Goal
import javax.inject.Inject

class UpdateGoalUseCase @Inject constructor(private val repository: GoalRepository) {
    suspend operator fun invoke(goal: Goal) = repository.updateGoal(goal)
}
