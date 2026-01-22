package com.menonisebastian.chucknorrisapp.data.network

import com.menonisebastian.chucknorrisapp.data.model.Joke
import com.menonisebastian.chucknorrisapp.data.model.JokeSearchResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface ChuckNorrisApiService {
    @GET("jokes/random")
    suspend fun getRandomJoke(@Query("category") category: String?): Joke

    @GET("jokes/categories")
    suspend fun getCategories(): List<String>

    // Nuevo endpoint para buscar chistes (usado para listar por categoría)
    @GET("jokes/search")
    suspend fun searchJokes(@Query("query") query: String): JokeSearchResponse
}

object RetrofitClient {
    private const val BASE_URL = "https://api.chucknorris.io/"

    val api: ChuckNorrisApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ChuckNorrisApiService::class.java)
    }
}