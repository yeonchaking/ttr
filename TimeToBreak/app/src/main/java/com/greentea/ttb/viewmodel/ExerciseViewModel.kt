package com.greentea.ttb.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.greentea.ttb.data.model.Exercise
import com.greentea.ttb.data.model.WorkoutRecord
import com.greentea.ttb.data.model.WorkoutSet
import com.greentea.ttb.data.repository.exercise.ExerciseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ExerciseViewModel(private val exerciseRepository: ExerciseRepository) : ViewModel() {


    private val _workoutRecord = MutableStateFlow(WorkoutRecord(exerciseId = 0, totalTime = 0, totalVolume = 0, startTime = 0, endTime = 0))
    val workoutRecord: StateFlow<WorkoutRecord> get() = _workoutRecord

    private val _workoutSets = MutableStateFlow<List<WorkoutSet>>(emptyList())
    val workoutSets: StateFlow<List<WorkoutSet>> get() = _workoutSets

    private val _exerciseNames = MutableStateFlow(listOf<String>())
    val exerciseNames: StateFlow<List<String>> get() = _exerciseNames

    private val _todayExercises = MutableStateFlow<Map<String, Int>>(emptyMap())
    val todayExercises: StateFlow<Map<String, Int>> get() = _todayExercises

    private val _allWorkoutRecords = MutableStateFlow<List<WorkoutRecord>>(emptyList())
    val allWorkoutRecords: StateFlow<List<WorkoutRecord>> get() = _allWorkoutRecords

    val allWorkoutSets: StateFlow<List<WorkoutSet>> = exerciseRepository.getAllWorkoutSets().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    var currentWorkoutRecordId: Long? = null
        private set

    private var sessionStartTime: Long = 0
    private var sessionEndTime: Long = 0

    init {
        loadExerciseNames()
        loadAllWorkoutRecords()
        loadTodayExercises()
    }

    private fun loadExerciseNames() {
        viewModelScope.launch {
            val exercises = exerciseRepository.getAllExercises()
            _exerciseNames.value = exercises.map { it.name }
            Log.d("ExerciseViewModel", "Loaded exercises: $exercises")
        }
    }

    private fun loadTodayExercises() {
        viewModelScope.launch {
            val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val records = exerciseRepository.getAllWorkoutRecords()
            val todayRecords = records.filter {
                val recordDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(it.startTime))
                recordDate == today
            }
            val exerciseCount = mutableMapOf<String, Int>()
            todayRecords.forEach { workoutRecord ->
                Log.d("ExerciseViewModel", "종목 카운트: $workoutRecord")
                val sets = exerciseRepository.getWorkoutSetByWorkoutRecordId(workoutRecord.id)
                val exercise = exerciseRepository.getExerciseById(workoutRecord.exerciseId)
                Log.d("ExerciseViewModel", "set: $sets")
                exercise?.let {
                    exerciseCount[it.name] = exerciseCount.getOrDefault(it.name, 0) + sets.size
                }
            }

            Log.d("ExerciseViewModel", "Today Record: $exerciseCount")
            _todayExercises.value = exerciseCount
        }
    }

    private fun loadAllWorkoutRecords() {
        viewModelScope.launch {
            val workoutRecords = exerciseRepository.getAllWorkoutRecords()
            Log.d("ExerciseViewModel", "Loaded records: $workoutRecords")
            _allWorkoutRecords.value = workoutRecords
        }
    }

    fun updateExerciseName(name: String) {
        viewModelScope.launch {
            val exercise = exerciseRepository.getExerciseByName(name)
            exercise?.let {
                _workoutRecord.value = _workoutRecord.value.copy(exerciseId = it.id)
                Log.d("ExerciseViewModel", "Updated exercise: $exercise")
            }
        }
    }

    suspend fun getExerciseIdByName(name: String): Long? {
        val exercise = exerciseRepository.getExerciseByName(name)
        return exercise?.id
    }

    fun addWorkoutSet(workoutSet: WorkoutSet) {
        _workoutSets.value += workoutSet
        // 총 볼륨 업데이트
        _workoutRecord.value = _workoutRecord.value.copy(
            totalVolume = _workoutRecord.value.totalVolume + (workoutSet.weight ?: 0) * (workoutSet.reps ?: 0)
        )
    }

    fun createNewWorkoutRecord(exerciseId: Long) {
        viewModelScope.launch {
            val currentTime = System.currentTimeMillis()
            val newWorkoutRecord = WorkoutRecord(
                exerciseId = exerciseId,
                totalTime = 0,
                totalVolume = 0,
                startTime = currentTime,
                endTime = currentTime
            )
            currentWorkoutRecordId = exerciseRepository.insertWorkoutRecord(newWorkoutRecord)
            _workoutRecord.value = newWorkoutRecord.copy(id = currentWorkoutRecordId!!)
            _workoutSets.value = emptyList()
            sessionStartTime = currentTime
            Log.d("ExerciseViewModel", "Created new workout record: $_workoutRecord")
        }
    }

    fun finalizeCurrentWorkoutRecord(weight: Int, reps: Int) {
        viewModelScope.launch {
            val currentTime = System.currentTimeMillis()
            currentWorkoutRecordId?.let { workoutRecordId ->
                // 마지막 세트를 추가
                val finalSet = WorkoutSet(
                    workoutRecordId = workoutRecordId,
                    number = _workoutSets.value.size + 1,
                    weight = weight,
                    reps = reps,
                    duration = 0
                )
                addWorkoutSet(finalSet)

                val updatedWorkoutRecord = _workoutRecord.value.copy(
                    totalTime = ((currentTime - _workoutRecord.value.startTime) / (1000 * 60)).toInt(),
                    endTime = currentTime
                )
                sessionEndTime = currentTime
                Log.d("ExerciseViewModel", "finalizeCurrentWorkoutRecord: $updatedWorkoutRecord")
                _workoutRecord.value = updatedWorkoutRecord
                exerciseRepository.updateWorkoutRecord(updatedWorkoutRecord)
                _workoutSets.value.forEach { workoutSet ->
                    exerciseRepository.insertWorkoutSet(workoutSet.copy(workoutRecordId = workoutRecordId))
                }
                Log.d("ExerciseViewModel", "finalizeCurrentWorkoutRecord: 여기서 추가")
                loadTodayExercises()
            }
        }
    }

    fun saveWorkoutRecordToDatabase() {
        viewModelScope.launch {
            currentWorkoutRecordId?.let { workoutRecordId ->
                exerciseRepository.updateWorkoutRecord(_workoutRecord.value.copy(id = workoutRecordId))
                _workoutSets.value.forEach { workoutSet ->
                    exerciseRepository.insertWorkoutSet(workoutSet.copy(workoutRecordId = workoutRecordId))
                }
                val savedWorkoutRecord = exerciseRepository.getWorkoutRecordById(workoutRecordId)
                val savedWorkoutSets = exerciseRepository.getWorkoutSetByWorkoutRecordId(workoutRecordId)
                Log.d("ExerciseViewModel", "Saved Workout record: $savedWorkoutRecord")
                Log.d("ExerciseViewModel", "Saved Workout sets: $savedWorkoutSets")
                resetCurrentWorkoutRecord()
                Log.d("ExerciseViewModel", "saveWorkoutRecordToDatabase: 여기서 추가")
                loadTodayExercises()
            }
        }
    }

    private fun resetCurrentWorkoutRecord() {
        currentWorkoutRecordId = null
        _workoutRecord.value = WorkoutRecord(exerciseId = 0, totalTime = 0, totalVolume = 0, startTime = 0, endTime = 0)
        _workoutSets.value = emptyList()
        sessionStartTime = 0
        sessionEndTime = 0
    }

    suspend fun getExerciseById(exerciseId: Long): Exercise? {
        return exerciseRepository.getExerciseById(exerciseId)
    }

    fun getSessionTimes(): Pair<Long, Long> {
        return Pair(sessionStartTime, sessionEndTime)
    }
}