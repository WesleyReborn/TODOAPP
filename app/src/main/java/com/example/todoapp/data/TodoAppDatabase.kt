package com.example.todoapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.todoapp.data.dao.TaskDao
import com.example.todoapp.data.model.Task

@Database(entities = [Task::class], version = 2, exportSchema = false)
abstract class TodoAppDatabase : RoomDatabase() {

    abstract fun taskDao() : TaskDao

    companion object {
        @Volatile
        private var INSTANCE: TodoAppDatabase? = null

        fun getDatabase(context: Context) : TodoAppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TodoAppDatabase::class.java,
                    "task_database"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
