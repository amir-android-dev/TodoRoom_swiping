package com.amir.todoroom.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "todo-table")
data class TodoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String = "",
    val description: String = ""
)