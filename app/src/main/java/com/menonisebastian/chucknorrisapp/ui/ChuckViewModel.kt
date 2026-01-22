package com.menonisebastian.chucknorrisapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.menonisebastian.chucknorrisapp.data.model.Joke
import com.menonisebastian.chucknorrisapp.data.repository.JokeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(private val repository: JokeRepository) : ViewModel() {

    // Lista de categorías
    private val _categories = MutableStateFlow<List<String>>(emptyList())
    val categories: StateFlow<List<String>> = _categories.asStateFlow()

    // Categoría seleccionada
    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory.asStateFlow()

    // Lista de chistes de la categoría actual (Resultado de la búsqueda)
    private val _categoryJokes = MutableStateFlow<List<Joke>>(emptyList())
    val categoryJokes: StateFlow<List<Joke>> = _categoryJokes.asStateFlow()

    // Estado de carga
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Favoritos
    val favorites: StateFlow<List<Joke>> = repository.favoriteJokes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        loadCategories()
    }

    private fun loadCategories() {
        viewModelScope.launch {
            try {
                _categories.value = repository.getCategories()
            } catch (e: Exception) {
                // Manejar error
            }
        }
    }

    fun selectCategory(category: String) {
        _selectedCategory.value = category
        searchJokesInCategory(category)
    }

    private fun searchJokesInCategory(category: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val jokes = repository.searchJokesByCategory(category)
                _categoryJokes.value = jokes
            } catch (e: Exception) {
                _categoryJokes.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun toggleFavorite(joke: Joke) {
        viewModelScope.launch {
            repository.toggleFavorite(joke)

            // Actualizar el estado visual en la lista de búsqueda
            val updatedList = _categoryJokes.value.map {
                if (it.id == joke.id) it.copy(isFavorite = !joke.isFavorite) else it
            }
            _categoryJokes.value = updatedList
        }
    }
}

class MainViewModelFactory(private val repository: JokeRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}