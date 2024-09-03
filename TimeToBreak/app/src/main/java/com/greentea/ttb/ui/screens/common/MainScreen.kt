import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import com.greentea.ttb.viewmodel.ExerciseViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: ExerciseViewModel) {
       MainScaffold(viewModel)
}
