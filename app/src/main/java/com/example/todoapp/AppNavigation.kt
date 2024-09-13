package com.example.todoapp

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.firebaseauthlogin.AuthViewModel
import com.example.todoapp.data.TaskRepository
import com.example.todoapp.pages.AddTaskPage
import com.example.todoapp.pages.EditTaskPage
import com.example.todoapp.pages.HomePage
import com.example.todoapp.pages.LoginPage
import com.example.todoapp.pages.SignupPage
import com.example.todoapp.ui.TaskViewModel


@Composable
fun AppNavigation(modifier: Modifier = Modifier,
                  navController: NavHostController,
                  authViewModel: AuthViewModel,
                  taskViewModel: TaskViewModel,
                  user : String,
                  onLogout : () -> Unit) {

    NavHost(navController, startDestination = "login") {
        composable("login") {
            LoginPage(navController = navController, authViewModel = authViewModel)
        }
        composable("signup") {
            SignupPage(navController = navController, authViewModel = authViewModel)
        }
        composable("home/{user}") { backStackEntry ->
            val user = backStackEntry.arguments?.getString("user") ?: user
            HomePage(
                navController = navController,
                taskViewModel = taskViewModel,
                authViewModel = authViewModel,
                user = user,
                onLogout = onLogout
            )
        }
        composable("add_task/{user}") { backStackEntry ->
            val user = backStackEntry.arguments?.getString("user") ?: user
            AddTaskPage(navController = navController, taskViewModel = taskViewModel, user = user)
        }
        composable("edit_task/{taskId}/{user}") { backStackEntry ->
            val taskId = backStackEntry.arguments?.getString("taskId") ?: ""
            val user = backStackEntry.arguments?.getString("user") ?: user
            EditTaskPage(
                navController = navController,
                taskId = taskId,
                taskViewModel = taskViewModel,
                user = user
            )
        }
    }
}
