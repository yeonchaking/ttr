import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import androidx.navigation.NavHostController
import com.greentea.ttb.viewmodel.ExerciseViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ExerciseResultScreen(navController: NavHostController, viewModel: ExerciseViewModel) {
    val workoutRecord by viewModel.workoutRecord.collectAsState()
    val workoutSets by viewModel.workoutSets.collectAsState()
    val (sessionStartTime, sessionEndTime) = viewModel.getSessionTimes()

    val sdfDate = SimpleDateFormat("yyyy년 MM월 dd일 EEEE", Locale.getDefault())
    val sdfTime = SimpleDateFormat("a hh:mm", Locale.getDefault())

    val startTime = Date(sessionStartTime)
    val endTime = Date(sessionEndTime)
    val totalTime = (sessionEndTime - sessionStartTime) / (1000 * 60) // in minutes

    val scope = rememberCoroutineScope()
    var exerciseName by remember { mutableStateOf("") }

    LaunchedEffect(workoutRecord.exerciseId) {
        scope.launch {
            exerciseName = viewModel.getExerciseById(workoutRecord.exerciseId)?.name ?: "Unknown"
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = sdfDate.format(startTime))
        Text(text = "${sdfTime.format(startTime)} ~ ${sdfTime.format(endTime)} / 총 $totalTime 분")
        Text(text = "운동명: $exerciseName")

        workoutSets.forEach { workoutSet ->
            Text(text = "${workoutSet.number}세트: ${workoutSet.weight} kg ${workoutSet.reps}회")
        }

        Text(text = "총 볼륨: ${workoutRecord.totalVolume} kg")

        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                viewModel.saveWorkoutRecordToDatabase()
                navController.navigate("StartScreen")
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("저장하고 홈으로")
        }
    }
}
