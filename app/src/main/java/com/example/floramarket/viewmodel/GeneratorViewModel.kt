package com.example.floramarket.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.floramarket.network.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GeneratorViewModel : ViewModel() {
    private val apiKey = "###" //здесь мой ключ
    private val TAG = "GeneratorViewModel"

    var generatedName by mutableStateOf("")
        private set

    var isLoading by mutableStateOf(false)
        private set

    var error by mutableStateOf<String?>(null)
        private set

    fun generateName(flowerDescription: String) {
        isLoading = true
        error = null

        Log.d(TAG, "Начинаем генерацию для: $flowerDescription")


        // Формируем сообщение для модели (новый формат)
        val prompt = """
            Придумай красивое название для букета цветов.
            
            Состав букета: $flowerDescription
            
            Название должно быть поэтичным, романтичным, оригинальным, из 1-3 слов, но не больше.
            Название должно получиться продающим, чтобы покупателю понравилось и он заинтересовался.
            Всего названий должно быть ровно 5, каждое на новой строке.
            Твой ответ должен состоять только из списка названий, без цифр, без кавычек, без маркеров.
            Не пиши никаких объяснений, пояснений, вступлений или заключений.
            Просто 5 строк с названиями.
        """.trimIndent()

        val request = ChatCompletionRequest(
            model = "deepseek-ai/DeepSeek-V3:fastest",
            messages = listOf(
                ChatMessage(
                    role = "user",
                    content = prompt
                )
            ),
            max_tokens = 100,
            temperature = 0.8
        )

        Log.d(TAG, "Отправляем запрос: model=${request.model}, max_tokens=${request.max_tokens}")

        viewModelScope.launch {
            try {
                val result = withContext(Dispatchers.IO) {
                    RetrofitClient.instance.generateName(
                        "Bearer $apiKey",
                        request
                    )
                }

                Log.d(TAG, "Получен ответ от API")
                Log.d(TAG, "Количество choices: ${result.choices.size}")

                if (result.choices.isNotEmpty()) {
                    generatedName = result.choices[0].message.content.trim()
                    Log.d(TAG, "Контент ответа: '$generatedName'")
                } else {
                    Log.e(TAG, "Список названий пуст!")
                    error = "Модель не вернула результат"
                }
            } catch (e: Exception) {
                Log.e(TAG, "Ошибка: ${e.message}", e)
                error = "Ошибка сети: ${e.message}"
            } finally {
                isLoading = false
                Log.d(TAG, "Генерация завершена, isLoading=$isLoading")
            }
        }
    }

    fun clearError() {
        error = null
    }
}