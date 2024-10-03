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
    // Controlador de navegação
    navController: NavController,
    // ViewModel que gerencia as tarefas
    taskViewModel: TaskViewModel,
    // Identificador do usuário
    user: String,
    // ID da tarefa a ser editada
    taskId: String
) {
    // Log para depuração ao abrir a página de edição
    Log.d("EditTaskPage", "Página de edição aberta para o Task ID: $taskId")
    // Obtém a tarefa pelo ID usando o ViewModel
    val task = taskViewModel.getTaskById(taskId)
    // Estados para armazenar o título e a descrição da tarefa, inicializados com os valores da tarefa
    var title by remember { mutableStateOf(task?.title ?: "") }
    var description by remember { mutableStateOf(task?.description ?: "") }
    // Verifica se a tarefa foi encontrada
    if (task == null) {
        // Log de erro e mensagem de erro na tela se a tarefa não for encontrada
        Log.e("EditTaskPage", "Tarefa não encontrada para o ID: $taskId")
        Text(text = "Tarefa não encontrada", style = MaterialTheme.typography.headlineLarge)
        return
    }
    // Layout da página de edição da tarefa
    Column(
        modifier = Modifier
            .fillMaxSize()
            // Adiciona um espaçamento de 16dp
            .padding(16.dp),
        // Centraliza os itens verticalmente
        verticalArrangement = Arrangement.Center,
        // Centraliza os itens horizontalmente
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Editar tarefa", style = MaterialTheme.typography.headlineLarge)
        // Espaçamento vertical
        Spacer(modifier = Modifier.height(16.dp))
        // Campo de texto para o título da tarefa
        OutlinedTextField(
            // Valor atual do título
            value = title,
            // Atualiza o estado quando o usuário altera o título
            onValueChange = { title = it },
            // Rótulo do campo de texto
            label = { Text(text = "Título") },
            // O campo ocupa toda a largura disponível
            modifier = Modifier.fillMaxWidth()
        )
        // Espaçamento vertical
        Spacer(modifier = Modifier.height(8.dp))
        // Campo de texto para a descrição da tarefa
        OutlinedTextField(
            // Valor atual da descrição
            value = description,
            // Atualiza o estado quando o usuário altera a descrição
            onValueChange = { description = it },
            // Rótulo do campo de texto
            label = { Text(text = "Descrição") },
            // O campo ocupa toda a largura disponível
            modifier = Modifier.fillMaxWidth()
        )
        // Espaçamento vertical
        Spacer(modifier = Modifier.height(16.dp))
        // Botão para salvar as alterações
        Button(
            onClick = {
                // Log de depuração ao clicar no botão salvar
                Log.d("EditTaskPage", "Botão salvar clicado para tarefa: $title")
                Log.d("EditTaskPage", "Atualizando tarefa: $title")
                // Atualiza a tarefa no ViewModel
                taskViewModel.updateTask(user, Task(id = taskId, title = title, description = description))
                // Define o resultado da edição para a página anterior
                navController.previousBackStackEntry?.savedStateHandle?.set("task_result", "Tarefa editada com sucesso")
                // Volta para a tela anterior
                navController.popBackStack()
            },
            // O botão ocupa toda a largura disponível
            modifier = Modifier.fillMaxWidth()
        ) {
            // Texto do botão
            Text(text = "Salvar")
        }
    }
}
