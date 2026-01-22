package com.menonisebastian.chucknorrisapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

// Usamos la misma clase para API, Room y Firestore para simplificar
@Entity(tableName = "favorite_jokes")
data class Joke(
    @PrimaryKey
    @SerializedName("id")
    val id: String = "",

    @SerializedName("value")
    val value: String = "",

    @SerializedName("url")
    val url: String = "",

    // Campo auxiliar para saber si es favorito en la UI (no viene de la API)
    var isFavorite: Boolean = false
)