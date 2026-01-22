package com.menonisebastian.chucknorrisapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.menonisebastian.chucknorrisapp.ui.theme.ChuckNorrisAppTheme
import com.menonisebastian.chucknorrisapp.data.local.AppDatabase
import com.menonisebastian.chucknorrisapp.data.network.RetrofitClient
import com.menonisebastian.chucknorrisapp.data.remote.FirestoreDataSource
import com.menonisebastian.chucknorrisapp.data.repository.JokeRepository
import com.menonisebastian.chucknorrisapp.ui.MainViewModel
import com.menonisebastian.chucknorrisapp.ui.MainViewModelFactory
import com.menonisebastian.chucknorrisapp.ui.screens.HomeScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // Inicialización manual de dependencias
        val database = AppDatabase.getDatabase(this)
        val api = RetrofitClient.api
        val firestore = FirestoreDataSource() // Instanciamos la clase helper de Firebase

        val repository = JokeRepository(api, database.jokeDao(), firestore)
        val viewModelFactory = MainViewModelFactory(repository)

        setContent {
            val viewModel: MainViewModel = viewModel(factory = viewModelFactory)
            ChuckNorrisAppTheme {
                HomeScreen(viewModel)
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ChuckNorrisAppTheme {
        Greeting("Android")
    }
}