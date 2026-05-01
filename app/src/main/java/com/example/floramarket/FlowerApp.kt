package com.example.floramarket

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.floramarket.cart.CartScreen
import com.example.floramarket.catalog.CatalogScreen
import com.example.floramarket.create.CreateBouquetScreen
import com.example.floramarket.favorite.FavoritesScreen
import com.example.floramarket.product.FlowerDetailScreen
import com.example.floramarket.viewmodel.FlowerViewModel

@Composable
fun FlowerApp(viewModel: FlowerViewModel){
    val selectedFlower = viewModel.selectedFlower
    val cameFromCart = viewModel.isCartOpen
    var isCreatingBouquet by remember { mutableStateOf(false) }
    var showFavorites by remember {mutableStateOf(false)}

    when {
        isCreatingBouquet -> {
            CreateBouquetScreen(
                onNavigateBack = { isCreatingBouquet = false },
                onBouquetCreated = { draft ->
                    viewModel.addNewBouquet(draft)
                    isCreatingBouquet = false
                }
            )
        }
        showFavorites -> {
            FavoritesScreen(
                favoriteFlowers = viewModel.favoriteFlowers,
                cartItemCount = viewModel.getItemCount(),
                onFlowerClick = {
                    viewModel.selectFlower(it)
                    showFavorites = false
                },
                onCartClick = {
                    showFavorites = false
                    viewModel.isCartOpen = true
                },
                onNavigateToHome = { showFavorites= false },
                onToggleFavorite = {viewModel.toggleFavorite(it)},
                isFavorite = {viewModel.isFavorite(it)}
            )
        }
        selectedFlower != null -> {
            FlowerDetailScreen(
                flower = selectedFlower,
                isFavorite = viewModel.isFavorite(selectedFlower.id),
                onFavoriteClick = {viewModel.toggleFavorite(selectedFlower.id)},
                onClose = {viewModel.closeDetail()},
                onAddToCart = {flower -> viewModel.addToCart(flower)},
                onUpdateQuantity = { flower, qty -> viewModel.updateQuantity(flower,qty)},
                quantityInCart = viewModel.getQuantityInCart(selectedFlower.id),
                cartItemCount = viewModel.getItemCount(),
                cameFromCart = cameFromCart,
                onNavigateToHome = {
                    viewModel.closeDetail()
                    viewModel.isCartOpen = false
                },
                onNavigateToCart = {
                    viewModel.closeDetail()
                    viewModel.isCartOpen = true
                }
                ,
                onNavigateToFavorites = {
                    viewModel.closeDetail()
                    viewModel.isCartOpen = false
                    showFavorites = true
                }
            )
        }
        viewModel.isCartOpen -> {
            CartScreen(
                cartItems = viewModel.cartItems,
                totalPrice = viewModel.getTotalPrice(),
                onClose = {viewModel.toggleCart()},
                onRemoveItem = {viewModel.removeFromCart(it)},
                onUpdateQuantity = { flower, qty -> viewModel.updateQuantity(flower,qty)},
                onFlowerClick = { flower ->
                    viewModel.selectFlowerFromCart(flower)
                },
                onNavigateToFavorites = {
                    viewModel.isCartOpen = false
                    showFavorites = true
                }
            )
        }
        else -> {
            CatalogScreen(
                flowers = viewModel.allFlowers,
                cartItemCount = viewModel.getItemCount(),
                onFlowerClick = {
                    viewModel.selectFlower(it)
                },
                onCartClick = {viewModel.toggleCart()},
                onCreateBouquetClick = { isCreatingBouquet = true },
                onFavoriteClick = { showFavorites = true },
                isFavorite = { viewModel.isFavorite(it) },
                onToggleFavorite = { viewModel.toggleFavorite(it) },
            )
        }
    }
}

