package com.example.todoapp.pages

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.firebaseauthlogin.AuthViewModel
import com.example.todoapp.data.TaskRepository
import com.example.todoapp.data.model.Task
import com.example.todoapp.ui.TaskViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomePage(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    taskViewModel: TaskViewModel,
    authViewModel: AuthViewModel,
    user : String,
    onLogout: () -> Unit
) {
    val taskList by taskViewModel.taskList.observeAsState(initial = emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Lista de Tarefas") },
                actions = {
                    TextButton(onClick = {
                        authViewModel.logout()
                        onLogout() }) {
                        Text("Sair")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                navController.navigate("add_task/$user")
            }) {
                Icon(Icons.Default.Add, contentDescription = "Adicionar tarefa")
            }
        },
        content = { innerPadding ->
            TaskList(
                tasks = taskList,
                modifier = Modifier.padding(innerPadding),
                onEditTask = { task ->
                    navController.navigate("edit_task/${task.id}/$user")
                },
                onDeleteTask = { taskId ->
                    taskViewModel.deleteTask(user, taskId)
                }
            )
        }
    )
}

@Composable
fun TaskList(
    tasks: List<Task>,
    modifier: Modifier = Modifier,
    onEditTask: (Task) -> Unit,
    onDeleteTask: (String) -> Unit
) {
    LazyColumn(modifier = modifier) {
        items(tasks) { task ->
            TaskItem(
                task = task,
                onEdit = onEditTask,
                onDelete = onDeleteTask
            )
        }
    }
}

@Composable
fun TaskItem(
    task: Task,
    onEdit: (Task) -> Unit,
    onDelete: (String) -> Unit
) {
    Card(modifier = Modifier.padding(8.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = task.title, style = MaterialTheme.typography.headlineSmall)
            Text(text = task.description, style = MaterialTheme.typography.bodyLarge)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = { onEdit(task) }) {
                    Icon(Icons.Default.Edit, contentDescription = "Editar tarefa")
                }
                IconButton(onClick = { onDelete(task.id) }) {
                    Icon(Icons.Default.Delete, contentDescription = "Excluir tarefa")
                }
            }
        }
    }
}
