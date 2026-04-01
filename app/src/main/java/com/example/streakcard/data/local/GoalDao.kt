package com.example.streakcard.data.local


import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface GoalDao {

    @Query("SELECT * FROM goals ORDER BY isPinned DESC, createdAt DESC")
    fun getAllGoals(): Flow<List<GoalEntity>>

    @Query("SELECT * FROM goals WHERE id = :id")
    fun getGoalById(id: Long): Flow<GoalEntity?>

    @Query("SELECT * FROM goals WHERE isPinned = 1 ORDER BY createdAt DESC")
    fun getPinnedGoals(): Flow<List<GoalEntity>>

    @Query("SELECT * FROM goals WHERE isWidgetEnabled = 1")
    fun getWidgetGoals(): Flow<List<GoalEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoal(goal: GoalEntity): Long

    @Update
    suspend fun updateGoal(goal: GoalEntity)

    @Delete
    suspend fun deleteGoal(goal: GoalEntity)

    @Query("DELETE FROM goals WHERE id = :id")
    suspend fun deleteGoalById(id: Long)

    @Query("UPDATE goals SET currentStreak = :streak, longestStreak = :longest, totalCheckIns = :total, lastCheckedDate = :date WHERE id = :id")
    suspend fun updateStreak(id: Long, streak: Int, longest: Int, total: Int, date: String)
}
