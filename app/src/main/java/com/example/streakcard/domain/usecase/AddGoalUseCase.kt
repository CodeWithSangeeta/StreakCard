package com.example.streakcard.domain.usecase



import com.streakcard.data.repository.GoalRepository
import com.streakcard.domain.model.Goal
import javax.inject.Inject

class AddGoalUseCase @Inject constructor(private val repository: GoalRepository) {
    suspend operator fun invoke(goal: Goal): Long = repository.addGoal(goal)
}
