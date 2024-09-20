package com.example.todoapp.pages

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
import java.util.UUID

@Composable
fun AddTaskPage(
    navController: NavController,
    taskViewModel: TaskViewModel,
    user: String
) {
    // Estados para título e descrição da nova tarefa
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    // Layout da página de adição de tarefa
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Título da página
        Text(text = "Adicionar nova tarefa", style = MaterialTheme.typography.headlineLarge)
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
        // Botão para salvar a nova tarefa
        Button(
            onClick = {
                // Gera um ID único para a nova tarefa
                val id = UUID.randomUUID().toString()
                // Cria uma nova tarefa com os dados fornecidos
                val newTask = Task(id = id, title = title, description = description)
                // Insere a nova tarefa no ViewModel
                taskViewModel.insertTask(user, newTask)
                // Volta para a tela anterior
                navController.popBackStack()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Salvar")
        }
    }
}
