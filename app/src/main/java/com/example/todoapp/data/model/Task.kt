package com.example.todoapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey val id : String,
    val title : String,
    val description : String,
    val completed : Boolean
)
