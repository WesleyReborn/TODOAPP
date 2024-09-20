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
    // Estado que mantém a lista de tarefas
    private val _taskList = MutableStateFlow<List<Task>>(emptyList())
    val taskList : StateFlow<List<Task>> = _taskList
    // Estado que mantém a mensagem de erro
    private val _errorMessage = MutableStateFlow<String?>(null)
    // Inicializa o ViewModel carregando as tarefas
    init {
        loadTasks()
    }
    // Função para carregar as tarefas do repositório
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
    // Função para inserir uma nova tarefa
    fun insertTask(user : String, task: Task) {
        viewModelScope.launch {
            try {
                taskRepository.insert(user, task)
                taskRepository.syncTasks(user)
                loadTasks()
            } catch (e : Exception) {
                _errorMessage.value = "Erro ao inserir a tarefa: ${e.message}"
            }
        }
    }
    // Função para atualizar uma tarefa existente
    fun updateTask(user : String, task: Task) {
        viewModelScope.launch {
            try {
                taskRepository.update(user, task)
                taskRepository.syncTasks(user)
                loadTasks()
            } catch (e : Exception) {
                _errorMessage.value = "Erro ao atualizar a tarefa: ${e.message}"
            }
        }
    }
    // Função para deletar uma tarefa
    fun deleteTask(user : String, taskId: String) {
        viewModelScope.launch {
            try {
                _taskList.value = _taskList.value.filter { it.id != taskId }
                taskRepository.deleteTask(user, taskId)
                taskRepository.syncTasks(user)
                loadTasks()
            } catch (e : Exception) {
                _errorMessage.value = "Erro ao excluir a tarefa: ${e.message}"
            }
        }
    }
    // Função para obter uma tarefa pelo ID
    fun getTaskById(id: String): Task? {
        return _taskList.value.find { it.id == id }
    }
    // Função para sincronizar as tarefas
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
// Fábrica para criar instâncias de TaskViewModel
class TaskViewModelFactory(private val taskRepository : TaskRepository) : ViewModelProvider.Factory{
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(TaskViewModel::class.java)) {
            return TaskViewModel(taskRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
