package com.example.todoapp.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
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
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.todoapp.data.model.Task
import com.example.todoapp.ui.TaskViewModel

@Composable
fun EditTaskPage(
    navController: NavController,
    taskViewModel: TaskViewModel,
    user : String,
    taskId: String
) {
    val task = taskViewModel.getTaskById(taskId)
    var title by remember { mutableStateOf(task?.title ?: "") }
    var description by remember { mutableStateOf(task?.description ?: "") }

    if (task == null) {
        Text(text = "Tarefa não encontrada", fontSize = 32.sp)
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Editar tarefa", fontSize = 32.sp)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text(text = "Título") })
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text(text = "Descrição") })

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            taskViewModel.updateTask(user, Task(id = taskId, title = title, description = description, completed = task.completed))
            navController.popBackStack()
        }) {
            Text(text = "Salvar")
        }
    }
}
