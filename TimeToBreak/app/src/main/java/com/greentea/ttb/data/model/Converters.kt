package com.greentea.ttb.data.model

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun fromExerciseType(value: ExerciseType): String {
        return value.name
    }

    @TypeConverter
    fun toExerciseType(value: String): ExerciseType {
        return ExerciseType.valueOf(value)
    }
}