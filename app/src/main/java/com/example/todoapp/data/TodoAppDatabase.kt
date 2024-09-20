package com.example.todoapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.todoapp.data.dao.TaskDao
import com.example.todoapp.data.model.Task

@Database(entities = [Task::class], version = 2, exportSchema = false)
abstract class TodoAppDatabase : RoomDatabase() {
    // Função abstrata para obter o DAO de tarefas
    abstract fun taskDao() : TaskDao
    companion object {
        @Volatile
        private var INSTANCE: TodoAppDatabase? = null
        // Função para obter a instância do banco de dados
        fun getDatabase(context: Context) : TodoAppDatabase {
            // Verifica se a instância já existe caso contrário cria uma nova
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TodoAppDatabase::class.java,
                    "task_database"
                )
                    // Configura a migração destrutiva como fallback
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
