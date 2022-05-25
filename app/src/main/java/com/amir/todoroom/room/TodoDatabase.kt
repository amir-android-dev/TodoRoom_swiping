package com.amir.todoroom.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(entities = [TodoEntity::class], version = 1)
abstract class TodoDatabase : RoomDatabase() {

    abstract fun todoDao(): TodoDAO

    companion object {
        /**
         *  this will keep a reference to any database returned via get instance.
         * This will help us to avoid repeatedly initializing the database, which is expensive in terms of performance
         */
        @Volatile
        private var INSTANCE: TodoDatabase? = null

//helper function to get the database
        /**
         * if a database has already been retrieved, the previous database will be returned.
         * Otherwise, we're going to create a new database.
         */
        fun getInstance(context: Context): TodoDatabase {

            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        TodoDatabase::class.java,
                        "todo_database"
                    ).fallbackToDestructiveMigration()
                        .build()

                    INSTANCE = instance
                }
                return instance
            }

        }
    }
}