package com.example.todoapp.pages

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomePage(
    // Controlador de navegação
    navController: NavHostController,
    // ViewModel que gerencia as tarefas
    taskViewModel: TaskViewModel,
    // ViewModel responsável pela autenticação do usuário
    authViewModel: AuthViewModel,
    // Identificador do usuário
    user: String,
    // Função de callback para lidar com o logout do usuário
    onLogout: () -> Unit
) {
    // Observa a lista de tarefas do ViewModel e reflete mudanças na UI
    val taskList by taskViewModel.taskList.collectAsState()
    // Observa o estado de carregamento (loading)
    val loadingState by taskViewModel.loadingState.collectAsState()
    // Sincroniza as tarefas ao carregar a página
    LaunchedEffect(key1 = Unit, block = { taskViewModel.synchTasks(user) })
    // Estado que controla a exibição do Snackbar
    val snackbarHostState = remember { SnackbarHostState() }
    // Escopo de coroutine para mostrar Snackbar de forma assíncrona
    val coroutineScope = rememberCoroutineScope()
    // Efeito lançado para exibir o Snackbar quando há um resultado de tarefa
    LaunchedEffect(navController) {
        navController.currentBackStackEntry?.savedStateHandle?.getLiveData<String>("task_result")?.observe(
            navController.currentBackStackEntry!!) {
            result ->
            Log.d("HomePage", "Resultado recebido: $result")
            result.let {
                // Lança uma coroutine para exibir o Snackbar
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(it)
                    // Limpa a mensagem após ser exibida
                    navController.currentBackStackEntry!!.savedStateHandle.remove<String>("task_result")
                }
        }
        }
    }
    // Estrutura principal da página utilizando Scaffold
    Scaffold(
        topBar = {
            TopAppBar(
                // Título da página
                title = { Text("Lista de Tarefas", style = MaterialTheme.typography.titleLarge) },
                // Botão de logout
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
        // Define onde o Snackbar será exibido
        snackbarHost = { SnackbarHost(snackbarHostState) },
        content = { innerPadding ->
            // Verifica o estado de carregamento e exibe a UI correspondente
            when (loadingState) {
                // Se estiver carregando, exibe um indicador de progresso
                is State.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        contentAlignment = Alignment.Center
                    ) {
                        // Indicador de carregamento
                        CircularProgressIndicator()
                    }
                }
                // Se o usuário estiver autenticado, exibe a lista de tarefas
                is State.Authenticated -> {
                    TaskList(
                        // Passa a lista de tarefas para exibição
                        tasks = taskList,
                        modifier = Modifier.padding(innerPadding),
                        // Ação para editar uma tarefa
                        onEditTask = { task ->
                            navController.navigate("edit_task/${task.id}/$user")
                            coroutineScope.launch {
                            }
                        },
                        // Ação para deletar uma tarefa
                        onDeleteTask = { taskId ->
                            // Deleta a tarefa do banco de dados
                            taskViewModel.deleteTask(user, taskId)
                            coroutineScope.launch {
                                // Exibe o Snackbar após deletar a tarefa com a opção de desfazer
                                val result = snackbarHostState.showSnackbar(
                                    message = "Tarefa deletada com sucesso",
                                    actionLabel = "Desfazer",
                                    duration = SnackbarDuration.Long,
                                    // Permite que o Snackbar seja descartado
                                    withDismissAction = true
                                )
                                // Se o usuário clicar em "Desfazer", restaura a tarefa deletada
                                if (result == SnackbarResult.ActionPerformed) {
                                    // Restaura a tarefa deletada
                                    taskViewModel.undoDelete(user)
                                    // Exibe o Snackbar informando que a tarefa foi restaurada
                                    snackbarHostState.showSnackbar(
                                        message = "Tarefa restaurada com sucesso",
                                        duration = SnackbarDuration.Short
                                    )
                                }
                            }
                        }
                    )
                }
                // Exibe uma mensagem de erro, se houver
                is State.Error -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        contentAlignment = Alignment.Center
                    ) {
                        // Exibe a mensagem de erro recebida do estado
                        Text(text = (loadingState as State.Error).message)
                    }
                }
                else -> {
                    // Caso sem tratamento explícito
                }
            }
        },
        // Botão flutuante para adicionar uma nova tarefa
        floatingActionButton = {
            FloatingActionButton(onClick = {
                // Navega para a tela de adição de tarefas
                navController.navigate("add_task/$user")
            }) {
                // Ícone de adicionar tarefa
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


