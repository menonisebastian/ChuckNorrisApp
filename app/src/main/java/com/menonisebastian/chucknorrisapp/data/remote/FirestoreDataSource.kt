package com.menonisebastian.chucknorrisapp.data.remote

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.menonisebastian.chucknorrisapp.data.model.Joke

class FirestoreDataSource {
    private val db = FirebaseFirestore.getInstance()
    private val collection = db.collection("favorites")

    fun saveFavorite(joke: Joke) {
        collection.document(joke.id).set(joke)
            .addOnSuccessListener { Log.d("Firestore", "Joke saved: ${joke.id}") }
            .addOnFailureListener { e -> Log.e("Firestore", "Error saving", e) }
    }

    fun removeFavorite(jokeId: String) {
        collection.document(jokeId).delete()
            .addOnSuccessListener { Log.d("Firestore", "Joke deleted: $jokeId") }
    }
}