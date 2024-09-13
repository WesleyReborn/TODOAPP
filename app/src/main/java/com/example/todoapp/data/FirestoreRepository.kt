package com.example.todoapp.data

import com.example.todoapp.data.model.Task
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.UUID

class FirestoreRepository {

    private val firestore = FirebaseFirestore.getInstance()

    private fun userTaskList(user : String) = firestore
        .collection("users")
        .document(user)
        .collection("tasks")

    suspend fun getAllTasks(user : String) : List<Task> {
        return userTaskList(user)
            .get()
            .await()
            .documents
            .mapNotNull { it.toObject(Task::class.java) }
    }

    suspend fun addTask(user : String, taskId: Task) {
        userTaskList(user)
        .document(taskId.id)
        .set(taskId)
        .await()
    }

    suspend fun updateTask(user : String, taskId: Task) {
        userTaskList(user)
        .document(taskId.id)
        .set(taskId)
        .await()
    }

    suspend fun deleteTask(user : String, taskId : String) {
        userTaskList(user)
            .document(taskId)
            .delete()
            .await()
    }
}
