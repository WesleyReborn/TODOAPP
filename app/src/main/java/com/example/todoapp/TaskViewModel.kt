package com.example.todoapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.todoapp.data.repository.TaskRepository
import com.example.todoapp.data.model.Task
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// ViewModel responsável por gerenciar o estado e as operações de tarefas
class TaskViewModel(private val taskRepository: TaskRepository, userId : String) : ViewModel() {
    // MutableStateFlow que contém a lista de tarefas, inicializado como uma lista vazia
    private val _taskList = MutableStateFlow<List<Task>>(emptyList())
    // StateFlow público que expõe a lista de tarefas, usado para observar mudanças de estado na UI
    val taskList : StateFlow<List<Task>> = _taskList
    // MutableStateFlow para armazenar mensagens de erro
    private val _errorMessage = MutableStateFlow<String?>(null)
    // MutableStateFlow para rastrear o estado de loading (carregamento)
    private val _loadingState = MutableStateFlow<State>(State.NotAuthenticated)
    // StateFlow que expõe o estado de loading para a UI
    val loadingState : StateFlow<State> = _loadingState
    // Variável para armazenar a tarefa excluída recentemente para permitir o undo (desfazer exclusão)
    private var recentlyDeletedTask : Task? = null
    // Bloco de inicialização que carrega as tarefas do repositório quando o ViewModel é criado
    init {
        loadTasks(userId)
    }
    // Função que carrega todas as tarefas do repositório
    private fun loadTasks(userId: String) {
        // Define o estado como carregando
        _loadingState.value = State.Loading
        viewModelScope.launch {
            try {
                // Obtém todas as tarefas
                val tasks = taskRepository.getAllTasks(userId)
                // Atualiza a lista de tarefas
                _taskList.value = tasks
                // Define o estado como autenticado (ou carregado com sucesso)
                _loadingState.value = State.Authenticated
            } catch (e : Exception) {
                // Define uma mensagem de erro
                _errorMessage.value = "Erro ao carregar as tarefas: ${e.message}"
                // Define o estado como erro
                _loadingState.value = State.Error(e.message ?: "Erro desconhecido")
            }
        }
    }
    // Função que insere uma nova tarefa no repositório
    fun insertTask(userId : String, task: Task) {
        viewModelScope.launch {
            try {
                // Insere a nova tarefa
                taskRepository.insert(userId, task)
                // Sincroniza as tarefas
                taskRepository.syncTasks(userId)
                // Recarrega as tarefas para atualizar a lista
                loadTasks(userId)
            } catch (e : Exception) {
                // Define a mensagem de erro
                _errorMessage.value = "Erro ao inserir a tarefa: ${e.message}"
            }
        }
    }
    // Função que atualiza uma tarefa existente
    fun updateTask(userId : String, task: Task) {
        viewModelScope.launch {
            try {
                // Atualiza a tarefa
                taskRepository.update(userId, task)
                // Sincroniza as tarefas
                taskRepository.syncTasks(userId)
                // Recarrega a lista de tarefas
                loadTasks(userId)
            } catch (e : Exception) {
                // Define a mensagem de erro
                _errorMessage.value = "Erro ao atualizar a tarefa: ${e.message}"
            }
        }
    }
    // Função que exclui uma tarefa
    fun deleteTask(userId : String, taskId: String) {
        viewModelScope.launch {
            try {
                // Armazena a tarefa para permitir desfazer
                recentlyDeletedTask = _taskList.value.find { it.id == taskId }
                // Exclui a tarefa localmente
                taskRepository.deleteTaskLocally(userId, taskId)
                // Atualiza a lista de tarefas na UI
                _taskList.value = _taskList.value.filter { it.id != taskId }
                // Exclui a tarefa do repositório remoto
                taskRepository.deleteTask(userId, taskId)
                // Sincroniza as tarefas
                taskRepository.syncTasks(userId)
            } catch (e : Exception) {
                // Define a mensagem de erro
                _errorMessage.value = "Erro ao excluir a tarefa: ${e.message}"
            }
        }
    }
    // Função que restaura uma tarefa excluída
    fun undoDelete(userId: String) {
        recentlyDeletedTask?.let { task ->
            viewModelScope.launch {
                try {
                    // Restaura a tarefa na lista de tarefas
                    _taskList.value = _taskList.value + task
                    // Insere a tarefa restaurada no repositório
                    taskRepository.insert(userId, task)
                    // Sincroniza as tarefas
                    taskRepository.syncTasks(userId)
                } catch (e : Exception) {
                    // Define a mensagem de erro
                    _errorMessage.value = "Erro ao restaurar a tarefa: ${e.message}"
                }
            }
        }
    }
    // Função que obtém uma tarefa pelo ID
    fun getTaskById(id: String): Task? {
        // Retorna a tarefa correspondente ao ID
        return _taskList.value.find { it.id == id }
    }
    // Função que sincroniza as tarefas manualmente
    fun synchTasks(userId : String) {
        viewModelScope.launch {
            try {
                // Sincroniza as tarefas
                taskRepository.syncTasks(userId)
                // Recarrega a lista de tarefas
                loadTasks(userId)
            } catch (e : Exception) {
                // Define a mensagem de erro
                _errorMessage.value = "Erro ao sincronizar as tarefas: ${e.message}"
            }
        }
    }
}
// Fábrica para criar instâncias de TaskViewModel, necessária para fornecer dependências
class TaskViewModelFactory(private val taskRepository : TaskRepository, private val userId: String) : ViewModelProvider.Factory{
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(TaskViewModel::class.java)) {
            // Retorna uma instância de TaskViewModel
            return TaskViewModel(taskRepository, userId) as T
        }
        // Lança uma exceção se a classe não for reconhecida
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
