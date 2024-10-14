package com.example.todoapp.pages

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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
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
import com.example.todoapp.TaskViewModel
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskPage(
    // Controlador de navegação
    navController: NavController,
    // ViewModel que gerencia as tarefas
    taskViewModel: TaskViewModel,
    // Identificador do usuário
    user: String
) {
    // Estrutura básica da página
    Scaffold(
        topBar = {
            // Barra superior com botão de voltar
            TopAppBar(
                title = {},
                actions = {
                    TextButton(onClick = {
                        // Retorna à tela anterior
                        navController.popBackStack()
                    }) {
                        Text("Voltar", style = MaterialTheme.typography.labelLarge)
                    }
                }
            )
        },
        content = { paddingValues ->
            // Estados que armazenam o título e a descrição da nova tarefa
            var title by remember { mutableStateOf("") }
            var description by remember { mutableStateOf("") }
            // Layout da página de adição de tarefa
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    // Respeita o padding fornecido pelo Scaffold
                    .padding(paddingValues)
                    // Espaçamento interno de 16dp
                    .padding(16.dp),
                // Centraliza os itens verticalmente
                verticalArrangement = Arrangement.Center,
                // Centraliza os itens horizontalmente
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Título da página
                Text(text = "Adicionar nova tarefa", style = MaterialTheme.typography.headlineLarge)
                // Espaçamento vertical
                Spacer(modifier = Modifier.height(16.dp))
                // Campo de texto para o título da tarefa
                OutlinedTextField(
                    // Valor do campo de texto
                    value = title,
                    // Callback chamado quando o valor muda
                    onValueChange = { title = it },
                    // Rótulo do campo de texto
                    label = { Text(text = "Título") },
                    // O campo de texto ocupa toda a largura disponível
                    modifier = Modifier.fillMaxWidth()
                )
                // Espaçamento vertical
                Spacer(modifier = Modifier.height(8.dp))
                // Campo de texto para a descrição da tarefa
                OutlinedTextField(
                    // Valor do campo de texto
                    value = description,
                    // Callback chamado quando o valor muda
                    onValueChange = { description = it },
                    // Rótulo do campo de texto
                    label = { Text(text = "Descrição") },
                    // O campo de texto ocupa toda a largura disponível
                    modifier = Modifier.fillMaxWidth()
                )
                // Espaçamento vertical
                Spacer(modifier = Modifier.height(16.dp))
                // Botão para salvar a nova tarefa
                Button(
                    onClick = {
                        // Gera um ID único para a nova tarefa
                        val id = UUID.randomUUID().toString()
                        // Cria uma nova tarefa com o título e descrição fornecidos
                        val newTask = Task(id = id, title = title, description = description)
                        // Insere a nova tarefa no banco de dados via ViewModel
                        taskViewModel.insertTask(user, newTask)
                        // Define um resultado indicando que a tarefa foi adicionada com sucesso
                        navController.previousBackStackEntry?.savedStateHandle?.set("task_result", "Tarefa adicionada com sucesso")
                        // Retorna à tela anterior
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
    )
}

