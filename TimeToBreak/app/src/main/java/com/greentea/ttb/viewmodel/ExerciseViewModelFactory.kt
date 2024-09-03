import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.greentea.ttb.data.repository.exercise.ExerciseRepository
import com.greentea.ttb.viewmodel.ExerciseViewModel

class ExerciseViewModelFactory(
    private val exerciseRepository: ExerciseRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ExerciseViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ExerciseViewModel(exerciseRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
