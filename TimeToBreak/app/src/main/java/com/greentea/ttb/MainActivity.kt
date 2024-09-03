package com.greentea.ttb

import ExerciseViewModelFactory
import MainScreen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModelProvider
import com.greentea.ttb.data.repository.exercise.ExerciseRepository
import com.greentea.ttb.ui.screens.common.SplashScreen
import com.greentea.ttb.ui.theme.TimeToBreakTheme
import com.greentea.ttb.viewmodel.ExerciseViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    private val applicationScope = CoroutineScope(SupervisorJob())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = AppDatabase.getDatabase(this, applicationScope)

        val exerciseRepository = ExerciseRepository(
            database.exerciseDao(),
            database.workoutRecordDao(),
            database.workoutSetDao()
        )

        val viewModelFactory = ExerciseViewModelFactory(exerciseRepository)
        val viewModel = ViewModelProvider(this, viewModelFactory)
            .get(ExerciseViewModel::class.java)

        setContent {
            TimeToBreakTheme {
                var showSplash by remember { mutableStateOf(true) }

                LaunchedEffect(Unit) {
                    delay(2000) // 3초 동안 스플래쉬 화면을 표시
                    showSplash = false
                }

                if (showSplash) {
                    SplashScreen()
                } else {
                    MainScreen(viewModel)
                }
            }
        }
    }
}
