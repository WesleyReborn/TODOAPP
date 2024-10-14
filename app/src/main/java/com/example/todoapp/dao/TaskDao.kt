package com.example.todoapp.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.todoapp.data.model.Task

// Define uma interface DAO (Data Access Object) que contém os métodos para acessar
// e manipular os dados da entidade Task no banco de dados
@Dao
interface TaskDao {
    // Insere uma nova tarefa no banco de dados.
    // Se houver um conflito (como um ID duplicado), a tarefa existente será substituída.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task : Task)
    // Atualiza uma tarefa existente no banco de dados.
    @Update
    suspend fun update(task : Task)
    // Recupera todas as tarefas armazenadas no banco de dados associadas a um usuário específico.
    @Query("SELECT * FROM tasks WHERE userId = :userId")
    suspend fun getAllTasks(userId: String): List<Task>
    // Deleta uma tarefa específica no banco de dados com base no ID fornecido.
    @Query("DELETE FROM tasks WHERE id = :id AND userId = :userId")
    suspend fun deleteTask(id: String, userId: String)
    // Insere uma lista de tarefas no banco de dados. O comportamento de conflito é o padrão (não substitui automaticamente).
    @Insert
    suspend fun insertAll(tasks : List<Task>)
    // Remove todas as tarefas armazenadas no banco de dados para um usuário específico.
    @Query("DELETE FROM tasks WHERE userId = :userId")
    suspend fun clearTasks(userId: String)
    // Recupera uma tarefa específica pelo ID e usuário. Retorna um objeto Task se encontrado.
    @Query("SELECT * FROM tasks WHERE id = :id AND userId = :userId")
    suspend fun getTaskById(id: String, userId: String): Task
    // Recupera todas as tarefas que ainda não foram sincronizadas com o backend (ou seja, o campo isSynch é igual a 0) para um usuário específico.
    @Query("SELECT * FROM tasks WHERE isSynch = 0 AND userId = :userId")
    suspend fun getUnsynchTasks(userId: String): List<Task>
}
