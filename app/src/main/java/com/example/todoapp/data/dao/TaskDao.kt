package com.example.todoapp.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.todoapp.data.model.Task

// Define a interface DAO (Data Access Object) para a entidade Task
@Dao
interface TaskDao {
    // Insere uma nova tarefa, substituindo em caso de conflito
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task : Task)
    // Atualiza uma tarefa existente
    @Update
    suspend fun update(task : Task)
    // Retorna todas as tarefas
    @Query("SELECT * FROM tasks")
    suspend fun getAllTasks() : List<Task>
    // Deleta uma tarefa pelo seu ID
    @Query("DELETE FROM tasks WHERE id = :id")
    suspend fun deleteTask(id: String)
    // Insere uma lista de tarefas
    @Insert
    suspend fun insertAll(tasks : List<Task>)
    // Deleta todas as tarefas
    @Query("DELETE FROM tasks")
    suspend fun clearTasks()
    // Retorna uma tarefa pelo seu ID
    @Query("SELECT * FROM tasks WHERE id = :id")
    suspend fun getTaskById(id: String): Task
    // Retorna todas as tarefas que não estão sincronizadas
    @Query("SELECT * FROM tasks WHERE isSynch = 0")
    suspend fun getUnsynchTasks(): List<Task>
}
