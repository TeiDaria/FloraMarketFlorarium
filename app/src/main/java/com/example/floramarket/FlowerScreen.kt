package com.example.floramarket

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun FlowerApp(viewModel: FlowerViewModel){
    val selectedFlower = viewModel.selectedFlower

    when {
        selectedFlower != null -> {
            FlowerDetailScreen(
                flower = selectedFlower,
                onClose = {viewModel.closeDetail()},
                onAddToCart = {viewModel.addToCart(it)},
                onUpdateQuantity = { flower, qty -> viewModel.updateQuantity(flower,qty)},
                quantityInCart = viewModel.getQuantityInCart(selectedFlower.id)
            )
        }
        viewModel.isCartOpen -> {
            CartScreen(
                cartItems = viewModel.cartItems,
                totalPrice = viewModel.getTotalPrice(),
                onClose = {viewModel.toggleCart()},
                onRemoveItem = {viewModel.removeFromCart(it)},
                onUpdateQuantity = { flower, qty -> viewModel.updateQuantity(flower,qty)}
            )
        }
        else -> {
            CatalogScreen(
                flowers = SampleData.flowers,
                cartItemCount = viewModel.getItemCount(),
                onFlowerClick = {viewModel.selectFlower(it)},
                onCartClick = {viewModel.toggleCart()}
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogScreen(
    flowers: List<Flower>,
    cartItemCount: Int,
    onFlowerClick: (Flower) -> Unit,
    onCartClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Товары") },
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = {
                        Icon(
                            Icons.Default.Home,
                            contentDescription = "Главная"
                        )
                    },
                    label = { Text("Главная")},
                    selected = true,
                    onClick = {/* Уже на главной */}
                )

                NavigationBarItem(
                    icon = {
                        Box{
                            Icon(
                                Icons.Default.ShoppingCart,
                                contentDescription = "Корзина"
                            )
                            if (cartItemCount>0){
                                Badge(
                                    modifier = Modifier.align(Alignment.TopEnd)
                                ){
                                    Text(cartItemCount.toString())
                                }
                            }
                        }
                    },
                    label = {Text("Корзина")},
                    selected = false,
                    onClick = onCartClick
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
    ){ paddingValues ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(paddingValues)
        ) {
            items(flowers) { flower ->
                FlowerCard(
                    flower = flower,
                    onClick = { onFlowerClick(flower) }
                )
            }
        }
    }
}

@Composable
fun FlowerCard(
    flower: Flower,
    onClick: () -> Unit
){
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ){
        Column {
            // Картинка (пока просто цветной прямоугольник, если нет URL)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .background(Color(0xFFF5F5F5))
                    .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)),
                contentAlignment = Alignment.Center
            ){
                // Можно использовать AsyncImage если есть реальные URL
                // AsyncImage(model = flower.imageUrl, contentDescription = flower.name)
                Text(
                    text = "🌸",
                    fontSize = 48.sp
                )
            }
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = flower.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "%.2f ₽".format(flower.price),
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlowerDetailScreen(
    flower: Flower,
    onClose: () -> Unit,
    onAddToCart: (Flower) -> Unit,
    onUpdateQuantity: (Flower, Int) -> Unit,
    quantityInCart: Int
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(flower.name)},
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) {
        paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            // Большая картинка
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .background(Color(0xFFF5F5F5))
                    .clip(RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ){
                Text(
                    text = "🌺",
                    fontSize = 120.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = flower.name,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "%.2f ₽".format(flower.price),
                fontSize = 24.sp,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = flower.description,
                fontSize = 16.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.weight(1f))

            if (quantityInCart==0){
                Button(
                    onClick = { onAddToCart(flower) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp)
                ){
                    Text(
                        text = "Добавить в корзину",
                        fontSize = 18.sp
                    )
                }
            } else {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ){
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 8.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        IconButton(
                            onClick = {
                                onUpdateQuantity(flower, quantityInCart-1)
                            },
                            modifier = Modifier.size(48.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Remove,
                                contentDescription = "Уменьшить",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }

                        Text(
                            text = quantityInCart.toString(),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        IconButton(
                            onClick = {
                                onUpdateQuantity(flower, quantityInCart + 1)
                            },
                            modifier = Modifier.size(48.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Увеличить",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    cartItems: List<Pair<Flower, Int>>,
    totalPrice: Double,
    onClose: () -> Unit,
    onRemoveItem: (Flower) -> Unit,
    onUpdateQuantity: (Flower, Int) -> Unit,
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
                        onQuantityChange = { newQty -> onUpdateQuantity(flower, newQty)}
                    )
                }
            }
        }
    }
}

@Composable
fun CartItemCard(
    flower: Flower,
    quantity: Int,
    onRemove: () -> Unit,
    onQuantityChange: (Int) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Маленькая иконка
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .background(Color(0xFFF5F5F5))
                    .clip(RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text("💮", fontSize = 30.sp)
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Информация о товаре
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = flower.name,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "%.2f ₽".format(flower.price),
                    color = MaterialTheme.colorScheme.primary
                )
            }

            // Управление количеством
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = { onQuantityChange(quantity - 1) }
                ) {
                    Icon(
                        imageVector = Icons.Filled.Remove,
                        contentDescription = "Уменьшить"
                    )
                }

                Text(
                    text = quantity.toString(),
                    modifier = Modifier.width(24.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )

                IconButton(
                    onClick = { onQuantityChange(quantity + 1) }
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Увеличить"
                    )
                }

                IconButton(onClick = onRemove) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Удалить",
                        tint = Color.Red
                    )
                }
            }
        }
    }
}