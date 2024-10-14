package com.example.todoapp.data.repository

import android.content.Context
import com.example.todoapp.dao.TaskDao
import com.example.todoapp.data.model.Task
import com.example.todoapp.utils.NetworkUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

import android.util.Log

// Repositório responsável por gerenciar o acesso aos dados das tarefas, tanto no banco local (Room) quanto no Firestore
class TaskRepository(
    // Acesso ao banco de dados local (Room)
    private val taskDao: TaskDao,
    // Acesso ao banco de dados remoto (Firestore)
    private val firestoreRepository: FirestoreRepository,
    // Contexto necessário para verificar o estado da rede
    private val context: Context
) {
    companion object {
        // Tag para logs
        private const val TAG = "TaskRepository"
    }
    // Função responsável por inserir uma nova tarefa no banco de dados local e no Firestore
    suspend fun insert(userId : String, task: Task) {
        Log.d(TAG, "Inserindo tarefa no banco de dados local: ${task.title}")
        // Realiza a inserção da tarefa em segundo plano
        withContext(Dispatchers.IO) {
            // Insere a tarefa no banco local (Room)
            taskDao.insert(task.copy(userId = userId))
            // Insere a tarefa no Firestore
            firestoreRepository.addTask(userId, task)
        }
    }
    // Função responsável por atualizar uma tarefa existente no banco de dados local e no Firestore
    suspend fun update(userId : String, task: Task) {
        Log.d(TAG, "Atualizando tarefa: ${task.title}")
        // Realiza a atualização da tarefa em segundo plano
        withContext(Dispatchers.IO) {
            // Atualiza a tarefa no banco local (Room)
            taskDao.update(task.copy(userId = userId))
            // Atualiza a tarefa no Firestore
            firestoreRepository.updateTask(userId, task)
        }
    }
    // Função responsável por inserir ou atualizar uma lista de tarefas no banco de dados local
    suspend fun updateTasks(userId : String, tasks : List<Task>) {
        // Realiza a inserção/atualização das tarefas em segundo plano
        withContext(Dispatchers.IO) {
            // Insere ou substitui as tarefas na tabela local
            taskDao.insertAll(tasks.map { it.copy(userId = userId) })
        }
    }
    // Função responsável por recuperar todas as tarefas armazenadas localmente no banco de dados
    suspend fun getAllTasks(userId: String): List<Task> {
        Log.d(TAG, "Recuperando todas as tarefas do banco de dados local")
        // Recupera as tarefas em segundo plano
        return withContext(Dispatchers.IO) {
            // Retorna todas as tarefas salvas no banco local
            taskDao.getAllTasks(userId)
        }
    }
    // Função responsável por excluir uma tarefa tanto do banco de dados local quanto do Firestore
    suspend fun deleteTask(userId : String, taskId : String) {
        // Realiza a exclusão em segundo plano
        return withContext(Dispatchers.IO) {
            // Remove a tarefa do banco local (Room)
            taskDao.deleteTask(taskId, userId)
            // Remove a tarefa do Firestore
            firestoreRepository.deleteTask(userId, taskId)
        }
    }
    // Função responsável por sincronizar as tarefas entre o banco de dados local e o Firestore
    suspend fun syncTasks(userId: String) {
        // Realiza a sincronização em segundo plano
        withContext(Dispatchers.IO) {
            Log.d(TAG, "Iniciando sincronização de tarefas para o usuário: $userId")
            try {
                // Verifica se o dispositivo está conectado à internet antes de tentar sincronizar
                if (NetworkUtils.isOnline(context)) {
                    // Recupera todas as tarefas que ainda não foram sincronizadas com o Firestore
                    val unsynchTasks = taskDao.getUnsynchTasks(userId)
                    Log.d(TAG, "Tarefas não sincronizadas encontradas: ${unsynchTasks.size}")
                    // Envia cada tarefa não sincronizada para o Firestore
                    for (task in unsynchTasks) {
                        Log.d(TAG, "Sincronizando tarefa: ${task.title}")
                        // Adiciona a tarefa ao Firestore
                        firestoreRepository.addTask(userId, task)
                        // Marca a tarefa como sincronizada no banco local
                        taskDao.insert(task.copy(isSynch = true))
                    }
                    // Recupera todas as tarefas do Firestore
                    val tasksFirestore = firestoreRepository.getAllTasks(userId)
                    Log.d(TAG, "Tarefas recuperadas do Firestore: ${tasksFirestore.size}")
                    // Insere ou substitui as tarefas do Firestore no banco de dados local
                    for (task in tasksFirestore) {
                        Log.d(TAG, "Inserindo tarefa do Firestore no banco local: ${task.title}"
                        )
                        taskDao.insert(task.copy(userId = userId))
                    }
                } else {
                    // Se o dispositivo estiver offline, a sincronização é ignorada
                    Log.d(TAG, "Dispositivo offline sincronização ignorada")
                }
            } catch (e: Exception) {
                // Registra o erro caso a sincronização falhe
                Log.e(TAG, "Erro ao sincronizar tarefas: ${e.message}")
                e.printStackTrace()
            }
        }
    }
    // Função responsável por excluir uma tarefa localmente sem afetar o Firestore
    suspend fun deleteTaskLocally(userId: String, taskId: String) {
        // Realiza a exclusão local em segundo plano
        withContext(Dispatchers.IO) {
            // Remove a tarefa apenas do banco local (Room)
            taskDao.deleteTask(taskId, userId)
        }
    }
}
