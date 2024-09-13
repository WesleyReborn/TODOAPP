package com.example.todoapp.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.todoapp.data.TaskRepository
import com.example.todoapp.data.model.Task
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

class TaskViewModel(private val taskRepository: TaskRepository) : ViewModel() {

    private val _taskList = MutableLiveData<List<Task>>()
    val taskList : LiveData<List<Task>> = _taskList

    init {
        loadTasks()
    }

    private fun loadTasks() {
        viewModelScope.launch {
            val tasks = taskRepository.getAllTasks()
            _taskList.postValue(tasks)
        }
    }

    fun insertTask(user : String, task: Task) {
        viewModelScope.launch {
            taskRepository.insert(user, task)
            loadTasks()
        }
    }

    fun updateTask(user : String, task: Task) {
        viewModelScope.launch {
            taskRepository.update(user, task)
            loadTasks()
        }
    }

    fun deleteTask(user : String, taskId: String) {
        viewModelScope.launch {
            taskRepository.deleteTask(user, taskId)
            loadTasks()
        }
    }
    fun getTaskById(id: String): Task? {
        return _taskList.value?.find { it.id == id }
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
