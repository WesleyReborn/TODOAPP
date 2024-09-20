@file:Suppress("NAME_SHADOWING")

package com.example.todoapp

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.todoapp.pages.AddTaskPage
import com.example.todoapp.pages.EditTaskPage
import com.example.todoapp.pages.HomePage
import com.example.todoapp.pages.LoginPage
import com.example.todoapp.pages.SignupPage
import com.example.todoapp.ui.TaskViewModel

@Composable
fun AppNavigation(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    taskViewModel: TaskViewModel,
    user: String,
    onLogout: () -> Unit
) {
    // Define o NavHost com o controlador de navegação e a tela inicial
    NavHost(navController, startDestination = "login") {
        // Define a rota para a tela de login
        composable("login") {
            LoginPage(navController = navController, authViewModel = authViewModel)
        }
        // Define a rota para a tela de cadastro
        composable("signup") {
            SignupPage(navController = navController, authViewModel = authViewModel)
        }
        // Define a rota para a tela inicial, passando o usuário como argumento
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
        // Define a rota para a tela de adicionar tarefa, passando o usuário como argumento
        composable("add_task/{user}") { backStackEntry ->
            val user = backStackEntry.arguments?.getString("user") ?: user
            AddTaskPage(navController = navController, taskViewModel = taskViewModel, user = user)
        }
        // Define a rota para a tela de editar tarefa, passando o ID da tarefa e o usuário como argumentos
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
