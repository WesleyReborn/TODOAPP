package com.example.todoapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.todoapp.data.FirestoreRepository
import com.example.todoapp.data.TaskRepository
import com.example.todoapp.data.TodoAppDatabase
import com.example.todoapp.ui.TaskViewModel
import com.example.todoapp.ui.TaskViewModelFactory
import com.example.todoapp.ui.theme.TODOAppTheme
import com.example.todoapp.workers.TaskSyncWorker

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inicializa o TaskRepository
        val taskRepository = TaskRepository(
            taskDao = TodoAppDatabase.getDatabase(applicationContext).taskDao(),
            firestoreRepository = FirestoreRepository(),
            context = applicationContext
        )
        // Cria um factory para TaskViewModel
        val taskViewModelFactory = TaskViewModelFactory(taskRepository)
        // Inicializa o AuthViewModel usando o viewModels
        val authViewModel: AuthViewModel by viewModels()
        // Inicializa o TaskViewModel usando viewModels e o factory
        val taskViewModel: TaskViewModel by viewModels { taskViewModelFactory }
        // Obtém o ID do usuário com o AuthViewModel
        val user = authViewModel.currentUser?.uid ?: ""
        // Cria um worker para sincronizar as tarefas com o ID do usuário
        TaskSyncWorker.enqueueTaskSync(applicationContext, user)
        setContent {
            TODOAppTheme {
                // Cria um NavController para navegação
                val navController = rememberNavController()
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    // Chama o AppNavigation
                    AppNavigation(
                        navController = navController,
                        authViewModel = authViewModel,
                        taskViewModel = taskViewModel,
                        user = user
                    ) {
                        // Navega para a tela de login e limpa a pilha de navegação até a tela inicial
                        navController.navigate("login") {
                            popUpTo("home") { inclusive = true }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LoadingScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}
