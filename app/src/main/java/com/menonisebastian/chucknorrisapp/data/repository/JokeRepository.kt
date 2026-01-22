package com.menonisebastian.chucknorrisapp.data.repository

import com.menonisebastian.chucknorrisapp.data.local.JokeDao
import com.menonisebastian.chucknorrisapp.data.model.Joke
import com.menonisebastian.chucknorrisapp.data.network.ChuckNorrisApiService
import com.menonisebastian.chucknorrisapp.data.remote.FirestoreDataSource
import kotlinx.coroutines.flow.Flow

class JokeRepository(
    private val api: ChuckNorrisApiService,
    private val dao: JokeDao,
    private val firestore: FirestoreDataSource
) {
    val favoriteJokes: Flow<List<Joke>> = dao.getFavoriteJokes()

    suspend fun getCategories(): List<String> = api.getCategories()

    // Búsqueda de chistes por texto (categoría)
    suspend fun searchJokesByCategory(category: String): List<Joke> {
        return try {
            val response = api.searchJokes(category)
            val jokes = response.result

            // Verificamos cuáles de estos ya están en favoritos
            // Nota: Para optimizar en listas grandes, idealmente haríamos una query "WHERE id IN (...)"
            // pero para este ejemplo iteramos.
            jokes.forEach { joke ->
                joke.isFavorite = dao.isFavorite(joke.id)
            }
            jokes
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun toggleFavorite(joke: Joke) {
        if (joke.isFavorite) {
            dao.deleteFavorite(joke)
            firestore.removeFavorite(joke.id)
        } else {
            val jokeToSave = joke.copy(isFavorite = true)
            dao.insertFavorite(jokeToSave)
            firestore.saveFavorite(jokeToSave)
        }
    }
}