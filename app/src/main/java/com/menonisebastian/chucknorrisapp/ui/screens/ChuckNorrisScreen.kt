package com.menonisebastian.chucknorrisapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.menonisebastian.chucknorrisapp.data.model.Joke
import com.menonisebastian.chucknorrisapp.ui.MainViewModel
import com.menonisebastian.chucknorrisapp.R
import com.menonisebastian.chucknorrisapp.ui.theme.AppColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: MainViewModel) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Inicio, 1: Favoritos

    // --- ESTADOS PARA EL FAB Y DIÁLOGO ---
    var showDialog by remember { mutableStateOf(false) }
    // Observamos el chiste "actual" (individual) que genera el ViewModel
    val currentRandomJoke by viewModel.currentJoke.collectAsState()
    val categoriaActual by viewModel.selectedCategory.collectAsState()

    // --- DIÁLOGO DE CHISTE ALEATORIO ---
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = {

                Column () {
                    Text(
                        text = "Chiste Aleatorio",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text("Categoria: ${categoriaActual?.replaceFirstChar { it.uppercase() } ?: "Random"}", fontSize = MaterialTheme.typography.bodySmall.fontSize)
                }
            },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    if (currentRandomJoke != null) {
                        Text(
                            text = currentRandomJoke!!.value,
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center
                        )
                        // Botón de favorito dentro del diálogo (Opcional)
                        IconButton(onClick = { viewModel.toggleFavorite(currentRandomJoke!!) }) {
                            Icon(
                                imageVector = if (currentRandomJoke!!.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Favorito",
                                tint = if (currentRandomJoke!!.isFavorite) Color.Red else Color.Gray
                            )
                        }
                    } else {
                        CircularProgressIndicator()
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = AppColors.Orange)
                ) {
                    Text("Cerrar")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { viewModel.fetchJoke() }
                ) {
                    Text("Otro")
                }
            },
            containerColor = AppColors.White
        )
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        // --- AQUÍ AÑADIMOS EL FAB ---
        floatingActionButton = {
            // Solo mostramos el botón si estamos en la pestaña 0 (Categorías)
            if (selectedTab == 0) {
                FloatingActionButton(
                    onClick = {
                        viewModel.fetchJoke() // Llama a la función para traer un chiste nuevo
                        showDialog = true     // Muestra el diálogo
                    },
                    containerColor = AppColors.Orange,
                    contentColor = Color.White
                ) {
                    Icon(Icons.Default.Shuffle, contentDescription = "Chiste Random")
                }
            }
        },
        bottomBar = {
            NavigationBar(
                containerColor = AppColors.White
            ) {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Category, contentDescription = "Inicio") },
                    label = { Text("Categorías") },
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = AppColors.SoftOrange
                    )
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Favorite, contentDescription = "Favoritos") },
                    label = { Text("Favoritos") },
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = AppColors.SoftBlue
                    )
                )
            }
        }
    ) { paddingValues ->
        // Columna principal que contiene la CABECERA FIJA y el CONTENIDO VARIABLE
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // --- CABECERA COMPARTIDA (LOGO) ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logolarge),
                    contentDescription = "Logo Chuck Norris",
                    modifier = Modifier.width(150.dp)
                )
            }

            // --- CONTENIDO VARIABLE ---
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                when (selectedTab) {
                    0 -> SearchScreenContent(viewModel)
                    1 -> FavoritesScreenContent(viewModel)
                }
            }
        }
    }
}

// --- PANTALLA 1: BÚSQUEDA Y CATEGORÍAS ---
@Composable
fun SearchScreenContent(viewModel: MainViewModel) {
    val categories by viewModel.categories.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val categoryJokes by viewModel.categoryJokes.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        // Selector de Categoría
        Text("Selecciona una Categoría:", style = MaterialTheme.typography.labelLarge)
        Spacer(modifier = Modifier.height(8.dp))

        Box(modifier = Modifier.fillMaxWidth()) {
            TextField(
                value = selectedCategory?.replaceFirstChar { it.uppercase() } ?: "Seleccionar...",
                onValueChange = {},
                readOnly = true,
                label = { Text("Categoría") },
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .fillMaxWidth()
                    .clickable { expanded = true },
                trailingIcon = { Icon(Icons.Default.ArrowDropDown, "Drop down") },
                shape = RoundedCornerShape(12.dp),
                enabled = false,
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    disabledContainerColor = AppColors.SoftBlue,
                    disabledTextColor = AppColors.DarkBlue,
                    disabledLabelColor = MaterialTheme.colorScheme.onSurface,
                )
            )

            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { expanded = true }
            )

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.fillMaxWidth(0.9f).background(AppColors.White)
            ) {
                categories.forEach { category ->
                    DropdownMenuItem(
                        text = { Text(text = category.replaceFirstChar { it.uppercase() }) },
                        onClick = {
                            viewModel.selectCategory(category)
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- LISTA DE RESULTADOS ---
        Text(
            text = if (selectedCategory != null) "Resultados (${categoryJokes.size})" else "Resultados",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = AppColors.Orange
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (isLoading) {
            Box(modifier = Modifier.fillMaxWidth().padding(20.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 80.dp) // Extra padding para que el FAB no tape el último item
        ) {
            if (categoryJokes.isEmpty() && !isLoading) {
                item {
                    Text(
                        "Selecciona una categoría para ver chistes.",
                        modifier = Modifier.padding(top = 20.dp).fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        color = Color.Gray
                    )
                }
            }
            items(categoryJokes) { joke ->
                JokeItem(joke = joke, onToggleFavorite = { viewModel.toggleFavorite(joke) })
            }
        }
    }
}

// --- PANTALLA 2: FAVORITOS ---
@Composable
fun FavoritesScreenContent(viewModel: MainViewModel) {
    val favorites by viewModel.favorites.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = "Mis Favoritos (${favorites.size})",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = AppColors.DarkBlue
        )

        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            if (favorites.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillParentMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            "No tienes favoritos guardados aún.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.Gray
                        )
                    }
                }
            }
            items(favorites) { joke ->
                FavoriteItem(joke = joke, onDelete = { viewModel.toggleFavorite(joke) })
            }
        }
    }
}

@Composable
fun JokeItem(joke: Joke, onToggleFavorite: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = AppColors.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = joke.value,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyMedium
            )
            IconButton(onClick = onToggleFavorite) {
                Icon(
                    imageVector = if (joke.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Favorito",
                    tint = if (joke.isFavorite) Color.Red else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun FavoriteItem(joke: Joke, onDelete: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = AppColors.SoftOrange),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = joke.value,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyMedium,
            )
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Favorite, contentDescription = "Eliminar", tint = AppColors.Orange)
            }
        }
    }
}