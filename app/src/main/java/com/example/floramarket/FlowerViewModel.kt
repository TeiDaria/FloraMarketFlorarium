package com.example.floramarket

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class FlowerViewModel: ViewModel() {

    private val _allFlowers = mutableStateOf(SampleData.flowers)
    val allFlowers:List<Flower>
        get() = _allFlowers.value

    var selectedFlower by mutableStateOf<Flower?>(null)
        private set

    var cartItems by mutableStateOf<List<Pair<Flower,Int>>>(emptyList())
        private set

    var isCartOpen by mutableStateOf(false)
        private set

    fun selectFlower(flower:Flower){
        selectedFlower = flower
        isCartOpen = false
    }

    fun closeDetail(){
        selectedFlower = null
    }

    fun addToCart(flower: Flower){
        val existingIndex = cartItems.indexOfFirst { it.first.id == flower.id }

        if (existingIndex != -1){
            //Товар уже есть, увеличиваем количество
            val updatedItems = cartItems.toMutableList()
            val (existingFlower, quantity) = updatedItems[existingIndex]
            updatedItems[existingIndex] = existingFlower to (quantity+1)
            cartItems = updatedItems
        } else {
            cartItems = cartItems + (flower to 1)
        }
    }

    fun removeFromCart(flower: Flower){
        cartItems = cartItems.filter{it.first.id != flower.id}
    }

    fun updateQuantity(flower:Flower, newQuantity: Int){
        if (newQuantity <= 0){
            removeFromCart(flower)
        } else{
            cartItems = cartItems.map{
                if (it.first.id == flower.id) it.first to newQuantity
                else it
            }
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
            imageUrl = bouquet.imageUrls.firstOrNull() ?: "",
            imageUrls = bouquet.imageUrls,
            description = bouquet.fullDescription
        )

        _allFlowers.value = _allFlowers.value + newFlower
    }
}
