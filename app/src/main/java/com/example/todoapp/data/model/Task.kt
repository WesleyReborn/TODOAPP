package com.example.todoapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

// Define uma entidade chamada 'Task' que representa uma tabela no banco de dados com o nome 'tasks'
@Entity(tableName = "tasks")
data class Task(
    // Chave primária da entidade, identificador único para cada tarefa
    @PrimaryKey val id : String = "",
    // Título da tarefa
    val title : String = "",
    // Descrição detalhada da tarefa
    val description : String = "",
    // Indica se a tarefa está sincronizada com o backend (false significa não sincronizada)
    val isSynch : Boolean = false,
    // Timestamp que registra o momento da última modificação na tarefa (usado para controle de versão e sincronização)
    val timestamp : Long = System.currentTimeMillis(),
    // Identificador do usuário associado à tarefa
    val userId : String = "" // Associar cada tarefa ao usuário que a criou
)
