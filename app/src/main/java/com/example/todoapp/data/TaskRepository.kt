package com.example.todoapp.data

import android.content.Context
import com.example.todoapp.data.dao.TaskDao
import com.example.todoapp.data.model.Task
import com.example.todoapp.utils.NetworkUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TaskRepository(
    private val taskDao: TaskDao,
    private val firestoreRepository: FirestoreRepository,
    private val context: Context
) {

    suspend fun insert(user : String, task: Task) {
        withContext(Dispatchers.IO) {
            taskDao.insert(task)
            if (NetworkUtils.isOnline(context)) {
                firestoreRepository.addTask(user, task)
            }
        }
    }

    suspend fun update(user : String, task: Task) {
        withContext(Dispatchers.IO) {
            taskDao.update(task)
            if (NetworkUtils.isOnline(context)) {
                firestoreRepository.updateTask(user, task)
            }
        }
    }

    suspend fun getAllTasks(): List<Task> {
        return withContext(Dispatchers.IO) {
            taskDao.getAllTasks()
        }
    }

    suspend fun deleteTask(user : String, taskId: String) {
        withContext(Dispatchers.IO) {
            taskDao.deleteTask(taskId)
            if (NetworkUtils.isOnline(context)) {
                firestoreRepository.deleteTask(user, taskId)
            }
        }
    }

    suspend fun getTaskById(id: String): Task {
        return withContext(Dispatchers.IO) {
            taskDao.getTaskById(id)
        }
    }

    suspend fun syncTasks(user : String) {
        withContext(Dispatchers.IO) {
            if (NetworkUtils.isOnline(context)) {
                val tasksFromFirestore = firestoreRepository.getAllTasks(user)
                for (task in tasksFromFirestore) {
                    taskDao.insert(task)
                }
            }
        }
    }
}
