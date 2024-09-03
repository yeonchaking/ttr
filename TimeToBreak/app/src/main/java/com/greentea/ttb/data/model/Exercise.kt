package com.greentea.ttb.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "exercise")
data class Exercise(
    @PrimaryKey var id: Long,  // autoGenerate 옵션 제거
    @ColumnInfo(name = "name") var name: String,
    @ColumnInfo(name = "type") var type: ExerciseType,
    @ColumnInfo(name = "break_time") var breakTime: Int
)