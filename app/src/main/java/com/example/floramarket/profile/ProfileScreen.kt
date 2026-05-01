package com.example.floramarket.profile

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.floramarket.viewmodel.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    userViewModel: UserViewModel = viewModel(),
    cartItemCount: Int,
    onNavigateToHome: () -> Unit,
    onNavigateToCart: () -> Unit,
    onNavigateToFavorites: () -> Unit
) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Профиль") }
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
                                modifier = Modifier.size(28.dp)
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
                    onClick = onNavigateToCart
                )

                NavigationBarItem(
                    icon = { Icon(Icons.Default.Favorite, contentDescription = "Избранное") },
                    label = { Text("Избранное") },
                    selected = false,
                    onClick = onNavigateToFavorites
                )

                NavigationBarItem(
                    icon = { Icon(Icons.Default.Person, contentDescription = "Профиль") },
                    label = { Text("Профиль") },
                    selected = true,
                    onClick = { /* Уже здесь */ }
                )
            }
        }
    ) { paddingValues ->
        if (userViewModel.user.isRegistered) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                // Аватар
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .padding(bottom = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = "Аватар",
                        modifier = Modifier.size(80.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                Text(
                    text = userViewModel.user.name,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Информация профиля
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        ProfileInfoRow(
                            icon = Icons.Default.Email,
                            label = "Email",
                            value = userViewModel.user.email
                        )
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                        ProfileInfoRow(
                            icon = Icons.Default.Phone,
                            label = "Телефон",
                            value = userViewModel.user.phone
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Кнопка выхода
                OutlinedButton(
                    onClick = {
                        userViewModel.logout()
                        Toast.makeText(context, "Вы вышли из профиля", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.Red
                    )
                ) {
                    Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Выйти")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Выйти")
                }
            }
        } else {
            // Форма регистрации
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Регистрация",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Заполните данные для оформления заказов",
                    fontSize = 14.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Поле имени
                OutlinedTextField(
                    value = userViewModel.nameInput,
                    onValueChange = { userViewModel.updateNameInput(it) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Имя") },
                    placeholder = { Text("Введите ваше имя") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Поле email
                OutlinedTextField(
                    value = userViewModel.emailInput,
                    onValueChange = { userViewModel.updateEmailInput(it) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Email") },
                    placeholder = { Text("example@mail.ru") },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Поле телефона
                OutlinedTextField(
                    value = userViewModel.phoneInput,
                    onValueChange = { userViewModel.updatePhoneInput(it) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Телефон") },
                    placeholder = { Text("+7 (999) 123-45-67") },
                    leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true
                )

                // Ошибка регистрации
                if (userViewModel.registrationError != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = userViewModel.registrationError!!,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Кнопка регистрации
                Button(
                    onClick = {
                        val success = userViewModel.register()
                        if (success) {
                            Toast.makeText(context, "Регистрация успешна!", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Зарегистрироваться", fontSize = 18.sp)
                }
            }
        }
    }
}