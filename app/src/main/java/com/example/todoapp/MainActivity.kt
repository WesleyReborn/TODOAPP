package com.example.todoapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.todoapp.data.repository.FirestoreRepository
import com.example.todoapp.data.repository.TaskRepository
import com.example.todoapp.data.database.TodoAppDatabase
import com.example.todoapp.navigation.AppNavigation
import com.example.todoapp.ui.theme.TODOAppTheme
import com.example.todoapp.worker.TaskSyncWorker

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inicializa o repositório de tarefas, responsável por fornecer dados
        // tanto do banco de dados local (Room) quanto do Firestore
        val taskRepository = TaskRepository(
            taskDao = TodoAppDatabase.getDatabase(applicationContext).taskDao(),
            firestoreRepository = FirestoreRepository(),
            context = applicationContext
        )
        // Obtém o ID do usuário autenticado
        val authViewModel : AuthViewModel by viewModels()
        val user = authViewModel.currentUser?.uid ?: ""
        // Cria um TaskViewModelFactory que será utilizado para fornecer instâncias de TaskViewModel
        val taskViewModelFactory = TaskViewModelFactory(taskRepository, user)
        // Inicializa o TaskViewModel, que lida com as operações de tarefas, usando o factory criado anteriormente
        val taskViewModel: TaskViewModel by viewModels { taskViewModelFactory }
        // Enfileira um Worker para sincronizar tarefas
        TaskSyncWorker.enqueueTaskSync(applicationContext, user)
        // Define o conteúdo da UI da Activity usando Jetpack Compose
        setContent {
            TODOAppTheme {
                // Cria um NavController que será usado para gerenciar a navegação entre as telas do app
                val navController = rememberNavController()
                // Cria uma estrutura de Scaffold que define a base da UI,
                // permitindo a adição de componentes como uma barra de navegação inferior ou superior, e preenchendo a tela
                Scaffold(modifier = Modifier.fillMaxSize()) { _ ->
                    // Chama a função que gerencia a navegação da aplicação
                    AppNavigation(
                        navController = navController,
                        authViewModel = authViewModel,
                        taskViewModel = taskViewModel,
                        user = user,
                    ) {
                        // Navega para a tela de login e remove todas as telas anteriores da pilha de navegação
                        // até a tela inicial ("home"), garantindo que o usuário não possa voltar à tela anterior
                        navController.navigate("login") {
                            popUpTo("home") { inclusive = true }
                        }
                    }
                }
            }
        }
    }
}
