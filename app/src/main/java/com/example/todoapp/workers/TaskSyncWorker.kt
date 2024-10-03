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
import com.example.todoapp.data.FirestoreRepository
import com.example.todoapp.data.TaskRepository
import com.example.todoapp.data.model.Task
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.LinkedBlockingQueue

// Classe que estende CoroutineWorker para execução de tarefas assíncronas
class TaskSyncWorker(
    context: Context,
    workerParams: WorkerParameters,
    // Repositório local de tarefas (Room)
    private val taskRepository: TaskRepository,
    // Repositório remoto de tarefas (Firestore)
    private val firestoreRepository: FirestoreRepository
) : CoroutineWorker(context, workerParams) {
    // Método que é chamado quando o worker é executado
    override suspend fun doWork(): Result {
        return try {
            // Obtém o ID do usuário a partir dos parâmetros de entrada do worker
            val user = inputData.getString("user") ?: return Result.failure()
            // Executa a sincronização de tarefas no dispatcher IO (para operações de longa duração)
            withContext(Dispatchers.IO) {
                // Obtém as tarefas do Firestore (remotas)
                val remoteTasks = firestoreRepository.getAllTasks(user)
                // Obtém as tarefas locais do Room
                val localTasks = taskRepository.getAllTasks()
                // Resolve conflitos entre tarefas locais e remotas
                val resolvedTasks = resolveConflictis(localTasks, remoteTasks)
                // Atualiza as tarefas locais e remotas com as resolvidas
                taskRepository.updateTasks(resolvedTasks)
                firestoreRepository.updateTasks(user, resolvedTasks)
            }
            // Retorna sucesso após a sincronização
            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            // Retorna falha em caso de exceção
            Result.failure()
        }
    }

    // Método para resolver conflitos entre as tarefas locais e remotas
    private fun resolveConflictis(localTasks : List<Task>, remoteTasks : List<Task>) : List<Task> {
        val resolvedTasks = mutableListOf<Task>()
        // Associa as tarefas locais por ID para fácil acesso
        val taskMap = localTasks.associateBy { it.id }.toMutableMap()
        // Percorre as tarefas remotas e compara com as locais
        for (remoteTask in remoteTasks) {
            val localTask = taskMap[remoteTask.id]
            // Se a tarefa remota for mais recente ou não existir localmente, substitui/adiciona
            if(localTask == null || remoteTask.timestamp > localTask.timestamp) {
                taskMap[remoteTask.id] = remoteTask
            }
        }
        // Adiciona todas as tarefas resolvidas na lista final
        resolvedTasks.addAll(taskMap.values)
        return resolvedTasks
    }
    // Companion object para gerenciar a fila de trabalhos e enfileirar a sincronização de tarefas
    companion object {
        // Fila de trabalhos de sincronização
        private val jobQueue = LinkedBlockingQueue<() -> Unit>()
        // Cria um escopo Coroutine com o dispatcher IO e SupervisorJob (para tratamento de falhas)
        private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
        // Inicializa uma coroutine que consome a fila de trabalhos continuamente
        init {
            scope.launch {
                while (isActive) {
                    // Retira o próximo trabalho da fila e o executa
                    val job = withContext(Dispatchers.IO) {
                        // Bloqueia até que um trabalho esteja disponível
                        jobQueue.take()
                    }
                    // Executa o trabalho
                    job()
                }
            }
        }
        // Função para enfileirar a sincronização de tarefas
        fun enqueueTaskSync(context: Context, user: String) {
            // Cria os dados de input (o ID do usuário)
            val inputData = workDataOf("user" to user)
            // Enfileira um novo trabalho de sincronização
            jobQueue.offer {
                // Configura o trabalho com as restrições de rede necessárias (conexão disponível)
                val syncRequest = OneTimeWorkRequestBuilder<TaskSyncWorker>()
                    .setInputData(inputData)
                    .setConstraints(
                        Constraints.Builder()
                            .setRequiredNetworkType(NetworkType.CONNECTED)
                            .build()
                    )
                    .build()
                // Enfileira o trabalho com uma política de substituição
                WorkManager.getInstance(context)
                    .enqueueUniqueWork(
                        // Nome único do trabalho
                        "TaskSyncWork",
                        // Substitui ou adiciona
                        ExistingWorkPolicy.APPEND_OR_REPLACE,
                        syncRequest
                    )
            }
        }
    }
}
