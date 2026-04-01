package com.example.streakcard.domain.usecase


package com.streakcard.domain.usecase

import com.streakcard.data.repository.GoalRepository
import javax.inject.Inject

class CheckInGoalUseCase @Inject constructor(private val repository: GoalRepository) {
    suspend operator fun invoke(goalId: Long) = repository.checkInGoal(goalId)
}
