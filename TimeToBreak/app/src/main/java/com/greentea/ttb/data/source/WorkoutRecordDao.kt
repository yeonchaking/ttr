package com.greentea.ttb.data.source

import androidx.room.*
import com.greentea.ttb.data.model.WorkoutRecord

@Dao
interface WorkoutRecordDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertWorkoutRecord(workoutRecord: WorkoutRecord): Long

    @Update
    suspend fun updateWorkoutRecord(workoutRecord: WorkoutRecord)

    @Query("SELECT * FROM workout_record WHERE id = :id")
    suspend fun getWorkoutRecordById(id: Long): WorkoutRecord?

    @Query("SELECT * FROM workout_record")
    suspend fun getAllWorkoutRecords(): List<WorkoutRecord>
}