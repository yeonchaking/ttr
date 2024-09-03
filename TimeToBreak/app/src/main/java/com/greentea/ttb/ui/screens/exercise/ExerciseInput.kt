import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.greentea.ttb.ui.theme.color3
import com.greentea.ttb.viewmodel.ExerciseViewModel
import kotlinx.coroutines.launch
import android.util.Log

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseInput(navController: NavHostController, onDismiss: () -> Unit, viewModel: ExerciseViewModel) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedExercise by remember { mutableStateOf<String?>(null) }

    val exerciseNames by viewModel.exerciseNames.collectAsState()
    val filteredExercises = if (searchQuery.isBlank()) {
        exerciseNames
    } else {
        exerciseNames.filter { it.contains(searchQuery, ignoreCase = true) }
    }

    val scope = rememberCoroutineScope()

    Log.d("ExerciseInput", "Search Query: $searchQuery")
    Log.d("ExerciseInput", "Filtered Exercises: $filteredExercises")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "운동 검색") },
        text = {
            Column(modifier = Modifier.padding(16.dp)) {
                TextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("운동 검색") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Search
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp) // 여기서 높이를 고정
                        .background(color3)
                ) {
                    LazyColumn(modifier = Modifier.fillMaxWidth()) {
                        items(filteredExercises) { exercise ->
                            Log.d("ExerciseInput", "Exercise Item: $exercise")
                            Text(
                                text = exercise,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedExercise = exercise
                                        searchQuery = exercise
                                    }
                                    .padding(16.dp)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            selectedExercise?.let {
                Button(
                    onClick = {
                        scope.launch {
                            viewModel.updateExerciseName(it)
                            val exerciseId = viewModel.getExerciseIdByName(it)
                            if (exerciseId != null) {
                                navController.navigate("ExerciseScreen/$exerciseId")
                                onDismiss()
                            } else {
                                // Show error message: Invalid exercise name
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("운동 시작")
                }
            } ?: Text("운동을 선택하세요")
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("취소")
            }
        },
        modifier = Modifier.widthIn(max = 300.dp) // Adjust the width of the dialog
    )
}
