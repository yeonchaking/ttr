package com.greentea.ttb.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "workout_set",
    foreignKeys = [ForeignKey(
        entity = WorkoutRecord::class,
        parentColumns = ["id"],
        childColumns = ["workoutRecordId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class WorkoutSet(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    var workoutRecordId: Long,
    var number: Int,
    var weight: Int?,
    var reps: Int?,
    var duration: Int?
)
