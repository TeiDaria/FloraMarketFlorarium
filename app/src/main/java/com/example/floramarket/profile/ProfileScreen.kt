package com.example.floramarket.profile

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.floramarket.create.rememberMultipleImagePicker
import com.example.floramarket.viewmodel.UserViewModel


fun formatPhoneForDisplay(phone: String): String {
    val digits = phone.filter { it.isDigit() }
    return buildString {
        if (digits.isNotEmpty()) append("+7")
        if (digits.length > 1) append(" (${digits.substring(1, minOf(4, digits.length))}")
        if (digits.length >= 5) append(") ${digits.substring(4, minOf(7, digits.length))}")
        if (digits.length >= 8) append("-${digits.substring(7, minOf(9, digits.length))}")
        if (digits.length >= 10) append("-${digits.substring(9, minOf(11, digits.length))}")
    }
}


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
                title = { Text("Профиль") },
                actions = {
                    if (userViewModel.userExists && !userViewModel.isEditing) {
                        IconButton(onClick = { userViewModel.startEditing() }) {
                            Icon(Icons.Default.Edit, contentDescription = "Редактировать")
                        }
                    }
                }
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
        if (userViewModel.showExistingAccountDialog) {
            AlertDialog(
                onDismissRequest = { userViewModel.cancelExistingAccount() },
                title = { Text("Аккаунт найден") },
                text = {
                    Text(
                        "У вас уже есть аккаунт с таким email. Хотите войти в него?"
                    )
                },
                confirmButton = {
                    TextButton(onClick = {
                        userViewModel.confirmExistingAccount()
                        Toast.makeText(context, "Вход выполнен успешно!", Toast.LENGTH_SHORT).show()
                    }) {
                        Text("Войти")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { userViewModel.cancelExistingAccount() }) {
                        Text("Отмена")
                    }
                }
            )
        }

        if (userViewModel.userExists && !userViewModel.isEditing) {
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
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFF5F5F5)),
                    contentAlignment = Alignment.Center
                ) {
                    if (userViewModel.avatarUrl.isNotEmpty()) {
                        AsyncImage(
                            model = userViewModel.avatarUrl,
                            contentDescription = "Аватар",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = "null",
                            modifier = Modifier.size(60.dp),
                            tint = Color.Gray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

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
                            value = formatPhoneForDisplay(userViewModel.user.phone)
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
                    text = if (userViewModel.isEditing) "Редактирование"
                    else if (userViewModel.userExists) "Вход в профиль"
                    else "Регистрация",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = if (userViewModel.userExists)
                        "Введите данные для входа"
                    else
                        "Заполните данные для оформления заказов",
                    fontSize = 14.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Аватар
                var showAvatarPicker by remember { mutableStateOf(false) }

                Box(
                    modifier = Modifier.size(120.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFF5F5F5))
                            .clickable { showAvatarPicker = true },
                        contentAlignment = Alignment.Center
                    ) {
                        if (userViewModel.avatarUrl.isNotEmpty()) {
                            AsyncImage(
                                model = userViewModel.avatarUrl,
                                contentDescription = "Аватар",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = "Добавить фото",
                                modifier = Modifier.size(60.dp),
                                tint = Color.Gray
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.CameraAlt,
                            contentDescription = "Сменить фото",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                if (showAvatarPicker) {
                    rememberMultipleImagePicker(
                        showPicker = showAvatarPicker,
                        onImageSelected = { uris ->
                            if (uris.isNotEmpty()) {
                                userViewModel.updateAvatarUrl(uris.first())
                            }
                        },
                        onPickerDismissed = { showAvatarPicker = false }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

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
                    placeholder = { Text("+7 999 123 45 67") },
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

                // Кнопки
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (userViewModel.isEditing) {
                        OutlinedButton(
                            onClick = { userViewModel.cancelEditing() },
                            modifier = Modifier.weight(1f).height(56.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Отмена")
                        }
                    }

                    Button(
                        onClick = { userViewModel.onProfileSubmit() },
                        modifier = Modifier.weight(1f).height(56.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = if (userViewModel.isEditing) "Сохранить"
                            else if (userViewModel.userExists) "Войти"
                            else "Зарегистрироваться",
                            fontSize = 18.sp
                        )
                    }
                }
            }
        }
    }
}