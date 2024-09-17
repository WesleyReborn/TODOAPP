package com.example.todoapp.data

import com.example.todoapp.data.model.Task
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.UUID

import android.util.Log

class FirestoreRepository {

    companion object {
        private const val TAG = "FirestoreRepository"
    }

    private val firestore = FirebaseFirestore.getInstance()

    private fun userTaskList(user: String) = firestore
        .collection("users")
        .document(user)
        .collection("tasks")

    suspend fun getAllTasks(user: String): List<Task> {
        return try {
            Log.d(TAG, "Buscando todas as tarefas do Firestore para o usuário: $user")
            val tasks = userTaskList(user)
                .get()
                .await()
                .documents
                .mapNotNull { it.toObject(Task::class.java) }

            Log.d(TAG, "Tarefas encontradas: ${tasks.size}")
            tasks
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao buscar tarefas do Firestore: ${e.message}")
            emptyList()
        }
    }

    suspend fun addTask(user: String, task: Task) {
        try {
            Log.d(TAG, "Adicionando tarefa ao Firestore: ${task.title}")
            userTaskList(user)
                .document(task.id)
                .set(task)
                .await()
            Log.d(TAG, "Tarefa adicionada com sucesso")
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao adicionar tarefa no Firestore: ${e.message}")
        }
    }

    suspend fun updateTask(user: String, task: Task) {
        try {
            Log.d(TAG, "Atualizando tarefa no Firestore: ${task.title}")
            userTaskList(user)
                .document(task.id)
                .set(task)
                .await()
            Log.d(TAG, "Tarefa atualizada com sucesso")
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao atualizar tarefa no Firestore: ${e.message}")
        }
    }

    suspend fun deleteTask(user: String, taskId: String) {
        try {
            Log.d(TAG, "Deletando tarefa no Firestore com ID: $taskId")
            userTaskList(user)
                .document(taskId)
                .delete()
                .await()
            Log.d(TAG, "Tarefa deletada com sucesso")
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao deletar tarefa no Firestore: ${e.message}")
        }
    }
}

