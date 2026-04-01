package com.example.streakcard.data.repository


package com.streakcard.data.repository

import com.streakcard.data.local.GoalDao
import com.streakcard.domain.mapper.toDomain
import com.streakcard.domain.mapper.toEntity
import com.streakcard.domain.model.Goal
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GoalRepositoryImpl @Inject constructor(
    private val dao: GoalDao
) : GoalRepository {

    override fun getAllGoals(): Flow<List<Goal>> =
        dao.getAllGoals().map { list -> list.map { it.toDomain() } }

    override fun getGoalById(id: Long): Flow<Goal?> =
        dao.getGoalById(id).map { it?.toDomain() }

    override fun getPinnedGoals(): Flow<List<Goal>> =
        dao.getPinnedGoals().map { list -> list.map { it.toDomain() } }

    override fun getWidgetGoals(): Flow<List<Goal>> =
        dao.getWidgetGoals().map { list -> list.map { it.toDomain() } }

    override suspend fun addGoal(goal: Goal): Long =
        dao.insertGoal(goal.toEntity())

    override suspend fun updateGoal(goal: Goal) =
        dao.updateGoal(goal.toEntity())

    override suspend fun deleteGoal(id: Long) =
        dao.deleteGoalById(id)

    override suspend fun checkInGoal(id: Long) {
        val entity = dao.getGoalById(id).first() ?: return
        val today = LocalDate.now()
        val lastDate = entity.lastCheckedDate?.let { LocalDate.parse(it) }

        if (lastDate == today) return // already checked in today

        val yesterday = today.minusDays(1)
        val newStreak = when {
            lastDate == yesterday -> entity.currentStreak + 1
            entity.missPolicy.name == "CONTINUE" && lastDate != null -> entity.currentStreak + 1
            else -> 1
        }
        val newLongest = maxOf(entity.longestStreak, newStreak)
        dao.updateStreak(id, newStreak, newLongest, entity.totalCheckIns + 1, today.toString())
    }
}
