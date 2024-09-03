package com.greentea.ttb.data.source

import androidx.room.*
import com.greentea.ttb.data.model.WorkoutSet
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutSetDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertWorkoutSet(workoutSet: WorkoutSet)

    @Query("SELECT * FROM workout_set WHERE workoutRecordId = :workoutRecordId")
    suspend fun getWorkoutSetByWorkoutRecordId(workoutRecordId: Long): List<WorkoutSet>

    @Query("SELECT * FROM workout_set")
    fun getAllWorkoutSets(): Flow<List<WorkoutSet>>
}