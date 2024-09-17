package com.example.todoapp.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.example.todoapp.data.dao.TaskDao
import com.example.todoapp.data.model.Task
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalPagingApi::class)
class RemoteTaskSynch(private val firestore : FirebaseFirestore, private val taskDao : TaskDao) : RemoteMediator<Int, Task>() {
    override suspend fun load(loadType: LoadType, state: PagingState<Int, Task>): MediatorResult {
        return try {

            val unsynchTasks = taskDao.getUnsynchTasks()

            for (task in unsynchTasks) {
                firestore.collection("tasks").document(task.id).set(task).await()
                taskDao.insert(task.copy(isSynch = true))
            }

            val firestoreTask = firestore.collection("tasks").get().await()

            for (doc in firestoreTask.documents) {
                val task = doc.toObject(Task::class.java)
                if (task != null) {
                    taskDao.insert(task)
                }
            }

            MediatorResult.Success(endOfPaginationReached = firestoreTask.isEmpty)
        } catch (e: Exception) {
            MediatorResult.Error(e)
        }   }
}
