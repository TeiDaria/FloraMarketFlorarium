package com.example.floramarket

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.floramarket.cart.CartScreen
import com.example.floramarket.catalog.CatalogScreen
import com.example.floramarket.create.CreateBouquetScreen
import com.example.floramarket.product.FlowerDetailScreen
import com.example.floramarket.viewmodel.FlowerViewModel

@Composable
fun FlowerApp(viewModel: FlowerViewModel){
    val selectedFlower = viewModel.selectedFlower
    val cameFromCart = viewModel.isCartOpen
    var isCreatingBouquet by remember { mutableStateOf(false) }

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
        selectedFlower != null -> {
            FlowerDetailScreen(
                flower = selectedFlower,
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
                }
            )
        }
        else -> {
            CatalogScreen(
                flowers = viewModel.allFlowers,
                cartItemCount = viewModel.getItemCount(),
                onFlowerClick = {viewModel.selectFlower(it)},
                onCartClick = {viewModel.toggleCart()},
                onCreateBouquetClick = { isCreatingBouquet = true }
            )
        }
    }
}

