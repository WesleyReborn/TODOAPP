package com.example.todoapp.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.todoapp.data.model.Task

@Dao
interface TaskDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task : Task)

    @Update
    suspend fun update(task : Task)

    @Query("SELECT * FROM tasks")
    suspend fun getAllTasks() : List<Task>

    @Query("DELETE FROM tasks WHERE id = :id")
    suspend fun deleteTask(id: String)

    @Insert
    suspend fun insertAll(tasks : List<Task>)

    @Query("DELETE FROM tasks")
    suspend fun clearTasks()

    @Query("SELECT * FROM tasks WHERE id = :id")
    suspend fun getTaskById(id: String): Task

    @Query("SELECT * FROM tasks WHERE isSynch = 0")
    suspend fun getUnsynchTasks(): List<Task>
}
