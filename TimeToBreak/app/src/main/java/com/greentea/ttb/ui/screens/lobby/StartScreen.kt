import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.airbnb.lottie.compose.*
import com.greentea.ttb.ui.theme.color3
import com.greentea.ttb.ui.theme.color4
import com.greentea.ttb.viewmodel.ExerciseViewModel

@Composable
fun StartScreen(navController: NavHostController, viewModel: ExerciseViewModel) {
    var showDialog by remember { mutableStateOf(false) }
    val todayExercises by viewModel.todayExercises.collectAsState()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        LottieAnimationScreen() // Lottie 애니메이션을 상단에 배치

        Spacer(modifier = Modifier.height(16.dp)) // 애니메이션과 다른 콘텐츠 사이에 간격 추가

        Box(
            modifier = Modifier
                .fillMaxWidth(0.8f) // 가로 너비를 줄임
                .padding(16.dp)
                .background(color3, RoundedCornerShape(8.dp))
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            if (todayExercises.isEmpty()) {
                Text(text = "오늘의 운동을 시작하세요", fontSize = 20.sp, color = color4)
            } else {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "오늘의 운동", fontSize = 24.sp, color = color4)
                    Spacer(modifier = Modifier.height(8.dp))
                    todayExercises.forEach { (exercise, count) ->
                        Text(text = "$exercise: ${count}세트", fontSize = 18.sp, color = color4)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp)) // 추가적인 간격

        Button(
            onClick = { showDialog = true }, // Show dialog on button click
            modifier = Modifier.size(200.dp, 50.dp) // Adjust the size as needed
        ) {
            Text(text = "운동하기", fontSize = 18.sp)
        }

        if (showDialog) {
            ExerciseInput(navController = navController, onDismiss = { showDialog = false }, viewModel = viewModel)
        }
    }
}

@Composable
fun LottieAnimationScreen() {
    val composition by rememberLottieComposition(LottieCompositionSpec.Asset("exAnimation.json"))
    val progress by animateLottieCompositionAsState(composition, iterations = LottieConstants.IterateForever)

    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        LottieAnimation(
            composition = composition,
            progress = { progress },
            modifier = Modifier.size(300.dp)
        )
    }
}
