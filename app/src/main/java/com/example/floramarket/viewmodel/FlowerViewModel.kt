package com.example.floramarket.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.floramarket.model.BouquetDraft
import com.example.floramarket.model.Flower

class FlowerViewModel: ViewModel() {
    private val _allFlowers = mutableStateListOf<Flower>()
    val allFlowers: List<Flower> = _allFlowers // ← Только для чтения

    var selectedFlower by mutableStateOf<Flower?>(null)
        private set

    var cartItems by mutableStateOf<List<Pair<Flower,Int>>>(emptyList())
        private set

    var isCartOpen by mutableStateOf(false)

    fun selectFlower(flower: Flower){
        selectedFlower = flower
        isCartOpen = false
    }

    fun closeDetail(){
        selectedFlower = null
    }

    fun addToCart(flower: Flower) :Boolean {
        val existingIndex = cartItems.indexOfFirst { it.first.id == flower.id }
        val currentQuantity = cartItems.find { it.first.id == flower.id }?.second ?: 0

        if (currentQuantity >= flower.availableQuantity) {
            return false
        }

        if (existingIndex != -1){
            val updatedItems = cartItems.toMutableList()
            val (existingFlower, quantity) = updatedItems[existingIndex]
            updatedItems[existingIndex] = existingFlower to (quantity+1)
            cartItems = updatedItems
        } else {
            cartItems = cartItems + (flower to 1)
        }
        return true
    }

    fun removeFromCart(flower: Flower){
        cartItems = cartItems.filter{it.first.id != flower.id}
    }

    fun updateQuantity(flower: Flower, newQuantity: Int): Boolean{
        if (newQuantity <= 0){
            removeFromCart(flower)
            return true
        } else if (newQuantity > flower.availableQuantity) {
            return false
        } else{
            cartItems = cartItems.map{
                if (it.first.id == flower.id) it.first to newQuantity
                else it
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

    fun openCart(){
        isCartOpen = true
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

        _allFlowers.add(newFlower)
    }
}
