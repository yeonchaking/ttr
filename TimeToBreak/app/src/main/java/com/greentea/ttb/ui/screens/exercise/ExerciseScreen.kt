import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.greentea.ttb.viewmodel.ExerciseViewModel
import com.greentea.ttb.data.model.WorkoutSet
import kotlinx.coroutines.delay

@Composable
fun ExerciseScreen(navController: NavHostController, viewModel: ExerciseViewModel, exerciseId: Long) {
    var weight by remember { mutableStateOf("") }
    var reps by remember { mutableStateOf("") }
    var countdown by remember { mutableStateOf(2) }
    var isExercising by remember { mutableStateOf(false) }
    var isResting by remember { mutableStateOf(false) }
    var startCountdown by remember { mutableStateOf(true) }

    LaunchedEffect(exerciseId) {
        if (viewModel.currentWorkoutRecordId == null) {
            viewModel.createNewWorkoutRecord(exerciseId)
        }
    }

    LaunchedEffect(startCountdown) {
        if (startCountdown) {
            countdown = 2
            while (countdown > 0) {
                delay(1000L)
                countdown--
            }
            isExercising = true
            startCountdown = false
        }
    }

    LaunchedEffect(isExercising) {
        if (isExercising && countdown > 0) {
            while (countdown > 0) {
                delay(1000L)
                countdown--
            }
            isExercising = false
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        if (startCountdown || (isExercising && countdown > 0)) {
            Text(text = "카운트다운: $countdown")
        } else {
            if (isResting) {
                OutlinedTextField(
                    value = weight,
                    onValueChange = { weight = it },
                    label = { Text("무게") },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = reps,
                    onValueChange = { reps = it },
                    label = { Text("횟수") },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        viewModel.currentWorkoutRecordId?.let { workoutRecordId ->
                            val workoutSet = WorkoutSet(workoutRecordId = workoutRecordId, number = viewModel.workoutSets.value.size + 1, weight = weight.toInt(), reps = reps.toInt(), duration = 0)
                            viewModel.addWorkoutSet(workoutSet)
                            weight = ""
                            reps = ""
                            isResting = false
                            startCountdown = true
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("한 세트 더")
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        viewModel.finalizeCurrentWorkoutRecord(weight.toInt(), reps.toInt())
                        navController.navigate("ExerciseResultScreen")
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("운동 종료")
                }
            } else {
                Text(text = "운동 중...")
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        isResting = true
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("휴식")
                }
            }
        }
    }
}
