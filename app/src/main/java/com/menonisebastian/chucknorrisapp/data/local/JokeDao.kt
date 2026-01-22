package com.menonisebastian.chucknorrisapp.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.menonisebastian.chucknorrisapp.data.model.Joke
import kotlinx.coroutines.flow.Flow

@Dao
interface JokeDao {
    @Query("SELECT * FROM favorite_jokes")
    fun getFavoriteJokes(): Flow<List<Joke>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(joke: Joke)

    @Delete
    suspend fun deleteFavorite(joke: Joke)

    @Query("SELECT EXISTS(SELECT * FROM favorite_jokes WHERE id = :id)")
    suspend fun isFavorite(id: String): Boolean
}