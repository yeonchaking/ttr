package com.greentea.ttb.di

import com.greentea.ttb.data.repository.exercise.ExerciseDataSource
import com.greentea.ttb.data.repository.exercise.ExerciseRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindExerciseRepository(
        exerciseDataSource: ExerciseDataSource
    ): ExerciseRepository
    // ExerciseDataSource를 ExerciseRepository로 바인딩해줌
}
