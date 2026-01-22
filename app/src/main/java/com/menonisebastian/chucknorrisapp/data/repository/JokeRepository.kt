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
    // Flujo de favoritos desde Room (Fuente de verdad local)
    val favoriteJokes: Flow<List<Joke>> = dao.getFavoriteJokes()

    suspend fun getCategories(): List<String> {
        return api.getCategories()
    }

    // Obtener un chiste aleatorio (opcionalmente filtrado por categoría)
    suspend fun getRandomJoke(category: String?): Joke {
        // 1. Llamada a la API
        val joke = api.getRandomJoke(category)

        // 2. IMPORTANTE: Verificar si ya existe en favoritos localmente
        // Esto asegura que si sale un chiste que ya guardaste, el corazón salga rojo
        joke.isFavorite = dao.isFavorite(joke.id)

        return joke
    }

    // Búsqueda de chistes por texto/categoría (Para la lista principal)
    suspend fun searchJokesByCategory(category: String): List<Joke> {
        return try {
            val response = api.searchJokes(category)
            val jokes = response.result

            // Verificamos estado de favorito para cada item de la lista
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
            // Eliminar de Room y Firestore
            dao.deleteFavorite(joke)
            firestore.removeFavorite(joke.id)
        } else {
            // Guardar en Room y Firestore
            val jokeToSave = joke.copy(isFavorite = true)
            dao.insertFavorite(jokeToSave)
            firestore.saveFavorite(jokeToSave)
        }
    }
}