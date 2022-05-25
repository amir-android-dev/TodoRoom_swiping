package com.amir.todoroom.room

import android.app.Application

class TodoApp : Application(){

    val db by lazy {
        TodoDatabase.getInstance(this)
    }
}