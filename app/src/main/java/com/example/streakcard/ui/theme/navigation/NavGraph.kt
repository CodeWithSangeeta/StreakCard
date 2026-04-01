package com.example.streakcard.ui.theme.navigation


import androidx.compose.runtime.Composable
import androidx.navigation.*
import androidx.navigation.compose.*
import com.streakcard.ui.screens.addgoal.AddGoalScreen
import com.streakcard.ui.screens.detail.GoalDetailScreen
import com.streakcard.ui.screens.home.HomeScreen

sealed class Screen(val route: String) {
    object Home       : Screen("home")
    object AddGoal    : Screen("add_goal")
    object GoalDetail : Screen("goal_detail/{goalId}") {
        fun createRoute(goalId: Long) = "goal_detail/$goalId"
    }
}

@Composable
fun StreakCardNavGraph(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = Screen.Home.route) {

        composable(Screen.Home.route) {
            HomeScreen(
                onAddGoal  = { navController.navigate(Screen.AddGoal.route) },
                onGoalClick = { id -> navController.navigate(Screen.GoalDetail.createRoute(id)) }
            )
        }

        composable(Screen.AddGoal.route) {
            AddGoalScreen(onBack = { navController.popBackStack() })
        }

        composable(
            route = Screen.GoalDetail.route,
            arguments = listOf(navArgument("goalId") { type = NavType.LongType })
        ) {
            GoalDetailScreen(onBack = { navController.popBackStack() })
        }
    }
}
