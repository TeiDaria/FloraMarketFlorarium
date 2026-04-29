package com.example.floramarket.create

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.floramarket.model.BouquetDraft
import com.example.floramarket.create.CreateBouquetViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateBouquetScreen(
    viewModel: CreateBouquetViewModel = viewModel(),
    onNavigateBack: () -> Unit,
    onBouquetCreated: (BouquetDraft) -> Unit
) {
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Создание букета") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Назад"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Фото букета",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    var showImagePicker by remember { mutableStateOf(false)}

                    rememberImagePicker(
                        showPicker = showImagePicker,
                        onImageSelected = { uri ->
                            viewModel.addImage(uri.toString())
                        },
                        onPickerDismissed = {
                            showImagePicker = false
                        }
                    )

                    AddImageButton(
                        onClick = { showImagePicker = true}
                    )
                }

                items(viewModel.imageUrls,
                    key = {url -> url.hashCode().toString() + viewModel.imageUrls.indexOf(url)}
                ) { imageUrl ->
                    ImagePreviewCard(
                        imageUrl = imageUrl,
                        onRemove = { viewModel.removeImage(imageUrl) }
                    )
                }
            }

            if (viewModel.imageUrls.isEmpty()) {
                Text(
                    text = "Добавьте хотя бы одно фото",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            Text(
                text = "Короткое описание для генерации названия",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )

            Text(
                text = "Опишите состав букета кратко, например: красные розы и желтые тюльпаны",
                fontSize = 12.sp,
                color = Color.Gray
            )

            OutlinedTextField(
                value = viewModel.shortDescription,
                onValueChange = { viewModel.updateShortDescription(it) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Красные розы и желтые тюльпаны...") },
                maxLines = 2
            )

            // Кнопка генерации названия
            Button(
                onClick = { viewModel.generateName() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                enabled = viewModel.shortDescription.isNotBlank() && !viewModel.isGeneratingName,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                )
            ) {
                if (viewModel.isGeneratingName) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Генерация...")
                } else {
                    Text("Сгенерировать название")
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            Text(
                text = "Название букета",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )

            OutlinedTextField(
                value = viewModel.name,
                onValueChange = { viewModel.updateName(it) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Введите или сгенерируйте название") },
                singleLine = true
            )

            Text(
                text = "Красивое описание от продавца",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )

            OutlinedTextField(
                value = viewModel.fullDescription,
                onValueChange = { viewModel.updateFullDescription(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                placeholder = {
                    Text("Напишите, что покупателям стоит знать о вашем букете")
                },
                maxLines = 5
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Цена (₽)",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )

                    OutlinedTextField(
                        value = viewModel.priceInput,
                        onValueChange = { viewModel.updatePrice(it) },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("до 1 000 000") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        leadingIcon = { Text("₽") }
                    )

                    if (viewModel.priceInput.toDoubleOrNull()?.let { it > 1_000_000 } ?: false) {
                        Text(
                            text = "Максимум 1 000 000 ₽",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "В наличии (шт)",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )

                    OutlinedTextField(
                        value = viewModel.quantityInput,
                        onValueChange = { viewModel.updateQuantity(it) },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("до 10 000") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )

                    if (viewModel.quantityInput.toIntOrNull()?.let { it > 10_000 } ?: false) {
                        Text(
                            text = "Максимум 10 000 шт.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    viewModel.publishBouquet(
                        onSuccess = { draft ->
                            onBouquetCreated(draft)
                            viewModel.resetForm()
                        },
                        onError = { error ->
                            // TODO: Показать ошибку
                        }
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = viewModel.isValid(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    text = "Опубликовать букет",
                    fontSize = 18.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun ImagePreviewCard(
    imageUrl: String,
    onRemove: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(120.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFF5F5F5))
            .border(1.dp, Color.LightGray, RoundedCornerShape(12.dp))
    ) {
        if (imageUrl.isNotEmpty() && imageUrl.startsWith("file://") || imageUrl.startsWith("content://") || imageUrl.startsWith("http")){
            AsyncImage(
                model = imageUrl,
                contentDescription = "Фото букета",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            // заглушка
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("🌸", fontSize = 40.sp)
            }
        }


        // Кнопка удаления
        IconButton(
            onClick = onRemove,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(32.dp)
        ) {
            Icon(
                Icons.Default.Close,
                contentDescription = "Удалить",
                tint = Color.Red
            )
        }
    }
}

@Composable
fun AddImageButton(
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(120.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFF5F5F5))
            .border(2.dp, Color.Gray, RoundedCornerShape(12.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                Icons.Default.Add,
                contentDescription = "Добавить фото",
                tint = Color.Gray,
                modifier = Modifier.size(40.dp)
            )
            Text(
                text = "Добавить",
                fontSize = 14.sp,
                color = Color.Gray
            )
        }
    }
}