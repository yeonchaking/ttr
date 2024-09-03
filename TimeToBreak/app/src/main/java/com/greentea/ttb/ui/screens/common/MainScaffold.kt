import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.greentea.ttb.viewmodel.ExerciseViewModel
import com.greentea.ttb.ui.screens.lobby.CalendarScreen
import com.greentea.ttb.ui.screens.lobby.MypageScreen
import com.greentea.ttb.ui.theme.color2
import com.greentea.ttb.ui.theme.color4
import com.guru.fontawesomecomposelib.FaIcon
import com.guru.fontawesomecomposelib.FaIcons

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScaffold(viewModel: ExerciseViewModel) {
    val scope = rememberCoroutineScope()
    val navController = rememberNavController()
    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry.value?.destination?.route

    Scaffold(
        topBar = {
            if (currentRoute != "ExerciseScreen") {
                TopAppBar(
                    title = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            FaIcon(
                                faIcon = FaIcons.CalendarAlt,
                                tint = color4,
                                modifier = Modifier
                                    .padding(20.dp)
                                    .clickable {
                                        navController.navigate("CalendarScreen")
                                    }
                            )
                            FaIcon(
                                faIcon = FaIcons.Home,
                                tint = color4,
                                modifier = Modifier
                                    .padding(20.dp)
                                    .clickable {
                                        navController.navigate("StartScreen")
                                    }
                            )
                            FaIcon(
                                faIcon = FaIcons.User,
                                tint = color4,
                                modifier = Modifier
                                    .padding(20.dp)
                                    .clickable {
                                        navController.navigate("MypageScreen")
                                    }
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = color2
                    )
                )
            }
        },
        content = { paddingValues ->
            NavHost(
                navController = navController,
                startDestination = "StartScreen",
                modifier = Modifier.padding(paddingValues)
            ) {
                composable("StartScreen") { StartScreen(navController, viewModel) }
                composable("CalendarScreen") { CalendarScreen(viewModel) }
                composable("MypageScreen") { MypageScreen() }
                composable(
                    route = "ExerciseScreen/{exerciseId}",
                    arguments = listOf(navArgument("exerciseId") { type = NavType.LongType })
                ) { backStackEntry ->
                    val exerciseId = backStackEntry.arguments?.getLong("exerciseId") ?: 0L
                    ExerciseScreen(navController, viewModel, exerciseId)
                }
                composable("ExerciseResultScreen") { ExerciseResultScreen(navController, viewModel) }
                // Add more screens here
            }
        }
    )
}
