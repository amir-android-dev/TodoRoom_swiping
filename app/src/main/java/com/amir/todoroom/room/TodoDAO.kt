package com.amir.todoroom.room

import androidx.room.*
import kotlinx.coroutines.flow.Flow


@Dao
interface TodoDAO {

    @Insert
    suspend fun insert(todoEntity: TodoEntity)

    @Update
    suspend fun update(todoEntity: TodoEntity)

    @Delete
    suspend fun delete(todoEntity: TodoEntity)

    @Query("select * from `todo-table`")
    fun fetchAllTodos():Flow<List<TodoEntity>>


    @Query("select * from `todo-table` where id=:id")
    fun fetchTodoById(id:Int):Flow<TodoEntity>


}