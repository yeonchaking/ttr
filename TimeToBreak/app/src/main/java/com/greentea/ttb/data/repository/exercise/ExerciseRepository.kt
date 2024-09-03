package com.greentea.ttb.data.repository.exercise

import com.greentea.ttb.data.model.Exercise
import com.greentea.ttb.data.model.WorkoutRecord
import com.greentea.ttb.data.model.WorkoutSet
import com.greentea.ttb.data.source.ExerciseDao
import com.greentea.ttb.data.source.WorkoutRecordDao
import com.greentea.ttb.data.source.WorkoutSetDao
import android.util.Log
import kotlinx.coroutines.flow.Flow

class ExerciseRepository(
    private val exerciseDao: ExerciseDao,
    private val workoutRecordDao: WorkoutRecordDao,
    private val workoutSetDao: WorkoutSetDao
) {
    suspend fun insertExercise(exercise: Exercise): Long = exerciseDao.insertExercise(exercise)
    suspend fun getExerciseById(id: Long) = exerciseDao.getExerciseById(id)
    suspend fun getAllExercises(): List<Exercise> {
        val exercises = exerciseDao.getAllExercises()
        Log.d("ExerciseRepository", "Fetched exercises: $exercises")
        return exercises
    }
    suspend fun getExerciseByName(name: String): Exercise? = exerciseDao.getExerciseByName(name)
    suspend fun insertWorkoutRecord(workoutRecord: WorkoutRecord): Long = workoutRecordDao.insertWorkoutRecord(workoutRecord)
    suspend fun updateWorkoutRecord(workoutRecord: WorkoutRecord) = workoutRecordDao.updateWorkoutRecord(workoutRecord)
    suspend fun getWorkoutRecordById(id: Long) = workoutRecordDao.getWorkoutRecordById(id)
    suspend fun getAllWorkoutRecords(): List<WorkoutRecord> = workoutRecordDao.getAllWorkoutRecords()
    suspend fun insertWorkoutSet(workoutSet: WorkoutSet) = workoutSetDao.insertWorkoutSet(workoutSet)
    suspend fun getWorkoutSetByWorkoutRecordId(workoutRecordId: Long) = workoutSetDao.getWorkoutSetByWorkoutRecordId(workoutRecordId)
    fun getAllWorkoutSets(): Flow<List<WorkoutSet>> {
        return workoutSetDao.getAllWorkoutSets()
    }
}
