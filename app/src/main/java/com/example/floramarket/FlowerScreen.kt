package com.example.floramarket

import android.widget.Toast
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
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
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun FlowerApp(viewModel: FlowerViewModel){
    val selectedFlower = viewModel.selectedFlower
    val cameFromCart = viewModel.isCartOpen
    var isCreatingBouquet by remember { mutableStateOf(false) }

    when {
        isCreatingBouquet -> {
            CreateBouquetScreen(
                onNavigateBack = { isCreatingBouquet = false},
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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogScreen(
    flowers: List<Flower>,
    cartItemCount: Int,
    onFlowerClick: (Flower) -> Unit,
    onCartClick: () -> Unit,
    onCreateBouquetClick: () -> Unit
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
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCreateBouquetClick,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Создать букет"
                )
            }
        }
    ){ paddingValues ->
        if (flowers.isEmpty()){
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(32.dp)
                ) {
                    Text(
                        text = "🌸",
                        fontSize = 72.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Здесь пока нет товаров",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Нажмите на кнопку '+' чтобы добавить свой первый букет",
                        fontSize = 16.sp,
                        color = Color.Gray,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = onCreateBouquetClick,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Добавить букет")
                    }
                }
            }
        }else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(paddingValues)
            ) {
                items(flowers, key = {it.id}) { flower ->
                    FlowerCard(
                        flower = flower,
                        onClick = { onFlowerClick(flower) }
                    )
                }
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
                    fontSize = 16.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    minLines = 2
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = formatPrice(flower.price),
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${flower.availableQuantity} шт.",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

fun formatPrice(price: Double): String {
    return when {
        price >= 1_000_000 -> "%.1fM ₽".format(price / 1_000_000)
        else -> "%.0f ₽".format(price)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlowerDetailScreen(
    flower: Flower,
    onClose: () -> Unit,
    onAddToCart: (Flower) -> Boolean,
    onUpdateQuantity: (Flower, Int) -> Boolean,
    quantityInCart: Int,
    cartItemCount: Int,
    cameFromCart: Boolean,
    onNavigateToHome: () -> Unit,
    onNavigateToCart: () -> Unit
) {
    val context = LocalContext.current

    val photos = flower.imageUrls.ifEmpty { listOf(flower.mainImageUrl) }
    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { flower.imageUrls.ifEmpty { listOf(flower.mainImageUrl) }.size }
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(flower.name) },
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                }
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
                    label = { Text("Главная") },
                    selected = !cameFromCart,
                    onClick = onNavigateToHome
                )

                NavigationBarItem(
                    icon = {
                        Box {
                            Icon(
                                Icons.Default.ShoppingCart,
                                contentDescription = "Корзина"
                            )
                            if (cartItemCount > 0) {
                                Badge(
                                    modifier = Modifier.align(Alignment.TopEnd)
                                ) {
                                    Text(cartItemCount.toString())
                                }
                            }
                        }
                    },
                    label = { Text("Корзина") },
                    selected = cameFromCart,
                    onClick = onNavigateToCart
                )

                NavigationBarItem(
                    icon = {
                        Icon(
                            Icons.Default.Favorite,
                            contentDescription = "Избранное"
                        )
                    },
                    label = { Text("Избранное") },
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
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
            ) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize()
                ) { page ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xFFF5F5F5)),
                        contentAlignment = Alignment.Center
                    ) {
                        // Здесь будет AsyncImage
                        Text("🌸", fontSize = 120.sp)
                    }
                }
                if (photos.size > 1) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(16.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.Black.copy(alpha = 0.6f))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "${pagerState.currentPage + 1}/${photos.size}",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Column(
                modifier = Modifier.padding(16.dp)
            ) {

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

                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "В наличии: ${flower.availableQuantity} шт.",
                        fontSize = 14.sp,
                        color = if (flower.availableQuantity > 0) Color(0xFF4CAF50) else Color.Red
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = flower.description,
                    fontSize = 16.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.weight(1f))

                if (quantityInCart == 0) {
                    Button(
                        onClick = {
                            val success = onAddToCart(flower)
                            if (!success) {
                                Toast.makeText(
                                    context,
                                    "Доступно только ${flower.availableQuantity} шт.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
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
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 8.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = {
                                    onUpdateQuantity(flower, quantityInCart - 1)
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
                                    val success = onUpdateQuantity(flower, quantityInCart + 1)
                                    if (!success) {
                                        Toast.makeText(
                                            context,
                                            "Доступно только ${flower.availableQuantity} шт.",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
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
}

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

@Composable
fun CartItemCard(
    flower: Flower,
    quantity: Int,
    onRemove: () -> Unit,
    onQuantityChange: (Int) -> Boolean,
    onClick: () -> Unit
) {
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
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
                    textAlign = TextAlign.Center
                )

                IconButton(
                    onClick = {
                        val success = onQuantityChange(quantity + 1)
                        if (!success){
                            Toast.makeText(
                                context,
                                "Доступно только ${flower.availableQuantity} шт.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
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