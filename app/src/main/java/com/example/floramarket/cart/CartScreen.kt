package com.example.floramarket.cart

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.floramarket.model.Flower

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    cartItems: List<Pair<Flower, Int>>,
    totalPrice: Double,
    onClose: () -> Unit,
    onRemoveItem: (Flower) -> Unit,
    onUpdateQuantity: (Flower, Int) -> Boolean,
    onFlowerClick: (Flower) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Корзина")},
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        },
        bottomBar = {
            Column {
                if (cartItems.isNotEmpty()){
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                    ){
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ){
                            Column {
                                Text(
                                    text = "Итого:",
                                    fontSize = 14.sp,
                                    color = Color.Gray
                                )
                                Text(
                                    text = "%.2f ₽".format(totalPrice),
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }

                            Button(
                                onClick = {
                                    /* Здесь будет оформление заказа */
                                },
                                modifier = Modifier.height(48.dp)
                            ) {
                                Text("Оформить")
                            }
                        }
                    }
                }

                NavigationBar {
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Home,
                            contentDescription = "Главная") },
                        label = { Text("Главная")},
                        selected = false,
                        onClick = onClose
                    )

                    NavigationBarItem(
                        icon = {
                            Box{
                                Icon(
                                    Icons.Default.ShoppingCart,
                                    contentDescription = "Корзина"
                                )
                                if (cartItems.isNotEmpty()){
                                    Badge(
                                        modifier = Modifier.align(Alignment.TopEnd)
                                    ){
                                        Text(cartItems.sumOf { it.second }.toString())
                                    }
                                }
                            }
                        },
                        label = {Text("Корзина")},
                        selected = true,
                        onClick = { /* Уже в корзине */ }
                    )

                    NavigationBarItem(
                        icon = {
                            Icon(
                                Icons.Default.Favorite,
                                contentDescription = "Избранное"
                            )
                        },
                        label = { Text("Избранное")},
                        selected = false,
                        onClick = { /* Будет позже */ }
                    )

                    NavigationBarItem(
                        icon = {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = "Профиль"
                            )
                        },
                        label = { Text("Профиль") },
                        selected = false,
                        onClick = { /* Будет позже */ }
                    )
                }
            }
        }
    ) { paddingValues ->
        if (cartItems.isEmpty()){
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ){
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "🛒",
                        fontSize = 64.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Корзина пуста",
                        fontSize = 20.sp,
                        color = Color.Gray
                    )
                }
            }
        } else {
            // Список товаров в корзине
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ){
                items(cartItems) { cartItem ->
                    val (flower, quantity) = cartItem
                    CartItemCard(
                        flower = flower,
                        quantity = quantity,
                        onRemove = { onRemoveItem(flower) },
                        onQuantityChange = { newQty -> onUpdateQuantity(flower, newQty)},
                        onClick = { onFlowerClick(flower) }
                    )
                }
            }
        }
    }
}