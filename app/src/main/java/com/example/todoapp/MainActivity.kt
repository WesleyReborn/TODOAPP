package com.example.todoapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.firebaseauthlogin.AuthViewModel
import com.example.todoapp.ui.theme.TODOAppTheme
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.todoapp.data.FirestoreRepository
import com.example.todoapp.data.TaskRepository
import com.example.todoapp.data.TodoAppDatabase
import com.example.todoapp.ui.TaskViewModel
import com.example.todoapp.ui.TaskViewModelFactory


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val taskRepository = TaskRepository(
            taskDao = TodoAppDatabase.getDatabase(applicationContext).taskDao(),
            firestoreRepository = FirestoreRepository(),
            context = applicationContext
        )
        val taskViewModelFactory = TaskViewModelFactory(taskRepository)
        val authViewModel : AuthViewModel by viewModels()
        val taskViewModel : TaskViewModel by viewModels { taskViewModelFactory }
        setContent {
            TODOAppTheme {
                val navController = rememberNavController()
                val user = authViewModel.currentUser?.uid ?: ""
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AppNavigation(
                        navController = navController,
                        authViewModel = authViewModel,
                        taskViewModel = taskViewModel,
                        user = user,
                        onLogout = {
                            navController.navigate("login") {
                                popUpTo("home") { inclusive = true }
                            }
                        },
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}
