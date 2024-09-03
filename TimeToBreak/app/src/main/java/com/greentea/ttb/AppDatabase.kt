package com.greentea.ttb

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.greentea.ttb.data.model.*
import com.greentea.ttb.data.source.ExerciseDao
import com.greentea.ttb.data.source.WorkoutRecordDao
import com.greentea.ttb.data.source.WorkoutSetDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(entities = [Exercise::class, WorkoutRecord::class, WorkoutSet::class], version = 2, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun exerciseDao(): ExerciseDao
    abstract fun workoutRecordDao(): WorkoutRecordDao
    abstract fun workoutSetDao(): WorkoutSetDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                    .addMigrations(MIGRATION_1_2) // 마이그레이션 추가
                    .addCallback(AppDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // workout_record 테이블 마이그레이션
                database.execSQL("CREATE TABLE new_workout_record AS SELECT * FROM workout_record")
                database.execSQL("DROP TABLE workout_record")
                database.execSQL("""
                    CREATE TABLE workout_record (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        exerciseId INTEGER NOT NULL,
                        totalTime INTEGER NOT NULL,
                        totalVolume INTEGER NOT NULL,
                        startTime INTEGER NOT NULL,
                        endTime INTEGER NOT NULL,
                        FOREIGN KEY(exerciseId) REFERENCES exercise(id) ON DELETE CASCADE
                    )
                """)
                database.execSQL("INSERT INTO workout_record (id, exerciseId, totalTime, totalVolume, startTime, endTime) SELECT id, exerciseId, totalTime, totalVolume, startTime, endTime FROM new_workout_record")
                database.execSQL("DROP TABLE new_workout_record")
                database.execSQL("CREATE INDEX index_workout_record_exerciseId ON workout_record(exerciseId)")

                // workout_set 테이블 마이그레이션
                database.execSQL("CREATE TABLE new_workout_set AS SELECT id, recordId AS workoutRecordId, number, weight, reps, duration FROM workout_set")
                database.execSQL("DROP TABLE workout_set")
                database.execSQL("""
                    CREATE TABLE workout_set (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        workoutRecordId INTEGER NOT NULL,
                        number INTEGER NOT NULL,
                        weight INTEGER,
                        reps INTEGER,
                        duration INTEGER,
                        FOREIGN KEY(workoutRecordId) REFERENCES workout_record(id) ON DELETE CASCADE
                    )
                """)
                database.execSQL("INSERT INTO workout_set (id, workoutRecordId, number, weight, reps, duration) SELECT id, workoutRecordId, number, weight, reps, duration FROM new_workout_set")
                database.execSQL("DROP TABLE new_workout_set")

             }
        }

        private class AppDatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch {
                        populateDatabase(database.exerciseDao())
                    }
                }
            }

            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
                Log.d("AppDatabase", "Database onOpen called")
            }
        }

        suspend fun populateDatabase(exerciseDao: ExerciseDao) {
            val exercises = listOf(
                Exercise(id = 1, name = "벤치 프레스", type = ExerciseType.CHEST, breakTime = 120),
                Exercise(id = 2, name = "스쿼트", type = ExerciseType.LEG, breakTime = 120),
                Exercise(id = 3, name = "숄더 프레스", type = ExerciseType.SHOULDER, breakTime = 90),
                Exercise(id = 4, name = "인클라인 벤치 프레스", type = ExerciseType.CHEST, breakTime = 100),
                Exercise(id = 5, name = "데드리프트", type = ExerciseType.COMPOUND, breakTime = 90),
                Exercise(id = 6, name = "사이드 레터럴 레이즈", type = ExerciseType.SHOULDER, breakTime = 60),
                Exercise(id = 7, name = "바벨 로우", type = ExerciseType.BACK, breakTime = 90),
                Exercise(id = 8, name = "T바 로우", type = ExerciseType.BACK, breakTime = 90),
                Exercise(id = 9, name = "암 컬", type = ExerciseType.ARM, breakTime = 60),
                Exercise(id = 10, name = "버피", type = ExerciseType.CARDIO, breakTime = 60),
                Exercise(id = 11, name = "트레드밀 러닝", type = ExerciseType.CARDIO, breakTime = 120),
                Exercise(id = 12, name = "인클라인 트레드밀 러닝", type = ExerciseType.CARDIO, breakTime = 120),
                Exercise(id = 13, name = "풀 업", type = ExerciseType.BACK, breakTime = 90),
            )
            Log.d("AppDatabase", "Inserting exercises: $exercises")
            exerciseDao.insertAll(exercises)
            val insertedExercises = exerciseDao.getAllExercises()
            Log.d("AppDatabase", "Inserted exercises: $insertedExercises")
        }
    }
}
