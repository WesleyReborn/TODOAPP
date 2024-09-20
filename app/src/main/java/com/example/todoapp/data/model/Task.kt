package com.example.todoapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class Task(
    // Chave primária da entidade, identificador único para cada tarefa
    @PrimaryKey val id : String = "",
    // Título da tarefa
    val title : String = "",
    // Descrição da tarefa
    val description : String = "",
    // Indica se a tarefa está sincronizada
    val isSynch : Boolean = false
)
