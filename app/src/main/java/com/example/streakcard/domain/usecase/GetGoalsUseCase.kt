package com.example.streakcard.domain.usecase


package com.streakcard.domain.usecase

import com.streakcard.data.repository.GoalRepository
import com.streakcard.domain.model.Goal
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetGoalsUseCase @Inject constructor(private val repository: GoalRepository) {
    operator fun invoke(): Flow<List<Goal>> = repository.getAllGoals()
}

class GetGoalByIdUseCase @Inject constructor(private val repository: GoalRepository) {
    operator fun invoke(id: Long): Flow<Goal?> = repository.getGoalById(id)
}

class GetPinnedGoalsUseCase @Inject constructor(private val repository: GoalRepository) {
    operator fun invoke(): Flow<List<Goal>> = repository.getPinnedGoals()
}
