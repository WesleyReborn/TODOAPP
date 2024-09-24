package com.example.todoapp.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.todoapp.AuthViewModel
import com.example.todoapp.State
import com.example.todoapp.data.model.Task
import com.example.todoapp.ui.TaskViewModel
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomePage(
    navController: NavHostController,
    taskViewModel: TaskViewModel,
    authViewModel: AuthViewModel,
    user: String,
    onLogout: () -> Unit
) {
    // Observa a lista de tarefas do ViewModel
    val taskList by taskViewModel.taskList.collectAsState()
    // Observa o estado de loading
    val loadingState by taskViewModel.loadingState.collectAsState()
    // Sincroniza as tarefas quando a página é carregada
    LaunchedEffect(key1 = Unit, block = { taskViewModel.synchTasks(user) })
    // Estado para controlar a exibição das Snackbars
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    // Estrutura da página com Scaffold
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Lista de Tarefas", style = MaterialTheme.typography.titleLarge) },
                actions = {
                    TextButton(onClick = {
                        authViewModel.logout()
                        onLogout()
                    }) {
                        Text("Sair", style = MaterialTheme.typography.labelLarge)
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        content = { innerPadding ->
            when (loadingState) {
                is State.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                is State.Authenticated -> {
                    TaskList(
                        tasks = taskList,
                        modifier = Modifier.padding(innerPadding),
                        onEditTask = { task ->
                            navController.navigate("edit_task/${task.id}/$user")
                        },
                        onDeleteTask = { taskId ->
                            taskViewModel.deleteTask(user, taskId)
                            coroutineScope.launch {
                                val result = snackbarHostState.showSnackbar(
                                    message = "Tarefa deletada",
                                    actionLabel = "Desfazer",
                                    duration = SnackbarDuration.Long,
                                    withDismissAction = true
                                )
                                if (result == SnackbarResult.ActionPerformed) {
                                    taskViewModel.undoDelete(user)
                                }
                            }
                        }
                    )
                }
                is State.Error -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = (loadingState as State.Error).message)
                    }
                }
                else -> {

                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                navController.navigate("add_task/$user")
            }) {
                Icon(Icons.Default.Add, contentDescription = "Adicionar tarefa")
            }
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
    // Lista de tarefas usando LazyColumn
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
    // Item de tarefa com ElevatedCard
    ElevatedCard(modifier = Modifier.padding(8.dp)) {
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


