package com.greentea.ttb.data.source

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.greentea.ttb.data.model.Exercise


@Dao
interface ExerciseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExercise(exercise: Exercise): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(exercises: List<Exercise>)

    @Query("SELECT * FROM exercise WHERE id = :id")
    suspend fun getExerciseById(id: Long): Exercise?

    @Query("SELECT * FROM exercise")
    suspend fun getAllExercises(): List<Exercise>

    @Query("SELECT * FROM exercise WHERE name = :name LIMIT 1")
    suspend fun getExerciseByName(name: String): Exercise?
}