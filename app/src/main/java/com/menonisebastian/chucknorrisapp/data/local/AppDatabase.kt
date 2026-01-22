package com.menonisebastian.chucknorrisapp.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.menonisebastian.chucknorrisapp.data.model.Joke

@Database(entities = [Joke::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun jokeDao(): JokeDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "chuck_app_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}