package com.greentea.ttb.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "workout_record",
    foreignKeys = [ForeignKey(
        entity = Exercise::class,
        parentColumns = ["id"],
        childColumns = ["exerciseId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["exerciseId"])]
)
data class WorkoutRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    var exerciseId: Long,
    var totalTime: Int,
    var totalVolume: Int,
    var startTime: Long,
    var endTime: Long
)