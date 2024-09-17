package com.example.todoapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.todoapp.data.TaskRepository
import com.example.todoapp.data.model.Task
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TaskViewModel(private val taskRepository: TaskRepository) : ViewModel() {

    private val _taskList = MutableStateFlow<List<Task>>(emptyList())
    val taskList : StateFlow<List<Task>> = _taskList
    private val _errorMessage = MutableStateFlow<String?>(null)
    init {
        loadTasks()
    }

    private fun loadTasks() {
        viewModelScope.launch {
            try {
                val tasks = taskRepository.getAllTasks()
                _taskList.value = tasks
            } catch (e : Exception) {
                _errorMessage.value = "Erro ao carregar as tarefas: ${e.message}"
            }
        }
    }

    fun insertTask(user : String, task: Task) {
        viewModelScope.launch {
            try {
                taskRepository.insert(task)
                taskRepository.syncTasks(user)
                loadTasks()
            } catch (e : Exception) {
                _errorMessage.value = "Erro ao inserir a tarefa: ${e.message}"
            }
        }
    }

    fun updateTask(user : String, task: Task) {
        viewModelScope.launch {
            try {
                taskRepository.update(task)
                taskRepository.syncTasks(user)
                loadTasks()
            } catch (e : Exception) {
                _errorMessage.value = "Erro ao atualizar a tarefa: ${e.message}"
            }
        }
    }

    fun deleteTask(user : String, taskId: String) {
        viewModelScope.launch {
           try {
               taskRepository.deleteTask(taskId)
               taskRepository.syncTasks(user)
               loadTasks()
           } catch (e : Exception) {
               _errorMessage.value = "Erro ao excluir a tarefa: ${e.message}"
           }
        }
    }
    fun getTaskById(id: String): Task? {
        return _taskList.value?.find { it.id == id }
    }

    fun synchTasks(user : String) {
        viewModelScope.launch {
            try {
                taskRepository.syncTasks(user)
                loadTasks()
            } catch (e : Exception) {
                _errorMessage.value = "Erro ao sincronizar as tarefas: ${e.message}"
            }
        }
    }
}

class TaskViewModelFactory(private val taskRepository : TaskRepository) : ViewModelProvider.Factory{
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(TaskViewModel::class.java)) {
            return TaskViewModel(taskRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
