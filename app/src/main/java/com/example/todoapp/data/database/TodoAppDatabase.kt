package com.example.todoapp.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.todoapp.dao.TaskDao
import com.example.todoapp.data.model.Task

// Definição da classe do banco de dados Room com a entidade Task e a versão do banco de dados
@Database(entities = [Task::class], version = 3, exportSchema = false)
abstract class TodoAppDatabase : RoomDatabase() {
    // Método abstrato para obter o DAO de tarefas, que será implementado pelo Room
    abstract fun taskDao() : TaskDao
    companion object {
        // A instância é volátil para garantir que as atualizações sejam visíveis para todos os threads
        @Volatile
        private var INSTANCE: TodoAppDatabase? = null
        // Função para obter a instância do banco de dados (Singleton)
        fun getDatabase(context: Context) : TodoAppDatabase {
            // Usa um bloco synchronized para garantir que apenas um thread crie a instância do banco de dados
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    // Garante que estamos usando o contexto da aplicação
                    context.applicationContext,
                    // Classe do banco de dados
                    TodoAppDatabase::class.java,
                    // Nome do arquivo de banco de dados
                    "task_database"
                )
                    // Configura o fallback para destruição de dados e recriação do banco ao invés de migração
                    .fallbackToDestructiveMigration()
                    // Cria a instância do banco de dados
                    .build()
                // Armazena a instância na variável INSTANCE
                INSTANCE = instance
                // Retorna a instância criada
                instance
            }
        }
    }
}
