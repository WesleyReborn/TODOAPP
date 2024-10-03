package com.example.todoapp.data

import android.util.Log
import com.example.todoapp.data.model.Task
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

// Repositório responsável pela interação com o Firestore, gerenciando as tarefas de um usuário
class FirestoreRepository {
    companion object {
        // Tag para logs
        private const val TAG = "FirestoreRepository"
    }
    // Instância do Firestore
    private val firestore = FirebaseFirestore.getInstance()
    // Função que retorna a referência para a coleção de tarefas de um usuário específico
    private fun userTaskList(user: String) = firestore
        // Coleção de usuários no Firestore
        .collection("users")
        // Documento correspondente ao usuário
        .document(user)
        // Coleção de tarefas dentro do documento do usuário
        .collection("tasks")
    // Função que busca todas as tarefas do Firestore para o usuário
    suspend fun getAllTasks(user: String): List<Task> {
        return try {
            Log.d(TAG, "Buscando todas as tarefas do Firestore para o usuário: $user")
            // Recupera os documentos da coleção de tarefas do usuário no Firestore
            val tasks = userTaskList(user)
                .get()
                // Espera até que a operação de busca seja concluída
                .await()
                // Obtém os documentos retornados
                .documents
                // Mapeia os documentos para objetos Task
                .mapNotNull { it.toObject(Task::class.java) }

            Log.d(TAG, "Tarefas encontradas: ${tasks.size}")
            // Retorna a lista de tarefas
            tasks
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao buscar tarefas do Firestore: ${e.message}")
            // Retorna uma lista vazia em caso de erro
            emptyList()
        }
    }
    // Função que adiciona uma tarefa ao Firestore para o usuário
    suspend fun addTask(user: String, task: Task) {
        try {
            Log.d(TAG, "Adicionando tarefa ao Firestore: ${task.title}")
            // Adiciona ou substitui a tarefa no Firestore
            userTaskList(user)
                // Define o ID do documento como o ID da tarefa
                .document(task.id)
                // Insere a tarefa no Firestore
                .set(task)
                // Espera até que a operação de inserção seja concluída
                .await()
            Log.d(TAG, "Tarefa adicionada com sucesso")
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao adicionar tarefa no Firestore: ${e.message}")
        }
    }
    // Função que atualiza uma tarefa existente no Firestore para o usuário
    suspend fun updateTask(user: String, task: Task) {
        try {
            Log.d(TAG, "Atualizando tarefa no Firestore: ${task.title}")
            // Atualiza a tarefa no Firestore (o comportamento é semelhante à inserção, substitui se existir)
            userTaskList(user)
                // Localiza o documento pelo ID da tarefa
                .document(task.id)
                // Define o novo conteúdo da tarefa
                .set(task)
                // Espera até que a operação de atualização seja concluída
                .await()
            Log.d(TAG, "Tarefa atualizada com sucesso")
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao atualizar tarefa no Firestore: ${e.message}")
        }
    }
    // Função que atualiza várias tarefas no Firestore para o usuário
    suspend fun updateTasks(user : String, tasks : List<Task>) {
        // Realiza a atualização das tarefas em segundo plano
        withContext(Dispatchers.IO) {
            // Itera sobre cada tarefa e chama a função de adicionar (que também atualiza)
            for(task in tasks) {
                addTask(user, task)
            }
        }
    }
    // Função que deleta uma tarefa do Firestore com base no ID
    suspend fun deleteTask(user: String, taskId: String) {
        try {
            Log.d(TAG, "Deletando tarefa no Firestore com ID: $taskId")
            // Exclui o documento correspondente ao ID da tarefa
            userTaskList(user)
                .document(taskId)
                // Realiza a exclusão no Firestore
                .delete()
                // Espera até que a operação de exclusão seja concluída
                .await()
            Log.d(TAG, "Tarefa deletada com sucesso")
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao deletar tarefa no Firestore: ${e.message}")
        }
    }
}

