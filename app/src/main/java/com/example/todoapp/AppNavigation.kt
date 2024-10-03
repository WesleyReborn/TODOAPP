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
    // Define o NavHost, que gerencia a navegação entre as diferentes páginas do app
    // O "startDestination" define a tela inicial do app, que é a tela de login
    NavHost(navController, startDestination = "login") {
        // Rota para a tela de login
        composable("login") {
            // Passa o navController e authViewModel para a tela de LoginPage
            LoginPage(navController = navController, authViewModel = authViewModel)
        }
        // Rota para a tela de cadastro
        composable("signup") {
            // Passa o navController e authViewModel para a tela de SignupPage
            SignupPage(navController = navController, authViewModel = authViewModel)
        }
        // Rota para a tela inicial (HomePage), utilizando o argumento "user"
        composable("home/{user}") { backStackEntry ->
            // Obtém o valor do argumento "user" da backStackEntry ou usa o valor padrão passado na função
            val user = backStackEntry.arguments?.getString("user") ?: user
            // Passa o navController, taskViewModel, authViewModel e user para a HomePage
            HomePage(
                navController = navController,
                taskViewModel = taskViewModel,
                authViewModel = authViewModel,
                user = user,
                // Função para lidar com o logout, passada como callback
                onLogout = onLogout
            )
        }
        // Rota para a tela de adicionar tarefas (AddTaskPage), passando o argumento "user"
        composable("add_task/{user}") { backStackEntry ->
            // Obtém o valor do argumento "user" da backStackEntry ou usa o valor padrão
            val user = backStackEntry.arguments?.getString("user") ?: user
            // Passa o navController, taskViewModel e user para a AddTaskPage
            AddTaskPage(navController = navController, taskViewModel = taskViewModel, user = user)
        }
        // Rota para a tela de editar tarefas (EditTaskPage), passando os argumentos "taskId" e "user"
        composable("edit_task/{taskId}/{user}") { backStackEntry ->
            // Obtém o valor do argumento "taskId" da backStackEntry ou usa uma string vazia se não houver valor
            val taskId = backStackEntry.arguments?.getString("taskId") ?: ""
            // Obtém o valor do argumento "user" da backStackEntry ou usa o valor padrão
            val user = backStackEntry.arguments?.getString("user") ?: user
            // Passa o navController, taskId, taskViewModel e user para a EditTaskPage
            EditTaskPage(
                navController = navController,
                taskId = taskId,
                taskViewModel = taskViewModel,
                user = user
            )
        }
    }
}
