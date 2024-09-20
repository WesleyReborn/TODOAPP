package com.example.todoapp.pages

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.todoapp.data.model.Task
import com.example.todoapp.ui.TaskViewModel

@Composable
fun EditTaskPage(
    navController: NavController,
    taskViewModel: TaskViewModel,
    user: String,
    taskId: String
) {
    // Log para depuração ao abrir a página de edição
    Log.d("EditTaskPage", "Página de edição aberta para o Task ID: $taskId")
    // Obtém a tarefa pelo ID
    val task = taskViewModel.getTaskById(taskId)
    // Estados para título e descrição da tarefa
    var title by remember { mutableStateOf(task?.title ?: "") }
    var description by remember { mutableStateOf(task?.description ?: "") }
    // Verifica se a tarefa foi encontrada
    if (task == null) {
        Log.e("EditTaskPage", "Tarefa não encontrada para o ID: $taskId")
        Text(text = "Tarefa não encontrada", style = MaterialTheme.typography.headlineLarge)
        return
    }
    // Layout da página de edição
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Editar tarefa", style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(16.dp))
        // Campo de texto para o título da tarefa
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text(text = "Título") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        // Campo de texto para a descrição da tarefa
        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text(text = "Descrição") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        // Botão para salvar as alterações
        Button(
            onClick = {
                Log.d("EditTaskPage", "Botão salvar clicado para tarefa: $title")
                Log.d("EditTaskPage", "Atualizando tarefa: $title")
                taskViewModel.updateTask(user, Task(id = taskId, title = title, description = description))
                navController.popBackStack()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Salvar")
        }
    }
}
