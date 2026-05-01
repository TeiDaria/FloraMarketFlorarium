package com.example.floramarket.favorite

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.floramarket.model.Flower
import com.example.floramarket.catalog.FlowerCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    favoriteFlowers: List<Flower>,
    cartItemCount: Int,
    onFlowerClick: (Flower) -> Unit,
    onCartClick: () -> Unit,
    onNavigateToHome: () -> Unit,
    onToggleFavorite: (String) -> Unit,
    isFavorite: (String) -> Boolean,
    onNavigateToProfile: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Избранное") }
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = "Главная") },
                    label = { Text("Главная") },
                    selected = false,
                    onClick = onNavigateToHome
                )

                NavigationBarItem(
                    icon = {
                        Box {
                            Icon(
                                Icons.Default.ShoppingCart,
                                contentDescription = "Корзина",
                            )
                            if (cartItemCount > 0) {
                                Badge(modifier = Modifier.align(Alignment.TopEnd)) {
                                    Text(cartItemCount.toString())
                                }
                            }
                        }
                    },
                    label = { Text("Корзина") },
                    selected = false,
                    onClick = onCartClick
                )

                NavigationBarItem(
                    icon = { Icon(Icons.Default.Favorite, contentDescription = "Избранное") },
                    label = { Text("Избранное") },
                    selected = true,
                    onClick = { /* Уже здесь */ }
                )

                NavigationBarItem(
                    icon = { Icon(Icons.Default.Person, contentDescription = "Профиль") },
                    label = { Text("Профиль") },
                    selected = false,
                    onClick = onNavigateToProfile
                )
            }
        }
    ) { paddingValues ->
        if (favoriteFlowers.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "🤍", fontSize = 72.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "В избранном пока пусто",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Нажмите на сердечко на карточке товара\nчтобы добавить его в избранное",
                        fontSize = 16.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(paddingValues)
            ) {
                items(favoriteFlowers, key = { it.id }) { flower ->
                    FlowerCard(
                        flower = flower,
                        isFavorite = isFavorite(flower.id),
                        onFavoriteClick = { onToggleFavorite(flower.id) },
                        onClick = { onFlowerClick(flower) }
                    )
                }
            }
        }
    }
}