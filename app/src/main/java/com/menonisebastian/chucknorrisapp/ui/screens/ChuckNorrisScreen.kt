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
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.menonisebastian.chucknorrisapp.data.model.Joke
import com.menonisebastian.chucknorrisapp.ui.MainViewModel
import com.menonisebastian.chucknorrisapp.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: MainViewModel) {
    val categories by viewModel.categories.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val categoryJokes by viewModel.categoryJokes.collectAsState()
    val favorites by viewModel.favorites.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    var expanded by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = { Image(painter = painterResource(id = R.drawable.logolarge),
                    modifier = Modifier
                        .width(100.dp)
                        .padding(vertical = 20.dp),
                    contentDescription = "logo texto") },
//                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
//                    containerColor = MaterialTheme.colorScheme.primaryContainer,
//                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
//                )
            )
        }
    ) { paddingValues ->

        // Usamos una única LazyColumn para manejar todo el contenido scrolleable
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            // --- SECCIÓN 1: SELECTOR DE CATEGORÍA ---
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text("Selecciona una Categoría:", style = MaterialTheme.typography.labelLarge)
                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = selectedCategory?.replaceFirstChar { it.uppercase() } ?: "Seleccionar...",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Categoría") },
                        trailingIcon = {
                            Icon(Icons.Default.ArrowDropDown, "Drop down")
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { expanded = true },
                        shape = RoundedCornerShape(16.dp),
                        enabled = false, // Deshabilitado para que el click lo maneje el Box
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledTextColor = MaterialTheme.colorScheme.onSurface,
                            disabledBorderColor = MaterialTheme.colorScheme.outline,
                            disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                    // Capa transparente clickable para abrir el menú
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .clickable { expanded = true }
                    )

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.fillMaxWidth(0.9f) // Ancho relativo al padre
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
            }

            // --- SECCIÓN 2: RESULTADOS DE LA CATEGORÍA ---
            item {
                Spacer(modifier = Modifier.height(24.dp))
                if (isLoading) {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else if (selectedCategory != null) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Resultados: ${categoryJokes.size} chistes",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            // Lista de chistes de la categoría seleccionada
            items(categoryJokes) { joke ->
                JokeItem(joke = joke, onToggleFavorite = { viewModel.toggleFavorite(joke) })
            }

            if (categoryJokes.isEmpty() && selectedCategory != null && !isLoading) {
                item {
                    Text(
                        "No se encontraron chistes para esta categoría.",
                        modifier = Modifier.padding(vertical = 16.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            // --- SECCIÓN 3: FAVORITOS ---
            item {
                HorizontalDivider(modifier = Modifier.padding(vertical = 24.dp))
                Text(
                    text = "Mis Favoritos Guardados (${favorites.size})",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            items(favorites) { joke ->
                FavoriteItem(joke = joke, onDelete = { viewModel.toggleFavorite(joke) })
            }

            if (favorites.isEmpty()) {
                item {
                    Text(
                        "Aún no tienes favoritos.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
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
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
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
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = joke.value,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyMedium
            )
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Favorite, contentDescription = "Eliminar de favoritos", tint = Color.Red)
            }
        }
    }
}