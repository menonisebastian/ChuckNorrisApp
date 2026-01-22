package com.menonisebastian.chucknorrisapp.data.model

import com.google.gson.annotations.SerializedName

data class JokeSearchResponse(
    @SerializedName("total")
    val total: Int,

    @SerializedName("result")
    val result: List<Joke>
)