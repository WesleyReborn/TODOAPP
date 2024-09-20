package com.example.todoapp.workers

import android.content.Context
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.todoapp.data.TaskRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.LinkedBlockingQueue

class TaskSyncWorker(
    context: Context,
    workerParams: WorkerParameters,
    private val taskRepository: TaskRepository
) : CoroutineWorker(context, workerParams) {
    // Método que executa a sincronização
    override suspend fun doWork(): Result {
        return try {
            // Obtém o ID do usuário a partir dos parâmetros de entrada
            val user = inputData.getString("user") ?: return Result.failure()
            // Executa a sincronização das tarefas no contexto de IO
            withContext(Dispatchers.IO) {
                taskRepository.syncTasks(user)
            }
            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure()
        }
    }
    // Objeto companion para gerenciar a fila de trabalhos e a enfileiramento de sincronização
    companion object {
        private val jobQueue = LinkedBlockingQueue<() -> Unit>()
        private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
        // Inicializa um coroutine que processa a fila de trabalhos
        init {
            scope.launch {
                while (isActive) {
                    val job = withContext(Dispatchers.IO) {
                        jobQueue.take()
                    }
                    job()
                }
            }
        }
        // Função para enfileirar a sincronização de tarefas
        fun enqueueTaskSync(context: Context, user: String) {
            val inputData = workDataOf("user" to user)
            jobQueue.offer {
                val syncRequest = OneTimeWorkRequestBuilder<TaskSyncWorker>()
                    .setInputData(inputData)
                    .setConstraints(
                        Constraints.Builder()
                            .setRequiredNetworkType(NetworkType.CONNECTED)
                            .build()
                    )
                    .build()
                WorkManager.getInstance(context)
                    .enqueueUniqueWork(
                        "TaskSyncWork",
                        ExistingWorkPolicy.APPEND_OR_REPLACE,
                        syncRequest
                    )
            }
        }
    }
}
