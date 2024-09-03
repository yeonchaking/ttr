package com.greentea.ttb.di

import android.content.Context
import androidx.room.Room
import com.greentea.ttb.AppDatabase
import com.greentea.ttb.data.source.ExerciseDao
import com.greentea.ttb.data.source.WorkoutSetDao
import com.greentea.ttb.data.source.WorkoutRecordDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class) // SingletonComponent는 앱의 생명주기와 동일한 범위를 갖는 컴포넌트
object AppModule {

    @Provides // 의존성 제공
    @Singleton
    fun provideDatabase(@ApplicationContext appContext: Context): AppDatabase {
        return Room.databaseBuilder(
            appContext,
            AppDatabase::class.java,
            "app_database"
        ).build() // AppDatabase 를 생성해서 반환
    }

    @Provides
    fun provideExerciseDao(appDatabase: AppDatabase): ExerciseDao {
        return appDatabase.exerciseDao()
    }

    @Provides
    fun provideRecordDao(appDatabase: AppDatabase): WorkoutRecordDao {
        return appDatabase.workoutRecordDao()
    }

    @Provides
    fun provideSetDao(appDatabase: AppDatabase): WorkoutSetDao {
        return appDatabase.workoutSetDao()
    }
}

