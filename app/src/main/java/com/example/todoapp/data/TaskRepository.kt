package com.example.todoapp.data

import android.content.Context
import com.example.todoapp.data.dao.TaskDao
import com.example.todoapp.data.model.Task
import com.example.todoapp.utils.NetworkUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

import android.util.Log

class TaskRepository(
    private val taskDao: TaskDao,
    private val firestoreRepository: FirestoreRepository,
    private val context: Context
) {
    companion object {
        private const val TAG = "TaskRepository"
    }
    // Função para inserir uma nova tarefa
    suspend fun insert(user : String, task: Task) {
        Log.d(TAG, "Inserindo tarefa no banco de dados local: ${task.title}")
        withContext(Dispatchers.IO) {
            taskDao.insert(task)
            firestoreRepository.addTask(user, task)
        }
    }
    // Função para atualizar uma tarefa existente
    suspend fun update(user : String, task: Task) {
        Log.d(TAG, "Atualizando tarefa: ${task.title}")
        withContext(Dispatchers.IO) {
            taskDao.update(task)
            firestoreRepository.updateTask(user, task)
        }
    }
    // Função para recuperar todas as tarefas
    suspend fun getAllTasks(): List<Task> {
        Log.d(TAG, "Recuperando todas as tarefas do banco de dados local")
        return withContext(Dispatchers.IO) {
            taskDao.getAllTasks()
        }
    }
    // Função para deletar uma tarefa
    suspend fun deleteTask(user : String, taskId : String) {
        return withContext(Dispatchers.IO) {
            taskDao.deleteTask(taskId)
            firestoreRepository.deleteTask(user, taskId)
        }
    }
    // Função para buscar uma tarefa pelo ID
    suspend fun getTaskById(id: String): Task {
        Log.d(TAG, "Buscando tarefa por ID: $id")
        return withContext(Dispatchers.IO) {
            val task = taskDao.getTaskById(id)
            Log.d(TAG, "Tarefa encontrada: ${task.title}")
            task
        }
    }
    // Função para sincronizar tarefas com o Firestore
    suspend fun syncTasks(user: String) {
        withContext(Dispatchers.IO) {
            Log.d(TAG, "Iniciando sincronização de tarefas para o usuário: $user")
            try {
                if (NetworkUtils.isOnline(context)) {
                    val unsynchTasks = taskDao.getUnsynchTasks()
                    Log.d(TAG, "Tarefas não sincronizadas encontradas: ${unsynchTasks.size}")
                    for (task in unsynchTasks) {
                        Log.d(TAG, "Sincronizando tarefa: ${task.title}")
                        firestoreRepository.addTask(user, task)
                        taskDao.insert(task.copy(isSynch = true))
                    }
                    val tasksFirestore = firestoreRepository.getAllTasks(user)
                    Log.d(TAG, "Tarefas recuperadas do Firestore: ${tasksFirestore.size}")
                    for (task in tasksFirestore) {
                        Log.d(
                            TAG,
                            "Inserindo tarefa do Firestore no banco local: ${task.title}"
                        )
                        taskDao.insert(task)
                    }
                } else {
                    Log.d(TAG, "Dispositivo offline sincronização ignorada")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Erro ao sincronizar tarefas: ${e.message}")
                e.printStackTrace()
            }
        }
    }
    // Função para deletar uma tarefa localmente
    suspend fun deleteTaskLocally(taskId: String) {
        withContext(Dispatchers.IO) {
            taskDao.deleteTask(taskId)
        }
    }
}
