package com.example.floramarket.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.floramarket.data.AppDatabase
import com.example.floramarket.model.BouquetDraft
import com.example.floramarket.model.Flower
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class FlowerViewModel(application: Application): AndroidViewModel(application) {

    private val flowerDao = AppDatabase.getDatabase(application).flowerDao()
    val allFlowers = mutableStateListOf<Flower>()
    val favoriteIds = mutableStateListOf<String>()

    val favoriteFlowers: List<Flower>
        get() = allFlowers.filter { it.isFavorite }

    var selectedFlower by mutableStateOf<Flower?>(null)
        private set

    var cartItems by mutableStateOf<List<Pair<Flower,Int>>>(emptyList())
        private set

    var isCartOpen by mutableStateOf(false)

    init {
        // Загружаем цветы из БД
        viewModelScope.launch {
            flowerDao.getAllFlowers().collectLatest { flowers ->
                allFlowers.clear()
                allFlowers.addAll(flowers)

                // Обновляем избранное
                favoriteIds.clear()
                favoriteIds.addAll(flowers.filter { it.isFavorite }.map { it.id })

                // Обновляем корзину
                cartItems = flowers
                    .filter { it.cartQuantity > 0 }
                    .map { it to it.cartQuantity }
            }
        }
    }

    fun isFavorite(flowerId: String): Boolean {
        return allFlowers.find { it.id == flowerId }?.isFavorite ?: false
    }

    fun toggleFavorite(flowerId: String){
        val flower = allFlowers.find { it.id == flowerId } ?: return
        val newFavorite = !flower.isFavorite

        viewModelScope.launch {
            flowerDao.updateFavorite(flowerId, newFavorite)
        }
    }

    fun selectFlower(flower: Flower){
        selectedFlower = flower
        isCartOpen = false
    }

    fun closeDetail(){
        selectedFlower = null
    }

    fun addToCart(flower: Flower) :Boolean {
        val currentQuantity = cartItems.find { it.first.id == flower.id }?.second ?: 0

        if (currentQuantity >= flower.availableQuantity) {
            return false
        }

        val newQuantity = currentQuantity + 1
        viewModelScope.launch {
            flowerDao.updateCartQuantity(flower.id, newQuantity)
        }
        return true
    }

    fun removeFromCart(flower: Flower){
        viewModelScope.launch {
            flowerDao.updateCartQuantity(flower.id, 0)
        }
    }

    fun updateQuantity(flower: Flower, newQuantity: Int): Boolean{
        if (newQuantity <= 0){
            removeFromCart(flower)
            return true
        } else if (newQuantity > flower.availableQuantity) {
            return false
        } else{
            viewModelScope.launch {
                flowerDao.updateCartQuantity(flower.id, newQuantity)
            }
            return true
        }
    }

    fun toggleCart(){
        isCartOpen = !isCartOpen
        if (isCartOpen){
            selectedFlower = null
        }
    }

    fun getTotalPrice(): Double {
        return cartItems.sumOf { it.first.price * it.second}
    }

    fun getItemCount(): Int {
        return cartItems.sumOf { it.second }
    }

    fun getQuantityInCart(flowerId: String): Int {
        return cartItems.find { it.first.id == flowerId }?.second ?: 0
    }

    fun selectFlowerFromCart(flower: Flower){
        selectedFlower = flower
    }

    fun addNewBouquet(bouquet: BouquetDraft) {
        val newFlower = Flower(
            id = System.currentTimeMillis().toString(),
            name = bouquet.name,
            price = bouquet.price ?: 0.0,
            mainImageUrl = bouquet.imageUrls.firstOrNull() ?: "",
            imageUrls = bouquet.imageUrls,
            description = bouquet.fullDescription,
            availableQuantity = bouquet.quantity ?: 1
        )

        viewModelScope.launch {
            flowerDao.insertFlower(newFlower)
        }
    }
}
